package com.notekeeper.restapi.payload.request;

import jakarta.validation.constraints.Size;

public class NoteInfo {

    @Size(max = 100)
    private String title;

    private String body;

    public NoteInfo() {
    }

    public NoteInfo(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
