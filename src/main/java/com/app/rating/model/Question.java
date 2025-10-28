package com.app.rating.model;

public class Question {
    private int id;
    private String text;
    private String type; // e.g., "RATING" (1-5 star) or "TEXT" (written feedback)
    private boolean isRequired;
    // Note: In a real DB, this links to the course, but for now, we'll keep it simple.

    public Question(int id, String text, String type, boolean isRequired) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.isRequired = isRequired;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public String getText() { return text; }
    public String getType() { return type; }
    public boolean isRequired() { return isRequired; }
}
