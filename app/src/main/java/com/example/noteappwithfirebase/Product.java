package com.example.noteappwithfirebase;

public class Product {
    private String text;
    private boolean isCompleted;

    public Product() {
    }

    public Product(String text, boolean isCompleted) {
        this.text = text;
        this.isCompleted = isCompleted;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
