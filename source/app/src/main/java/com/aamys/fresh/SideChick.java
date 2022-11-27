package com.aamys.fresh;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class SideChick {

    Activity activity;

    public SideChick(Activity activity)
    {
            this.activity = activity;
    }

    public RoundTag DisplayRoundTag(ConstraintLayout parent, Drawable drawable,String Label,OnIconClick callback)
    {
        View child = activity.getLayoutInflater().inflate(R.layout.singleround_tag, (ViewGroup) parent);
        ImageView icon = child.findViewById(R.id.iconspace);
        TextView label = child.findViewById(R.id.label);
        ConstraintLayout constraintLayout = child.findViewById(R.id.carticon);
        RoundTag roundTag = new RoundTag(label,icon,constraintLayout);
        roundTag.SetText(Label);
        roundTag.SetIcon(drawable);
        roundTag.setOnClickListener(new RoundTag.OnTagClick() {
            @Override
            public void OnClick(RoundTag roundTag) {
                callback.OnIconClick();
            }
        });

        return roundTag;
    }

    public interface OnIconClick
    {
        public void OnIconClick();
    }
}
