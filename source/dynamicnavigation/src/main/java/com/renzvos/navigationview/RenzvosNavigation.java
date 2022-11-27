package com.renzvos.navigationview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewbinding.ViewBindings;

import com.google.android.material.navigation.NavigationView;
import com.renzvos.navigationview.databinding.AppBarNavigationBinding;


import java.util.ArrayList;


public class RenzvosNavigation {

    View root;
    AppCompatActivity activity;
    Context context;
    DrawerLayout drawer;
    ArrayList<Page> pages;
    boolean loaded = false;

    public RenzvosNavigation(Context context, AppCompatActivity activity){

        this.activity = activity;
        this.context = context;

    }

    public void OnCreater(String Heading, String Subheading, Drawable SidebarImage)
    {
        activity.setTheme(R.style.Theme_Rnav_NoActionBar);
        VariableInit();
        ChangeDrawerContent(Heading,Subheading,SidebarImage);
    }

    public void OnCreater(String Heading, String Subheading, int Theme,Drawable SidebarImage)
    {

        activity.setTheme(Theme);
        VariableInit();
        ChangeDrawerContent(Heading,Subheading,SidebarImage);
    }



    public void Load(ArrayList<Page> pages)
    {
        this.pages = pages;
        NavigationView tempnavView = ViewBindings.findChildViewById(root, R.id.nav_view);
        Menu menu = tempnavView.getMenu();

        for(int i = 0 ; i < pages.size() ; i++)
        {
            int RandomAdd = 2541;
            menu.add(pages.get(i).group,RandomAdd + i, i, pages.get(i).Title);
            menu.getItem(i).setIcon(pages.get(i).Icon);
            menu.getItem(i).isCheckable();
            pages.get(i).setId(menu.getItem(i).getItemId());
        }

        if(loaded == false)
        {
            OpenPage(0);
        }

        final NavigationView navView = ViewBindings.findChildViewById(root, R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String TAG = "Navigation";
                Log.i(TAG, "Item ID: " + item.getItemId());
                Log.i(TAG, "Item Order: " + item.getOrder());
                Log.i(TAG, "Item Title: " + item.getTitle());


                for(int i = 0 ; i < pages.size() ; i++)
                {
                    Log.i(TAG, "Checking : " + pages.get(i).id + " and " + item.getItemId());
                    if(item.getItemId() == pages.get(i).id)
                    {
                        Log.i(TAG, "Found : ");
                        Log.i(TAG, "Loading: " + pages.get(i).Title);
                        Log.i(TAG, "Loading: " + pages.get(i).id);
                        pages.get(i).callback.OnClick();
                        break;
                    }
                }
                int size = navView.getMenu().size();
                Log.i(TAG, "Checking: ");
                for (int i = 0; i < size; i++) {
                    if(i == item.getOrder())
                    {
                        Log.i(TAG, "Ticking: " + item.getOrder());
                        navView.getMenu().getItem(item.getOrder()).setChecked(true);
                    }
                    else
                    {
                        Log.i(TAG, "Removing: " + i);
                        navView.getMenu().getItem(i).setChecked(false);
                    }

                }

                return false;}
        });



    }

    public void CheckPage(int index)
    {
        final NavigationView navView = ViewBindings.findChildViewById(root, R.id.nav_view);
        int size = navView.getMenu().size();
        for (int i = 0; i < size; i++) {

            if(i == index)
            {
                navView.getMenu().getItem(index).setChecked(true);
            }
            else
            {
                navView.getMenu().getItem(i).setChecked(false);
            }


        }

    }

    public void UnCheckAll()
    {

        final NavigationView navView = ViewBindings.findChildViewById(root, R.id.nav_view);
        int size = navView.getMenu().size();
        for (int i = 0; i < size; i++) {
                navView.getMenu().getItem(i).setChecked(false);
        }

    }

    public void OpenPage(int index)
    {
       pages.get(index).callback.OnClick();
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
        loaded = true;
        drawer.close();
    }

    public boolean OnCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    public boolean OnSupportNavigationUp()
    {

        return true;
    }


    public boolean ActionBarSelectItem(MenuItem item)
    {
        if(item.getItemId() == R.id.opendrawer)
        {
            drawer.open();
            return true;
        }
        return false;
    }


    private void VariableInit()
    {
        root = activity.getLayoutInflater().inflate(R.layout.navactivity, null, false);
        ViewBindings.findChildViewById(root, R.id.app_bar_navigation);
        activity.setContentView(root);


        View appBarNavigation = ViewBindings.findChildViewById(root, R.id.app_bar_navigation);
        AppBarNavigationBinding binding_appBarNavigation = AppBarNavigationBinding.bind(appBarNavigation);
        activity.setSupportActionBar(binding_appBarNavigation.toolbar);
        drawer= (DrawerLayout) root;


    }

    public void ChangeDrawerContent(String Heading , String SubHeading,Drawable SidebarImage)
    {
        NavigationView navigationView= (NavigationView) activity.findViewById (R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView heading = header.findViewById(R.id.drawerheading);
        heading.setText(Heading);
        TextView subheading = header.findViewById(R.id.drawersubheading);
        subheading.setText(SubHeading);
        if(SidebarImage != null)
        {
            LinearLayout linearLayout = header.findViewById(R.id.navviewlayout);
            linearLayout.setBackground(SidebarImage);
        }


    }







}
