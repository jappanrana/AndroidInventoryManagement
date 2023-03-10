package com.example.androidinventorymanagement.Models;

public class ProductsModel {

    String name;
    String code;
    String mrp;
    String gstAmt;
    String key;

    String measurement;

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

    public String getGstAmt() {
        return gstAmt;
    }

    public void setGstAmt(String gstAmt) {
        this.gstAmt = gstAmt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public ProductsModel() {
    }

    public ProductsModel(String name, String code, String mrp, String gstAmt, String key, String measurement) {
        this.name = name;
        this.code = code;
        this.mrp = mrp;
        this.gstAmt = gstAmt;
        this.key = key;
        this.measurement = measurement;
    }
}
