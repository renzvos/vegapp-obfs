// Generated by view binder compiler. Do not edit!
package com.example.vegapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;

import com.example.vegapp.databinding.AppBarNavigationBinding;
import com.google.android.material.navigation.NavigationView;


import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCustomBinding implements ViewBinding {
  @NonNull
  private final DrawerLayout rootView;

  @NonNull
  public final AppBarNavigationBinding appBarNavigation;

  @NonNull
  public final DrawerLayout drawerLayout;

  @NonNull
  public final NavigationView navView;

  private ActivityCustomBinding(@NonNull DrawerLayout rootView,
      @NonNull AppBarNavigationBinding appBarNavigation, @NonNull DrawerLayout drawerLayout,
      @NonNull NavigationView navView) {
    this.rootView = rootView;
    this.appBarNavigation = appBarNavigation;
    this.drawerLayout = drawerLayout;
    this.navView = navView;
  }

  @Override
  @NonNull
  public DrawerLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCustomBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCustomBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.navactivity, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCustomBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.app_bar_navigation;
      View appBarNavigation = ViewBindings.findChildViewById(rootView, id);
      if (appBarNavigation == null) {
        break missingId;
      }
      AppBarNavigationBinding binding_appBarNavigation = AppBarNavigationBinding.bind(appBarNavigation);

      DrawerLayout drawerLayout = (DrawerLayout) rootView;

      id = R.id.nav_view;
      NavigationView navView = ViewBindings.findChildViewById(rootView, id);
      if (navView == null) {
        break missingId;
      }

      return new ActivityCustomBinding((DrawerLayout) rootView, binding_appBarNavigation,
          drawerLayout, navView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
