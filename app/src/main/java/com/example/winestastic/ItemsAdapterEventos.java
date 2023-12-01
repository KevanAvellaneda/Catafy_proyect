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

public class ItemsAdapterEventos extends  RecyclerView.Adapter<ItemsAdapterEventos.ViewHolder> {
    ArrayList<ItemsDomainEventos> items2;

    Context context;

    public ItemsAdapterEventos(ArrayList<ItemsDomainEventos> items2, Context context) {
        this.items2 = items2;
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
        ItemsDomainEventos itemsDomainEventos = items2.get(position);
        holder.titleTxt.setText(itemsDomainEventos.getNombre_evento());
        holder.addressTxt.setText(itemsDomainEventos.getUbicacion_evento());

        Glide.with(context).load(itemsDomainEventos.getUrl()).into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return items2.size();
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
