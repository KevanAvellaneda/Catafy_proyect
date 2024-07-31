package com.example.winestastic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private List<PageData> pages;

    public ViewPagerAdapter(List<PageData> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PageData pageData = pages.get(position);
        holder.textViewTitle.setText(pageData.getTitle());
        holder.textViewDescription.setText(pageData.getDescription());

        // Configuración de resolucion para Glide
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(400,400);

        // Usar Glide para cargar la imagen desde la URL
        Glide.with(holder.itemView.getContext())
                .asBitmap() // Cargar como un bitmap para la carga progresiva
                .load(pageData.getImageUrl())
                .thumbnail(0.20f)
                .placeholder(R.drawable.cargandoo) // Cargamos una imagen de baja resolución inicialmente
                .error(R.drawable.errorr) //Imagen en caso de error al cargar
                .apply(requestOptions) // Aplicar opciones de cache
                .transition(BitmapTransitionOptions.withCrossFade()) // Agregar transición al cargar la imagen
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageView;
        TextView textViewTitle;
        TextView textViewDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}