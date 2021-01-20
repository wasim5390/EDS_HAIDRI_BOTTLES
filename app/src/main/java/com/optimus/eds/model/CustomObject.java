package com.optimus.eds.model;

public class CustomObject {


    public CustomObject(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String toString(){
        return getText().toString();
    }

    Long id;
    String text;
}
