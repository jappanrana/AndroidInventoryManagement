package com.example.androidinventorymanagement.Models;

import java.io.Serializable;

public class ExportModel implements Serializable {
    String key;
    String name;
    String code;
    String mrp;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public ExportModel() {
    }

    public ExportModel(String key, String name, String code, String mrp) {
        this.key = key;
        this.name = name;
        this.code = code;
        this.mrp = mrp;
    }
}
