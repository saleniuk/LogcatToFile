package com.saleniuk.logcattofile;

public class Parameter {

    String tag;
    Priority priority;

    public Parameter(String tag, Priority priority) {
        this.tag = tag;
        this.priority = priority;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return tag + ":" + priority.getValue();
    }
}
