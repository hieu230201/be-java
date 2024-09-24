package org.example.basewebsub.util;

public class StringBuilderPlus {

    private final StringBuilder sb;

    public StringBuilderPlus() {
        sb = new StringBuilder();
    }

    public int length() {
        return sb.length();
    }

    public void append(String str) {
        sb.append(str != null ? str : "");
    }

    public void appendLine() {
        sb.append(System.lineSeparator());
    }

    public void appendLine(String str) {
        sb.append(str != null ? str : "").append(System.lineSeparator());
    }

    public String toString() {
        return sb.toString();
    }

    public void appendNewLine(String str) {
        sb.append(str != null ? str : "").append(System.lineSeparator());
    }
}