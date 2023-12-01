package com.example.winestastic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ItemsAdapter extends  RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    ArrayList<ItemsDomain> items;

    Context context;

    public ItemsAdapter(ArrayList<ItemsDomain> items, Context context) {
        this.items = items;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(context).inflate(R.layout.item_viewholder,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDomain itemsDomain = items.get(position);
        holder.titleTxt.setText(itemsDomain.getNombre_vinedos());
        holder.addressTxt.setText(itemsDomain.getUbicacion_vinedos());

        Glide.with(context).load(itemsDomain.getUrl()).into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, addressTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.nombrevinedo);
            addressTxt=itemView.findViewById(R.id.direccion);
            pic=itemView.findViewById(R.id.url);
        }
    }
}
