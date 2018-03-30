package com.saleniuk.logcattofile;

public enum Priority {

    VERBOSE("V"),
    DEBUG("D"),
    INFO("I"),
    WARNING("W"),
    ERROR("E"),
    FATAL("F"),
    SILENT("S");

    private final String value;

    /**
     * @param value
     */
    Priority(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
