package com.example.androidinventorymanagement.Models;

public class QuoteModel {
    String name;
    Float qty;
    String code;
    String mrp;
    String gstAmt;
    String addedfrom;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
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

    public String getAddedfrom() {
        return addedfrom;
    }

    public void setAddedfrom(String addedfrom) {
        this.addedfrom = addedfrom;
    }

    public QuoteModel() {
    }

    public QuoteModel(String name, Float qty, String code, String mrp, String gstAmt, String addedfrom) {
        this.name = name;
        this.qty = qty;
        this.code = code;
        this.mrp = mrp;
        this.gstAmt = gstAmt;
        this.addedfrom = addedfrom;
    }
}
