package com.aamys.fresh;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class ProductSliderAdapter extends SliderAdapter {
    ArrayList<String> ImageUrls;
    SliderCallback callback;

    public ProductSliderAdapter(ArrayList<String> imageUrls,SliderCallback callback)
    {
        this.ImageUrls = imageUrls;
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return ImageUrls.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        Picasso.get().load(ImageUrls.get(position)).into(viewHolder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                callback.Loaded();
            }

            @Override
            public void onError(Exception e) {

            }
        });

        //viewHolder.bindImageSlide(ImageUrls.get(position));

    }

    public interface SliderCallback
    {
        public void Loaded();
    }
}