package org.chunkmancompletionist.types;

public enum TaskGroup {
    SKILL ("Skill"),
    NON_SKILL ("Non Skill"),
    QUEST ("Quest"),
    DIARY ("Diary"),
    COMBAT("Combat"),
    EXTRA ("Other");

    private final String displayText;

    TaskGroup(String displayText) {
        this.displayText = displayText;
    }

    public String displayText() {
        return this.displayText;
    }
}
