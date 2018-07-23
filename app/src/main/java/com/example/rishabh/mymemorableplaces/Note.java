package com.example.rishabh.mymemorableplaces;

public class Note {
    public Note(String title, byte[] data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    String title;
    byte[] data;
}
