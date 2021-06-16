package com.example.fcontreras.augercsachecker;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Switch;

public class ItemConfigViewHolder extends RecyclerView.ViewHolder {

    SwitchCompat switch1;

    public ItemConfigViewHolder(View itemView) {
        super(itemView);
        switch1 = (SwitchCompat) itemView.findViewById(R.id.switch1);
    }
}
