package com.example.androidinventorymanagement.Models;

public class party {

    String name,number,GSTno,address,key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGSTno() {
        return GSTno;
    }

    public void setGSTno(String GSTno) {
        this.GSTno = GSTno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public party() {
    }

    public party(String name, String number, String GSTno, String address, String key) {
        this.name = name;
        this.number = number;
        this.GSTno = GSTno;
        this.address = address;
        this.key = key;
    }
}
