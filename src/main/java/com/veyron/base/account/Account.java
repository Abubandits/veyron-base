package com.veyron.base.account;

public class Account {
    private String name;

    // нужен для Gson
    public Account() {
    }

    public Account(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
