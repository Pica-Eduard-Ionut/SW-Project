package org.example.model;

import java.util.List;

public class Recipe {
    private String id;
    private String title;
    private List<String> cuisines;
    private String difficulty;

    public Recipe(String id, String title, List<String> cuisines, String difficulty) {
        this.id = id;
        this.title = title;
        this.cuisines = cuisines;
        this.difficulty = difficulty;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public List<String> getCuisines() { return cuisines; }
    public String getDifficulty() { return difficulty; }

    @Override
    public String toString() {
        return title + " (" + difficulty + ")";
    }
}