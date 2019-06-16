package com.trick77.inspector;

public class FlashData {

    private String id;
    private String name;
    private String encryptCompressMethod;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void getName(String longName) {
        this.name = longName;
    }

    public String getEncryptCompressMethod() {
        return encryptCompressMethod;
    }

    public void setEncryptCompressMethod(String encryptCompressMethod) {
        this.encryptCompressMethod = encryptCompressMethod;
    }
}
