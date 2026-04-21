package org.example.model;

public class User {
    private String name;
    private String surname;
    private String skill;
    private String preferredCuisine;

    public User(String name, String surname, String skill, String preferredCuisine) {
        this.name = name;
        this.surname = surname;
        this.skill = skill;
        this.preferredCuisine = preferredCuisine;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getPreferredCuisine() {
        return preferredCuisine;
    }

    public void setPreferredCuisine(String preferredCuisine) {
        this.preferredCuisine = preferredCuisine;
    }
}