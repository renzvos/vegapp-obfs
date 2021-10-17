package com.example.vegapp;

import androidx.fragment.app.Fragment;

public class Page {
    int id;
    Fragment fragment;
    String Title;
    int Icon;
    int group;
    boolean Activity;
    Class activityclass;

    public Page(String title, int Icon, int Group, Fragment fragment) {
        this.Activity = false;
        this.fragment = fragment;
        this.Title = title;
        this.Icon = Icon;
        this.group = Group;
    }

    public Page(String title, int Icon, int Group, Class activity) {
        this.Activity = true;
        this.Title = title;
        this.Icon = Icon;
        this.group = Group;
        this.activityclass = activity;
    }

    public Page(String title, int Icon, Fragment fragment){
        this.fragment = fragment;
        this.Title = title;
        this.Icon = Icon;
        this.group = R.id.defaultgroup;
    }

    public Page(String title, int Icon, Class activity) {
        this.Activity = true;
        this.Title = title;
        this.Icon = Icon;
        this.group = R.id.defaultgroup;
        this.activityclass = activity;
    }


    public void setId(int id)
    {this.id = id;}

}
