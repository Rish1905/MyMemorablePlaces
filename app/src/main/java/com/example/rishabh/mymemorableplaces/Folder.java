package com.example.rishabh.mymemorableplaces;

public class Folder {

    public Folder(byte[] image, String folderName) {
        this.image = image;
        this.folderName = folderName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    private byte[] image;
    private String folderName;


}
