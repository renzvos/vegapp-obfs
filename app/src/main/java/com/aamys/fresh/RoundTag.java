package com.aamys.fresh;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class RoundTag {
    public TextView text;
    public ImageView icon;
    public ConstraintLayout constraintLayout;

    public RoundTag(TextView textView , ImageView imageView, ConstraintLayout constraintLayout)
    {
        this.text = textView;
        this.icon = imageView;
        this.constraintLayout = constraintLayout;
    }
    public void SetText(String text)
    {
        this.text.setText(text);
    }
    public void SetIcon(Drawable drawable)
    {
        icon.setImageDrawable(drawable);
    }

    public void setOnClickListener(OnTagClick callback)
    {
        RoundTag roundTag = this;
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.OnClick(roundTag);
            }
        });
    }

    public interface OnTagClick
    {
        public void OnClick(RoundTag roundTag);
    }
}
