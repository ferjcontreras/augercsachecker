package com.example.fcontreras.augercsachecker;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView nombre;
    TextView detalle;
    ImageView imagen;
    TextView last;
    CardView cardView;
    TextView ack;
    ToggleButton silent;


    public ItemViewHolder(View itemView) {
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nombre);
        detalle = (TextView) itemView.findViewById(R.id.detalle);
        imagen = (ImageView) itemView.findViewById(R.id.imagen);
        last = (TextView) itemView.findViewById(R.id.ultima);
        cardView = (CardView)itemView.findViewById(R.id.card);
        ack = (TextView)itemView.findViewById(R.id.ack);
        silent = (ToggleButton) itemView.findViewById(R.id.silent);
        //parlante = (ImageView) itemView.findViewById(R.id.parlante);
    }
}
