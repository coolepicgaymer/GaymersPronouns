package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.types.GPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final GaymersPronouns plugin;

    private String url;
    private String user;
    private String password;

    private Connection connection;
    long lastDbConnection;

    public DatabaseManager(GaymersPronouns plugin, String url, String user, String password) {
        this.plugin = plugin;

        reload(url, user, password);
    }

    public void reload(String url, String user, String password) {
        lastDbConnection = 0;
        this.url = url;
        this.user = user;
        this.password = password;

        connection = getConnection(true);
        setup();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(2)) connection.close();
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql.generic-error"));
            e.printStackTrace();
        }
    }

    public void test(CommandSender sender) {
        String uuid = "semiaquatic-egglaying-mammalofaction"; // So you've found my silly little fake uuid, huh... Don't blame me, I needed 36 characters and perry is cool.

        sender.sendMessage(MessageManager.getMessage("configuration.database.test.start"));
        sender.sendMessage(MessageManager.getMessage("configuration.database.test.setup", booleanToTestString(testConnection())));
        sender.sendMessage(MessageManager.getMessage("configuration.database.test.insert", booleanToTestString(testInsert(uuid))));
        sender.sendMessage(MessageManager.getMessage("configuration.database.test.update", booleanToTestString(testUpdate(uuid))));
        sender.sendMessage(MessageManager.getMessage("configuration.database.test.select", booleanToTestString(testSelect(uuid))));
        sender.sendMessage(MessageManager.getMessage("configuration.database.test.delete", booleanToTestString(testDelete(uuid))));
        sender.sendMessage(MessageManager.getMessage("configuration.database.test.end"));
    }

    private String booleanToTestString(boolean bool) {
        return bool ? MessageManager.getMessage("configuration.misc.pass") : MessageManager.getMessage("configuration.misc.fail");
    }

    private Connection getConnection(boolean force) {
        try {
            // Force a new connection if prompted or null, also check validity of longer than 10 minutes since last connection.
            if (force || connection != null || (lastDbConnection < System.currentTimeMillis()-(1000*60*10) && !connection.isValid(1))) connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql.not-connected"));
            e.printStackTrace();
        }

        lastDbConnection = System.currentTimeMillis();
        return connection;
    }

    private void setup() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Statement statement = getConnection(false).createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS player_pronouns(player char(36), pronoun_id int, priority int)");
                statement.execute("CREATE TABLE IF NOT EXISTS player_pronoun_options(player char(36) primary key, opt_out_reminders boolean, fluid_reminders boolean, username varchar(16))");
                //statement.execute("CREATE TABLE IF NOT EXISTS pronoun_sets(id int primary key, display varchar(32), dominant varchar(16), subjective varchar(16), objective varchar(16), possessive varchar(16), reflexive varchar(16), verb varchar(16), hidden boolean)");
            } catch (SQLException e) {
                plugin.getLogger().warning(MessageManager.getMessage("console.sql.table-creation-error"));
                e.printStackTrace();
            }
        });
    }

    public GPPlayer createFullPlayerProfile(String uuid, GPPlayer defaults) {
        try (Connection connection = getConnection(false); PreparedStatement statementOptions = connection.prepareStatement("SELECT * FROM player_pronoun_options WHERE player = ?"); PreparedStatement statementPronouns = connection.prepareStatement("SELECT * FROM player_pronouns WHERE player = ? ORDER BY priority")){
            boolean optOutReminders;
            boolean fluidReminders;
            String username;

            statementOptions.setString(1, uuid);
            statementPronouns.setString(1, uuid);

            ResultSet resultOptions = statementOptions.executeQuery();
            ResultSet resultPronouns = statementPronouns.executeQuery();

            if (resultOptions.next()) {
                optOutReminders = (resultOptions.getInt(2) != 0);
                fluidReminders = (resultOptions.getInt(3) != 0);
                username = resultOptions.getString(4);
            } else {
                optOutReminders = defaults.isOptOutReminders();
                fluidReminders = defaults.isFluidReminders();
                username = defaults.getUsername();

                createPlayerOptionsEntry(defaults);
            }

            List<Integer> pronouns = new ArrayList<>();
            while (resultPronouns.next()) {
                pronouns.add(resultPronouns.getInt(2));
            }

            return new GPPlayer(uuid, username, pronouns, optOutReminders, fluidReminders);
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql.query-error"));
            e.printStackTrace();
        }

        return null;
    }

    public String tryGetOfflineUUID(String username) {
        try (PreparedStatement statementOptions = getConnection(false).prepareStatement("SELECT player FROM player_pronoun_options WHERE username = ?")) {
            String uuid = null;

            statementOptions.setString(1, username);

            ResultSet resultOptions = statementOptions.executeQuery();

            if (resultOptions.next()) uuid = resultOptions.getString(1);

            return uuid;
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql.query-error"));
            e.printStackTrace();
        }

        return null;
    }

    public void createPlayerOptionsEntry(GPPlayer defaults) throws SQLException {
        try (PreparedStatement statement = getConnection(false).prepareStatement("INSERT INTO player_pronoun_options(player, opt_out_reminders, fluid_reminders, username) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, defaults.getUuid());
            statement.setBoolean(2, defaults.isOptOutReminders());
            statement.setBoolean(3, defaults.isFluidReminders());
            statement.setString(4, defaults.getUsername());

            statement.executeUpdate();
        }
    }

    public boolean updatePlayerOptionsEntry(GPPlayer player) {
        try (PreparedStatement statement = getConnection(false).prepareStatement("UPDATE player_pronoun_options SET opt_out_reminders = ?, fluid_reminders = ? WHERE player = ?")) {
            statement.setBoolean(1, player.isOptOutReminders());
            statement.setBoolean(2, player.isFluidReminders());
            statement.setString(3, player.getUuid());

            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql-update-error"));
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean updateUsername(String uuid, String username) {
        try (PreparedStatement statement = getConnection(false).prepareStatement("UPDATE player_pronoun_options SET username = ? WHERE player = ?")) {
            statement.setString(1, username);
            statement.setString(2, uuid);

            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql-update-error"));
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean updatePlayerPronounsEntry(GPPlayer player) {
        try (PreparedStatement deleteStatement = getConnection(false).prepareStatement("DELETE FROM player_pronouns WHERE player = ?")) {
            deleteStatement.setString(1, player.getUuid());
            deleteStatement.executeUpdate();

            List<Integer> pronouns = player.getPronouns();
            for (int i = 1; i < pronouns.size()+1; i++) {
                try (PreparedStatement statement = getConnection(false).prepareStatement("INSERT INTO player_pronouns(player, pronoun_id, priority) VALUES (?, ?, ?)")) {
                    statement.setString(1, player.getUuid());
                    statement.setInt(2, pronouns.get(i-1));
                    statement.setInt(3, i);

                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql-update-error"));
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<Integer> getPronouns(String uuid) {
        List<Integer> pronouns = new ArrayList<>();

        try (PreparedStatement statement = getConnection(false).prepareStatement("SELECT * FROM player_pronouns WHERE player = ?")) {
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();

            while (result.next()) pronouns.add(result.getInt(2));
        } catch (SQLException e) {
            plugin.getLogger().warning(MessageManager.getMessage("console.sql.generic-error"));
            e.printStackTrace();
        }

        return pronouns;
    }

    private boolean testConnection() {
        try {
            if (connection == null || connection.isClosed()) connection = DriverManager.getConnection(url, user, password);
            if (connection != null && !connection.isClosed()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean testDelete(String uuid) {
        try (PreparedStatement deleteStatement = getConnection(false).prepareStatement("DELETE FROM player_pronouns WHERE player = ?")) {
            deleteStatement.setString(1, uuid);
            deleteStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean testInsert(String uuid) {
        try (PreparedStatement statement = getConnection(false).prepareStatement("INSERT INTO player_pronouns(player, pronoun_id, priority) VALUES (?, ?, ?)")) {
            statement.setString(1, uuid);
            statement.setInt(2, -1);
            statement.setInt(3, 2);

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean testUpdate(String uuid) {
        try (PreparedStatement statement = getConnection(false).prepareStatement("UPDATE player_pronouns SET pronoun_id = ?, priority = ? WHERE player = ?")) {
            statement.setInt(1, 1);
            statement.setInt(2, 1);
            statement.setString(3, uuid);

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean testSelect(String uuid) {
        try (PreparedStatement statement = getConnection(false).prepareStatement("SELECT * FROM player_pronouns WHERE player = ? ORDER BY priority")) {
            statement.setString(1, uuid);

            ResultSet response = statement.executeQuery();

            boolean result = response.next();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
