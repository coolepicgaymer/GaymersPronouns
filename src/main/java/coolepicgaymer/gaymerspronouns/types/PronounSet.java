package coolepicgaymer.gaymerspronouns.types;

public class PronounSet {

    String display;
    String dominant;

    String subjective;
    String objective;
    String possessive;
    String reflexive;

    String verb;

    boolean hidden;

    /**
     * Creates a new set of pronouns.
     *
     * @param display How they should be displayed when standalone (e.g. "He/Him", "She/Her", "They/Them", etc.).
     * @param dominant How they should be displayed when NOT standalone (i.e. should fit: He/[here], [here]/She/They, They/[here], etc.).
     *
     * @param subjective The subjective pronoun (e.g. "He", "She", "It", etc.)
     * @param objective The objective pronoun (e.g. "Her", "Them", "Him", etc.)
     * @param possessive The possessive pronoun (e.g. "Their", "Its", "His", etc.)
     * @param reflexive The reflexive pronoun (e.g. "Themselves", "Himself", "Herself", etc.)
     * @param verb The verb associated with the pronoun (e.g. "Is", "Are", etc.)
     *
     */
    public PronounSet(String display, String dominant, String subjective, String objective, String possessive, String reflexive, String verb, boolean hidden) {
        this.display = display;

        if (dominant != null) this.dominant = dominant;
        else this.dominant = display.split("/")[0];

        this.subjective = subjective;
        this.objective = objective;
        this.possessive = possessive;
        this.reflexive = reflexive;

        this.verb = verb;

        this.hidden = hidden;
    }



    public String getDisplay() {
        return display;
    }

    public String getDominant() {
        return dominant;
    }

    public String getSubjective() {
        return subjective;
    }

    public String getObjective() {
        return objective;
    }

    public String getPossessive() {
        return possessive;
    }

    public String getReflexive() {
        return reflexive;
    }

    public String getVerb() {
        return verb;
    }

    public boolean isHidden() { return hidden; }

}
