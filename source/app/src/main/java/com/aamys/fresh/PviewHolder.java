package com.aamys.fresh;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PviewHolder {
    int TitleBarColor;
    String Title;

    boolean leftbutton = false;
    int leftbuttondrawable;

    public PviewHolder(int TitleBarColor,String Title)
    {
        this.TitleBarColor = TitleBarColor;
        this.Title = Title;
    }

    public void setLeftButton()
    {
        leftbutton = true;
    }



    public void ModifyContextForActivity(AppCompatActivity activity, Holdercallbacks holdercallbacks)
    {
        Toolbar toolbar = activity.findViewById(R.id.customtoolbar);
        toolbar.setBackgroundColor(TitleBarColor);
        TextView title = activity.findViewById(R.id.toolbarTitle);
        title.setText(Title);
        if(leftbutton)
        {
            ImageView lb = activity.findViewById(R.id.titleleftbutton);
            lb.setVisibility(View.VISIBLE);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holdercallbacks.OnClickLeftButton();
                }
            });
        }

    }

    public interface Holdercallbacks{
        public void OnClickLeftButton();
    }



}
