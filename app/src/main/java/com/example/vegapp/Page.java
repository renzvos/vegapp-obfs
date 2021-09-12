package com.example.vegapp;

import androidx.fragment.app.Fragment;

public class Page {
    int id;
    Fragment fragment;
    String Title;
    int Icon;
    int group;

    public Page(String title, int Icon, int Group, Fragment fragment) {
        this.fragment = fragment;
        this.Title = title;
        this.Icon = Icon;
        this.group = Group;
    }

    public Page(String title, int Icon, Fragment fragment){
        this.fragment = fragment;
        this.Title = title;
        this.Icon = Icon;
        this.group = R.id.defaultgroup;
    }

    public void setId(int id)
    {this.id = id;}

}
