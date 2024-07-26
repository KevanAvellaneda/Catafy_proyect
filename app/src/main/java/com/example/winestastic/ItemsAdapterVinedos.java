package com.example.winestastic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapterVinedos extends  RecyclerView.Adapter<ItemsAdapterVinedos.ViewHolder> {
    public static final int LAYOUT_DEFAULT = 0;
    public static final int LAYOUT_CUSTOM = 1;

    ArrayList<ItemsDomainVinedos> items;

    Context context;
    private int layoutType;

    public ItemsAdapterVinedos(ArrayList<ItemsDomainVinedos> items, Context context, int layoutType) {
        this.items = items;
        this.context = context;
        this.layoutType = layoutType;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = (viewType == LAYOUT_CUSTOM) ? R.layout.item_viewholder_vertodoslugares : R.layout.item_viewholder;
        View inflate = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsDomainVinedos itemsDomainVinedos = items.get(position);
        holder.titleTxt.setText(itemsDomainVinedos.getNombre_vinedos());
        holder.addressTxt.setText(itemsDomainVinedos.getUbicacion_vinedos());

        // Configuración de resolucion para Glide
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(400,400);

        Glide.with(context)
                .asBitmap() // Cargar como un bitmap para la carga progresiva
                .load(itemsDomainVinedos.getUrl())
                .thumbnail(0.20f)
                .placeholder(R.drawable.cargandoo) // Cargamos una imagen de baja resolución inicialmente
                .error(R.drawable.errorr) //Imagen en caso de error al cargar
                .apply(requestOptions) // Aplicar opciones de cache
                .transition(BitmapTransitionOptions.withCrossFade()) // Agregar transición al cargar la imagen
                .fitCenter()
                .into(holder.pic);

        SharedPreferences sharedPreferences = context.getSharedPreferences("favoritos", Context.MODE_PRIVATE);
        final boolean[] esFavorito = {sharedPreferences.contains(itemsDomainVinedos.getIdVinedos())};

        if (esFavorito[0]) {
            holder.favoriteIcon.setImageResource(R.drawable.corazon_rojo);
        } else {
            holder.favoriteIcon.setImageResource(R.drawable.corazon);
        }

        holder.favoriteIcon.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (esFavorito[0]) {
                editor.remove(itemsDomainVinedos.getIdVinedos());
                holder.favoriteIcon.setImageResource(R.drawable.corazon);
                Toast.makeText(context, "Lugar eliminado de favoritos", Toast.LENGTH_SHORT).show();
            } else {
                editor.putString(itemsDomainVinedos.getIdVinedos(), itemsDomainVinedos.getNombre_vinedos());
                holder.favoriteIcon.setImageResource(R.drawable.corazon_rojo);
                Toast.makeText(context, "Lugar añadido a favoritos", Toast.LENGTH_SHORT).show();
            }

            editor.apply();
            // Actualizar la variable esFavorito después de hacer clic
            esFavorito[0] = !esFavorito[0];
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, itemsDomainVinedos.getNombre_vinedos(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailVinedosActivity.class);
                intent.putExtra("idVinedos", itemsDomainVinedos.getIdVinedos());  // Asegúrate de tener un método getIdBarbacoa() en tu modelo
                intent.putExtra("titleTxt", itemsDomainVinedos.getNombre_vinedos());
                intent.putExtra("addressTxt", itemsDomainVinedos.getUbicacion_vinedos());
                //intent.putExtra("horarioTxt", itemsDomainVinedos.getHorario_barbacoa());
                intent.putExtra("imageUrl", itemsDomainVinedos.getUrl());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<ItemsDomainVinedos> items) {
        this.items = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return layoutType;
    }

    public void setFilter(ArrayList<ItemsDomainVinedos> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, addressTxt;
        ImageView pic, favoriteIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.nombrevinedo);
            addressTxt=itemView.findViewById(R.id.direccion);
            pic=itemView.findViewById(R.id.url);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }
    }
}
