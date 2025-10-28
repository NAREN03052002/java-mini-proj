package com.app.rating.model;

/**
 * Represents a single feedback question and its type.
 */
public class Question {
    private int id;
    private String text;
    private String type; // e.g., "RATING" (1-5 star) or "TEXT" (written feedback)
    private boolean isRequired;

    public Question(int id, String text, String type, boolean isRequired) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.isRequired = isRequired;
    }
    
    // Getters
    public int getId() { return id; }
    public String getText() { return text; }
    public String getType() { return type; }
    public boolean isRequired() { return isRequired; }
}
