package com.example.winestastic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    private final List<SliderData> sliderItemsList;

    public SliderAdapter(Context context, ArrayList<SliderData> sliderDataArrayList) {
        this.sliderItemsList = sliderDataArrayList;
    }

    // Inflating the slider_layout
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Set Data
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final SliderData sliderItem = sliderItemsList.get(position);

        // Loard image from url
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImgUrl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);

        // Set destination Activity
        Class<?> destination = sliderItem.getDestination();
        if(destination != null)
            viewHolder.setDestination(destination, position);
    }

    @Override
    public int getCount() {
        return sliderItemsList.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Initializing views
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.myimage);
            this.itemView = itemView;
        }

        public void setDestination(Class<?> destination, int position){
            imageViewBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), destination);
                    intent.putExtra("position", position);
                    view.getContext().startActivity(intent);

                }
            });
        }
    }
}
