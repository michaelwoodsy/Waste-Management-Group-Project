package org.seng302.project.repository_layer.model.types;

/**
 * Enumeration used to define valid tags
 */
public enum Tag {

    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    BLUE("blue"),
    PURPLE("purple"),
    NONE("none");

    private final String name;

    Tag(String name) {
        this.name = name;
    }

    /**
     * Returns the Tag object given the name of the tag, returns null if the tag is not valid
     *
     * @param name String name of the tag to get
     * @return the Tag corresponding to the name given
     */
    public static Tag getTag(String name) {
        for (Tag tag : Tag.values()) {
            if (tag.name.equals(name)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Checks if a string matches one of the valid tag names
     *
     * @param name string to check validity of
     * @return true if the string is a valid tag
     */
    public static boolean checkTag(String name) {
        return getTag(name) != null;
    }

}
