package coolepicgaymer.gaymerspronouns.types;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.managers.PronounManager;

public class PronounSet {

    String display;
    String displaysecond;

    String subjective;
    String objective;
    String possessiveadj;

    /**
     * Creates a new set of pronouns with only a display.
     *
     * @param display How they should be displayed both when standalone and mixed (should work as just [display] && He/[display] && She/They/[display], etc.)
     *
     */
    public PronounSet(String display) {
        this.display = display;
        this.displaysecond = display;

        PronounManager manager = GaymersPronouns.getPronounManager();
        this.subjective = manager.getDefaultPronoun("subjective");
        this.objective = manager.getDefaultPronoun("objective");
        this.possessiveadj = manager.getDefaultPronoun("possessiveadj");
    }

    /**
     * Creates a new set of pronouns.
     *
     * @param display How they should be displayed when standalone (e.g. "He/Him", "She/Her", "They/Them", etc.).
     * @param displaysecond How they should be displayed when NOT standalone (i.e. should fit: He/[here], [here]/She/They, They/[here], etc.).
     *
     * @param subjective The subjective pronoun (e.g. "He", "She", "It", etc.)
     * @param objective The objective pronoun (e.g. "Her", "Them", "Him", etc.)
     * @param possessiveadj The possessive adj. pronoun (e.g. "Their", "Its", "His", etc.)
     *
     */
    public PronounSet(String display, String displaysecond, String subjective, String objective, String possessiveadj) {
        this.display = display;
        this.displaysecond = displaysecond;
        this.subjective = subjective;
        this.objective = objective;
        this.possessiveadj = possessiveadj;
    }



    public String getDisplay() {
        return display;
    }

    public String getSecondaryDisplay() {
        return displaysecond;
    }

    public String getSubjective() {
        return subjective;
    }

    public String getObjective() {
        return objective;
    }

    public String getPossessiveAdj() {
        return possessiveadj;
    }

}
