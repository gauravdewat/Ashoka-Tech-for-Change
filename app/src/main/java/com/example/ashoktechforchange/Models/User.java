package com.example.ashoktechforchange.Models;

public class User {

    public String name;
    public String mobileNo;
    public String pincode;
    public String address;
    public String firstCompDone;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String mobileNo, String pincode, String address, String firstCompDone) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.pincode = pincode;
        this.address = address;
        this.firstCompDone = firstCompDone;
    }
}
