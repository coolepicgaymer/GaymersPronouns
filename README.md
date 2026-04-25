# Gaymer's Pronouns
A simple, yet very customizable Minecraft plugin that adds pronouns.

Pick your pronouns with a fancy looking GUI, and display them in any way you want it. Natively supports putting it in chat or the tablist. Can also be put anywhere else by using PlaceholderAPI.

There are a whole load of comments in the config file, making the configuration easy, even if you're a beginner.



## Quick shortcuts

<details>

<summary>Default config.yml</summary>

```yaml
#------------------------------------------------------------------#
#
#      Welcome to the GaymersPronouns configuration file.
#
#      Thank you for making your server more inclusive.
#
#------------------------------------------------------------------#
# I've tried my best to explain everything as you go, using these comments.
# (Can be used with PlaceholderAPI: %GaymersPronouns_pronouns% and others. Get more info with /gp papi.)

#
# Which language you want to use. Currently available: EN (english) or DA (danish)
# Keep in mind, you can also modify the messages.yml yourself.
# It may seem large, but only the top half is necessary. The bottom half is only visible to admins and the console (which you probably don't need since you can read this).
#
# Note: You may want to reset the pronouns.yml and messages.yml and generate new ones in the new language.
# This can be done by deleting or renaming the files and reloading the plugin or restarting the server.
# You can also reset them by changing languages in-game using /gp set language [locale].
#
locale: EN

#
# You can disable in-game configuration with the admin command by setting this to true.
# It's still possible to use to command to reload the plugin and assigning pronouns.
#
# All config changes are logged in the config.
#
# Note: Experimental Mode must be enabled for in-game configuration.
#
disable-in-game-configuration: false

#
# Whether you want to log pronoun changes in the console.
#
log-pronoun-changes: false

#
# These is the default pronoun set ID. You can see IDs in-game with "/gp pronouns" or in the pronouns.yml file.
# They will ONLY be used when absolutely needed, and when the player doesn't have pronouns set.
#
# By default (in English):
# They/They is 3
# She/Her is 2
# He/Him is 1
#
# Check pronouns.yml for more info.
#
default-pronouns: 3

#
# Whether placeholders should randomly pick which preferred pronoun set to use (true) or always use the top priority (false).
#
# Note: This can be mildly funky at times when using a pronoun and a verb at the same time. (e.g. %GaymersPronouns_Objective% %GaymersPronouns_verb%)
# I've done my best to make them match in most cases, but in rare cases with the right timing, they may not match. (e.g. "He are" instead of "He is")
#
random-priority-in-placeholders: false

#
# This is the inclusive maximum of pronouns a player can have when picking multiple. Set to 0 for no maximum (not recommended).
#
max-pronouns: 3

#
# Here, you can enable whether the pronoun selection menu should pop up immediately the first time someone joins the server.
#
prompt-on-first-join: false

#
# Whether you want to remind your players to pick their pronouns if they aren't set every time they log in. (Can be disabled in-game per player as well, this is just the default)
# True if you want reminders, false if you do not.
#
default-no-pronouns-reminder: true

#
# Whether you want to show the book with general info about pronouns in the pronoun menu for people who might not know.
#
show-tutorial-item: true

#
# Items shown in the menu
#
# Note: Further configuration can be done in messages.yml
#
gui:
  items:
    group1: NAME_TAG
    group2: RABBIT_FOOT
    selected: EMERALD_BLOCK
    reminder-enabled: LIME_DYE
    reminder-disabled: GRAY_DYE
    tutorial: BOOK
    glass-row:
      cancel: GRAY_STAINED_GLASS_PANE
      multiple-start: CYAN_STAINED_GLASS_PANE
      multiple-zero-selected: RED_STAINED_GLASS_PANE
      multiple-single-selected: YELLOW_STAINED_GLASS_PANE
      multiple-save: LIME_STAINED_GLASS_PANE

#
# This is where the pronouns will be displayed. Remove the hashtag in front of the ones you want.
# The format of how they're shown is edited further down, don't worry.
#
# Here's an explanation for them all:
# - chatformat: just shown in chat alongside someone's name, as a prefix, suffix, or whatever else.
# - chathover: shown when you hover over someone's name with your mouse after they send chat messages.
# - tablist: shows alongside someone's name as a prefix/suffix in the tab list.
#
# All of these require a restart to work.
#
display:
  #- chatformat
  #- chathover
  #- tablist

display-format:

  #
  # This is how chat messages will look ONLY if chatformat is enabled above.
  #
  # Good to know:
  # - Write "{PRONOUNS}" for it to be replaced with the player's pronouns.
  # - Write "{DISPLAYNAME}" for it to be replaced with the player's display name.
  # - Write "{USERNAME}" for it to be replaced with the player's username.
  # - Supports PlaceholderAPI.
  #
  # IMPORTANT:
  # - Write "{MESSAGE}" for it to be replaced with the message.
  #
  chat-format: "&7[{PRONOUNS}&7] &f<{DISPLAYNAME}&f> {MESSAGE}"

  #
  # This is how the little box will look when you hover over someone's name ONLY if chathover is enabled above.
  #
  # Good to know:
  # - Write "{PRONOUNS}" for it to be replaced with the player's pronouns.
  # - Write "{DISPLAYNAME}" for it to be replaced with the player's display name.
  # - Write "{USERNAME}" for it to be replaced with the player's username.
  # - Supports PlaceholderAPI.
  #
  # This is written as a list to make room for multiple lines.
  #
  chat-hover: ["&7This player's pronouns are &f{PRONOUNS}&7."]

  #
  # This is how someone's name will look in the tab list ONLY if tablist is enabled above.
  #
  # IMPORTANT:
  # - Write "{PRONOUNS}" for it to be replaced with the player's pronouns.
  # - Write "{DISPLAYNAME}" for it to be replaced with the player's display name.
  # - Write "{USERNAME}" for it to be replaced with the player's username.
  # - Supports PlaceholderAPI.
  #
  tab-list: "&7[{PRONOUNS}&7] &f{USERNAME}"

  #
  # Whether you want to update the tablist names every x seconds.
  # It will update on pronoun changes regardless. This is just for if you
  # use PlaceholderAPI and have placeholders that need updating. If you don't
  # have any placeholders, just leave this off and ignore it.
  #
  update-tablist-periodically: false
  tablist-update-delay: 60


#
# Whether you want to use pronoun-specific colors. When enabling, remember to edit your pronouns.yml to assign the colors as they are all white by default.
# Colors are assigned by setting 'color' to a color code, e.g. &f or &c. You can see the default pronoun (3 by default) as it's the only one with an assigned color out of the box.
#
use-individual-colors: false

#
# When using individual colors, what color should the slash separating the pronouns be? Ignored if use-individual-colors is false. Leave empty ("") to let
# the pronoun color itself continue into the separator.
#
# Note: This is not used when only one pronoun is selected (e.g. He/Him) but will only be used when combining multiple sets (e.g. He/They).
#
individual-separator-color: "&7"

#
# In case you want to use an SQL database. I won't provide much information for this one since if you want to use this, you probably know what you're doing.
#
# NOTICE: In order to accommodate any multi-language situations, the pronouns.yml remain local, so if you want identical pronouns everywhere, you must copy it for now.
# I will most likely allow for having the pronoun sets in the database, just not in this update. So, ignore the "local-pronouns" setting as it is completely unused as of
# right now but will likely be utilized in the future. It is only there so you don't have to update the config in the future.
#
# If you don't know what this is, just ignore all of this and keep "use" as "false".
#
database:
  use: false
  local-pronouns: false # Unused. Read above.
  url: "jdbc:mysql://localhost/DatabaseName"
  user: "root"
  password: ""

#
# Do NOT change this or you may lose your config.
#
version: 3

#
# Enable experimental features?
#
developer-mode: false
```

</details>

<details>

<summary>Default pronouns.yml (english)</summary>

```yaml
#
# This is what will be displayed when someone has not set their pronouns.
#
undefined-pronouns: "Not set"

# ---------------------------------------- #
#
# How do I add more pronouns?
#
# You can add more pronouns simply by doing some copy pasting or editing the existing ones.
#
# You can also easily make new ones in-game using the "/gp pronouns" command if you find this overwhelming.
#
#
#
# Here's the format:
#                               This is the pronoun ID. It's not visible to players in-game and is only used for saving purposes. This also
#                               means that if you were to edit or swap around these numbers below, that players with those pronouns would get
#  1:                       <-- different pronouns assigned to them, so don't do that. Note that the pronouns are shown in the order of
#                               their IDs from low to high. Positive numbers are shown on the left side of the pronoun picker, and negative
#                               numbers on the right side. Do not use 0 as it is reserved.
#
#   hidden: false           <-- Whether you want to hide it from the picker menu for some reason. It can still be manually assigned by admins.
#
#   color: "&f"             <-- The color used when "use-individual-colors" is true in the config. Defaults to "&f" or the one you set for your
#                               default pronoun (3 by default) if not set. Remember to use quotes to avoid issues.
#
#   display: "They/Them"    <-- This is how it's displayed when it's the only picked pronouns of a player. This is the only required attribute.
#
#   dominant: "They"        <-- This is the dominant pronoun used when it's in a set of multiple pronouns (e.g. She/They). If not specified, the first
#                               part of the display attribute will be used (in this case, "They", which would make this redundant, hence its rare use).
#
#   subjective: "They"      <-- The subjective pronoun. (e.g. He, She, They)
#   objective: "Them"       <-- The objective pronoun. (e.g. Him, Her, Them)
#   possessive: "Their"     <-- The possessive pronoun. (e.g. His, Her, Their)
#   reflexive: "Themselves" <-- The reflexive pronoun. (e.g. Himself, Herself, Themselves)
#
#   verb: "Are"             <-- The verb associated with the pronoun (e.g. "Is", "Are" to be used in placeholders like "He *is*", "They *are*").
#
#
#
#   This seems like a lot. But if you don't use PlaceholderAPI (which most probably don't), you only really need the ID and the display.
#   In some cases the 'dominant' is also useful when the first pronoun in the x/y format isn't the one you want displayed when multiple sets are shown together.
#   Hiding pronouns may be useful if a set of pronouns is being abused somehow (can still be assigned with /gp assign).
#
# ---------------------------------------- #

pronouns:

  #
  # Negative numbers will be shown separately from 1 and above (on the right side of the menu).
  #
  -1:
    display: "Any"
  -2:
    display: "Ask"
  -3:
    display: "None"
    subjective: "This player"
    objective: "That player"
    possessive: "This player's"
    reflexive: "The player"
    verb: "Is"
    hidden: true

  #
  #  These will be shown on the left side of the menu.
  #
  1:
    display: "He/Him"
    subjective: "He"
    objective: "Him"
    possessive: "His"
    reflexive: "Himself"
    verb: "Is"
  2:
    display: "She/Her"
    subjective: "She"
    objective: "Her"
    possessive: "Her"
    reflexive: "Herself"
    verb: "Is"
  3:
    color: "&f"
    display: "They/Them"
    subjective: "They"
    objective: "Them"
    possessive: "Their"
    reflexive: "Themselves"
    verb: "Are"
  4:
    display: "It/Its"
    subjective: "It"
    objective: "It"
    possessive: "Its"
    reflexive: "Itself"
    verb: "Is"

  # There's not any deep reason for skipping 5 here. It's just because some languages have more pronouns so you can change between them without messing up your players' saved pronouns or having a weird order of displaying them in.

  6:
    display: "Xe/Xem"
    subjective: "Xe"
    objective: "Xem"
    possessive: "Xyr"
    reflexive: "Xemself"
    verb: "Is"
  7:
    display: "Ze/Zir"
    subjective: "Ze"
    objective: "Zir"
    possessive: "Zirs"
    reflexive: "Zirself"
    verb: "Is"
  8:
    display: "Ey/Em"
    subjective: "Ey"
    objective: "Em"
    possessive: "Eirs"
    reflexive: "Emself"
    verb: "Is"
```

</details>

<details>

<summary>Default messages.yml (english)</summary>

```yaml
version: 3 # Do NOT change this unless you know what you're doing or have been specifically instructed to do so

no-pronouns-reminder: "&cYou don't have any pronouns set. &7Click here to set them now."
fluid-reminder: "&7Your pronouns are currently set to &c{0}&7. Click here to change them, or ignore this to keep them."
change-pronouns-hover: "&7Click here to change your pronouns now."
pronouns-menu: "&7Pick your pronouns"
pronoun-reminder-name: "&cToggle Pronoun Reminder"
pronoun-reminder-lore:
  - "&7Click here to {0} pronoun reminders."
  - ""
  - "&7Enabling this will remind you what your"
  - "&7current pronouns are, every time you join, so"
  - "&7you can quickly change them."
cancel-name: "&cCancel"
cancel-lore:
  - "&7Click here to cancel."
previous-page-name: "&cPrevious Page"
next-page-name: "&cNext Page"
toggle-fluid-reminders: "&7Pronoun reminders have now been {0}."
pronouns-set: "&7Your pronouns have been set to &c{0}&7. You can change them at any time with &f/pronouns&7."
pick-above: "&cPick your pronouns above"
initialize-multiple-name: "&cPick Multiple"
initialize-multiple-lore:
  - "&7Click here to pick multiple pronouns."
not-enough-multiple-name: "&cNot enough..."
not-enough-multiple-lore:
  - "&7You must pick at least one to continue."
confirm-multiple-name: "&cConfirm"
confirm-multiple-lore:
  - "&7Click here to confirm your pick of pronouns."
max-pronouns-reached: "&7Sorry, but you can't pick more than {0} pronouns."
pronoun-chat-hover:
  - "&fThis player's pronouns are {0}."
player-not-online: "&cThis player is not online right now."
pronouns-other: "&7Pronouns of &f{0}&7 are &f{1}&7."
info-item-name: "&cWhat are pronouns?"
info-item-lore:
  - "&7Pronouns are what people use to refer to you"
  - "&7in third person."
  - ""
  - "&7Here are some examples:"
  - "&8- &7&nHe&7 is very cool. I like &nhim&7."
  - "&8- &7&nShe&7 is the best! There's nothing bad about &nher&7."
  - "&8- &7&nThey&7 gave me a gift. Very thoughtful of &nthem&7."
  - ""
  - "&7How would you like to be referred to?"
  - ""
  - "&7Note: &fYou can pick multiple by clicking below."
misc:
  enabled: "enabled"
  disabled: "disabled"
  enable: "enable"
  disable: "disable"



# ---------- Admin commands ----------
#
# If you're translating this manually, you won't need to edit further unless you want to translate the admin messages, too.
#
# ------------------------------------

console:
  reload: "Reloading the plugin... (Initiated by {0}, took {1} ms)"
  assigned-new: "{0} assigned the pronouns {1} for {2}."
  assigned-changed: "{0} assigned the pronouns {1} for {2} (Previously {3})."
  unassigned: "{0} unassigned pronouns of {1} (Previously {2})."
  picked-new: "Own pronouns changed by {0} to {1}."
  picked-changed: "Own pronouns changed by {0} to {1} (Previously {2})."
  fluid-reminders-toggled: "Own fluid reminders {0} by {1}."
  invalid-file: "Invalid or outdated {0}. Generating a new one and reloading..."
  language-files-reset: "In order to switch languages, messages.yml and pronouns.yml have been reset. In case you made edits to them, they are still in the plugin folder."
  changed-config: "Config value {0} changed by {1} from \"{2}\" to \"{3}\"."
  edited-pronouns: "Pronouns with ID {0} changed by {1} ({2} -> {3})."
  created-pronouns: "Pronouns with ID {0} created by {1} ({2})."
  deleted-pronouns: "Pronouns with ID {0} deleted by {1} ({2})."
  pronoun-format: "{0}/{1}/{2}/{3}, {4}"
  players-only: "This can only be done by in-game players."
  sql:
    connected: "Connected to SQL database successfully."
    not-connected: "Failed to connect to SQL database."
    query-error: "Unable to execute SQL query."
    table-creation-error: "Unable to create table in SQL database."
    player-load-error: "Could not load player {0} from SQL database. Using defaults for now."
    sql-update-error: "Failed to update data in SQL database."
    migration-started: "A player data migration was started by {0}."
    migration-success: "Migration of player data was successful."
    migration-error: "Migration of player data failed."
    generic-error: "There was an error with the SQL database."
assignments:
  assigned-new: "&7Assigned the pronouns &f{0}&7 for &f{1}&7."
  assigned-changed: "&7Assigned the pronouns &f{0}&7 for &f{1}&7 (Previously &f{2}&7)."
  unassigned: "&7Unassigned pronouns of &f{0}&7 (Previously &f{1}&7)."
  no-assigned-pronouns: "&f{0}&7 does not have any assigned pronouns."
  unrecognized-pronouns: "&7Unrecognized pronouns: &f\"{0}\""
  name-required: "&7You must specify a username."
  pronouns-required: "&7You must specify which pronouns to assign."
configuration:
  reload: "&aReloaded GaymersPronouns. &7(Took {0} ms)"
  no-permission: "&cInsufficient permission."
  other:
    no-value: "&7You must type a value you want to edit."
    invalid-value: "&7This is not a valid value to edit."
    not-a-number: "&7This value must be a number."
    not-a-boolean: "&7This value must be &ftrue&7 or &ffalse&7."
    not-a-language: "&7This language is not available."
    changed-value: "&7Changed the value &f{0}&7 from &f{1}&7 to &f{2}&7."
    language-files-reset: "&cYour language and pronoun configuration files have been reset to change languages. In case you made edits to them, they are still in the plugin folder."
  toggles:
    chatformat: "&7Chat format is &f{0}&7 as an active display type."
    chathover: "&7Chat hovering is &f{0}&7 as an active display type."
    tablist: "&7Tablist is &f{0}&7 as an active display type."
    randomize-pronouns: "&7Randomizing pronoun priorities in placeholders is now &f{0}&7."
    requires-reload: "&fNote: &7Reload with &f/gp reload&7 or restart to see changes."
  display-changes:
    must-contain-username: "&cYou must include a username or display name. &7You can do this with &f{USERNAME}&7 or &f{DISPLAYNAME}&7 respectively. Note that they must be capitalised."
    example-unavailable: "&7Unavailable from console."
    format-disabled: "&7Note: &fThis format type is currently disabled."
    chatformat:
      - "&aSuccessfully changed the chat format."
      - "&7Example: &f{0}"
    chathover:
      - "&aSuccessfully changed the hover format."
      - "&7Example: &f{0}"
    tablist:
      - "&aSuccessfully changed the tablist format."
      - "&7Example: &f{0}"
  instructions:
    chatformat:
      - "&cHow to edit the chat format"
      - "&7Use &f/gp format chat <text>"
      - "&7Type what you what you want it to say when people speak in chat. There are certain things you can type that will be replaced with variables such as names or pronouns."
      - "&fList of variables (must be capitalized):"
      - "&8- &f{USERNAME}&8: &7Just the player's name."
      - "&8- &f{DISPLAYNAME}&8: &7Player's display name (i.e. including prefix etc.)."
      - "&8- &f{PRONOUNS}&8: &7Player's picked pronouns. Displays {0} if not specified."
      - "&8- &f{MESSAGE}&8: &7Chat message (added automatically at the end when absent)."
      - "&8- &fAny PlaceholderAPI placeholders &7(if installed)."
      - "&7Note: &fEither username or display name is required."
    chathover:
      - "&cHow to edit the hover format"
      - "&7Use &f/gp format hover <text>"
      - "&7Type what you what you want to display when hovering over a name or message in chat. There are certain things you can type that will be replaced with variables such as names or pronouns."
      - "&fList of variables (must be capitalized):"
      - "&8- &f{USERNAME}&8: &7Just the player's name."
      - "&8- &f{DISPLAYNAME}&8: &7Player's display name (i.e. including prefix etc.)."
      - "&8- &f{PRONOUNS}&8: &7Player's picked pronouns. Displays {0} if not specified."
      - "&8- &fAny PlaceholderAPI placeholders &7(if installed)."
      - "&7Note: &fDoing this in the config allows for multiple lines."
    tablist:
      - "&cHow to edit the tablist format"
      - "&7Use &f/gp format tablist <text>"
      - "&7Type what you what you want the tab-list to say for each player. There are certain things you can type that will be replaced with variables such as names or pronouns."
      - "&fList of variables (must be capitalized):"
      - "&8- &f{USERNAME}&8: &7Just the player's name."
      - "&8- &f{DISPLAYNAME}&8: &7Player's display name (i.e. including prefix etc.)."
      - "&8- &f{PRONOUNS}&8: &7Player's picked pronouns. Displays {0} if not specified."
      - "&8- &fAny PlaceholderAPI placeholders &7(if installed)."
      - "&7Note: &fEither username or display name is required."
    format-editor:
      - "&cHow to edit formats"
      - "&7You can edit the display format for the chat, chat hovering, or the tab list using this command. For further instructions on each of them, specify which type you want to edit:"
      - "&8- &f/{0} format chat &7<text>&8: &fEdit the chat display format (when enabled)."
      - "&8- &f/{0} format hover &7<text>&8: &fEdit the chat hovering display format (when enabled)."
      - "&8- &f/{0} format tablist &7<text>&8: &fEdit the tablist display format (when enabled)."
    papi:
      - "&cHow to use PlaceholderAPI"
      - "&7You can use PlaceholderAPI to display a player's preferred set of pronouns or get a specific type of pronoun to refer to them in a given context."
      - ""
      - "&cList of placeholders:"
      - "&8- &f%GaymersPronouns_pronouns%&8: &7Preferred pronouns."
      - "&8- &f%GaymersPronouns_subjective%&8: &7Subjective (e.g. They)."
      - "&8- &f%GaymersPronouns_objective%&8: &7Objective (e.g. Them)."
      - "&8- &f%GaymersPronouns_possessive%&8: &7Possessive (e.g. Their)."
      - "&8- &f%GaymersPronouns_reflexive%&8: &7Reflexive (e.g. Themselves)."
      - "&8- &f%GaymersPronouns_verb%&8: &7Verb (e.g. Are)."
      - "&7Note: &fCapitalize the first letter to capitalize the result (e.g. %GaymersPronouns_Subjective% -> They, %GaymersPronouns_subjective% -> they)"
    papi-not-installed:
      - ""
      - "&7Note: &cPlaceholderAPI is not installed."
  gui:
    name: "&8Configure GaymersPronouns"
    pronouns:
      enter: "&cPronoun Settings"
      enter-lore:
        - "&7Summary:"
        - "&8- &7Max Pronouns&8: &f{0}"
        - "&8- &7Default Pronouns&8: &f{0}&8/&f{1}&8/&f{2}&8/&f{3} &7({4})"
        - "&8- &7Pronouns List&8: &fClick for info"
        - "&8- &7Prompt on First Join&8: &f{0}"
        - "&8- &7Remind Unset Pronouns&8: &f{0}"
        - ""
        - "&7Click to edit pronoun settings."
    display:
      enter: "&cDisplay Settings"
      enter-lore: ["&7See below for display settings."]
      chatformat: "&cChat Messages"
      chatformat-lore:
        - "&7Enabled: &f{0}"
        - ""
        - "&7Current Format:"
        - ""
        - "&f{1}"
        - ""
        - "&7Left click to toggle."
        - "&7Right click for info on how to change the format."
      chathover: "&cChat Hovering"
      chathover-lore:
        - "&7Enabled: &f{0}"
        - ""
        - "&7Current Format:"
        - ""
        - "&f{1}"
        - ""
        - "&7Left click to toggle."
        - "&7Right click for info on how to change the format."
      tablist: "&cTablist"
      tablist-lore:
        - "&7Enabled: &f{0}"
        - ""
        - "&7Current Format:"
        - ""
        - "&f{1}"
        - ""
        - "&7Left click to toggle."
        - "&7Right click for info on how to change the format."
    placeholderapi:
      installed: "&cHow to use PlaceholderAPI"
      installed-lore:
        - "&7Optionally, you can use PlaceholderAPI to"
        - "&7display either specific pronouns, or the set of"
        - "&7preferred pronouns for a player."
        - ""
        - "&7Click here for a list of placeholders."
      not-installed: "&cPlaceholderAPI"
      not-installed-lore:
        - "&7Not installed. Click for more info."
      randomize-priority: "&cRandomize Pronoun Priority"
      randomize-priority-lore:
        - "&7Choose whether you want to pick a random set"
        - "&7of preferred pronouns for a player when using"
        - "&7placeholders or if it should always use the"
        - "&7top priority with no variation."
        - ""
        - "&7Currently {0}&7."
        - ""
        - "&7Click here to toggle."
  misc:
    generic-message: "Hello"
    true: "&aYes"
    false: "&cNo"
    pass: "&aPass"
    fail: "&cFail"
  database:
    migration:
      confirm: "&fAre you sure you want to migrate all existing local player data to the database? This will overwrite any existing pronoun data in the database but keep the options data (fluid reminders, etc.) and may take a while. Type &c/{0} migratedata confirm&f to confirm. Also reloads the plugin when done. &cNot recommended for large amounts of players."
      start: "&fStarting an attempt to migrate existing local player data to the database. This may take a while if there is a lot..."
      success: "&aAll local player data has been migrated to the database successfully and the plugin was reloaded."
      error: "&cAn error occurred while migrating the player data to the database."
      not-enabled: "&cYou must enable use of the SQL database in the config.yml before migrating."
    test:
      start: "&fStarting SQL connection test..."
      setup: "&fTest 1 &7(connection): {0}"
      insert: "&fTest 2 &7(insert): {0}"
      update: "&fTest 3 &7(update): {0}"
      select: "&fTest 4 &7(select): {0}"
      delete: "&fTest 5 &7(delete): {0}"
      end: "&fEnded connection test."
  help: "&cHere's some help with commands:"
  help-assign: # Variables here don't increase from {0} to {1}, {2} etc. because it uses a different method for displaying it. All variables below in the help section should be {0}.
    - "&8- &7/{0} &fassign &8- &fAssign or change someone else's pronouns."
    - "&8- &7/{0} &funassign &8- &fUnassign pronouns for a player."
  help-reload: "&8- &7/{0} &freload &8- &fReload the plugin."
  help-configure:
    - "&8- &7/{0} &fconfigure &8- &fOpen the configuration menu."
    - "&8- &7/{0} &fset &8- &fSet a specific configuration value."
    - "&8- &7/{0} &fpronouns &8- &fAdd, remove, or edit pronouns."
    - "&8- &7/{0} &fformat &8- &fChange display formats (if enabled)."
    - "&8- &7/{0} &fpapi &8- &fInfo on how to use PlaceholderAPI (if installed)."
  help-database:
    - "&8- &7/{0} &fmigratedata&8- &fMigrates any local player data to the database."
    - "&8- &7/{0} &ftestsql&8- &fTests the connection to the SQL database."
```

</details>
