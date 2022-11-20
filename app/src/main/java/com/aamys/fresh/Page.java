package com.aamys.fresh;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Page {
    int id;
    String Title;
    int Icon;
    int group;
    RZNavigationClickListener callback;



    public Page(String title, int Icon, int Group, RZNavigationClickListener Onclick ) {
        this.Title = title;
        this.Icon = Icon;
        this.group = Group;
        this.callback = Onclick;
    }


    public Page(String title, int Icon, RZNavigationClickListener Onclick){
        this.Title = title;
        this.Icon = Icon;
        this.group = R.id.defaultgroup;
        this.callback = Onclick;
    }





    public void setId(int id)
    {this.id = id;}


    public interface RZNavigationClickListener
    {
         void OnClick();
    }


}
