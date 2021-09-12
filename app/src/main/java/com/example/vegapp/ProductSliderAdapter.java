package com.example.vegapp;

import java.util.ArrayList;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class ProductSliderAdapter extends SliderAdapter {
    ArrayList<String> ImageUrls;
    public ProductSliderAdapter(ArrayList<String> imageUrls)
    {
        this.ImageUrls = imageUrls;
    }

    @Override
    public int getItemCount() {
        return ImageUrls.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        viewHolder.bindImageSlide(ImageUrls.get(position));
    }
}