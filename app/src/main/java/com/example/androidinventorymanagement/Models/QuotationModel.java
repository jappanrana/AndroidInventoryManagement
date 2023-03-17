package com.example.androidinventorymanagement.Models;

import java.util.ArrayList;

public class QuotationModel {
    String customerName,CustomerNumber,dateTime,key,name,type;
    int discount,gst,subtotal,total;
    ArrayList<QuoteModel> Items = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNumber() {
        return CustomerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        CustomerNumber = customerNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getGst() {
        return gst;
    }

    public void setGst(int gst) {
        this.gst = gst;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<QuoteModel> getItems() {
        return Items;
    }

    public void setItems(ArrayList<QuoteModel> items) {
        Items = items;
    }

    public QuotationModel() {
    }

    public QuotationModel(String customerName, String customerNumber, String dateTime, String key, String name, String type, int discount, int gst, int subtotal, int total, ArrayList<QuoteModel> items) {
        this.customerName = customerName;
        CustomerNumber = customerNumber;
        this.dateTime = dateTime;
        this.key = key;
        this.name = name;
        this.type = type;
        this.discount = discount;
        this.gst = gst;
        this.subtotal = subtotal;
        this.total = total;
        Items = items;
    }
}
