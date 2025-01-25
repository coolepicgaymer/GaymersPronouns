package coolepicgaymer.gaymerspronouns.types;

import java.util.List;

public class GPPlayer {
    private final String uuid;
    private List<Integer> pronouns;
    private boolean optOutReminders;
    private boolean fluidReminders;

    public GPPlayer(String uuid, List<Integer> pronouns, boolean optOutReminders, boolean fluidReminders) {
        this.uuid = uuid;
        this.pronouns = pronouns;
        this.optOutReminders = optOutReminders;
        this.fluidReminders = fluidReminders;
    }

    public String getUuid() {
        return uuid;
    }

    public List<Integer> getPronouns() {
        return pronouns;
    }

    public void setPronouns(List<Integer> pronouns) {
        this.pronouns = pronouns;
    }

    public boolean isOptOutReminders() {
        return optOutReminders;
    }

    public void setOptOutReminders(boolean optOutReminders) {
        this.optOutReminders = optOutReminders;
    }

    public boolean isFluidReminders() {
        return fluidReminders;
    }

    public void setFluidReminders(boolean fluidReminders) {
        this.fluidReminders = fluidReminders;
    }
}
