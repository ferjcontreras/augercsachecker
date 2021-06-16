package com.example.fcontreras.augercsachecker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;


import java.util.ArrayList;
import java.util.HashMap;

public class ConfigAdapter extends RecyclerView.Adapter<ItemConfigViewHolder> {


    ArrayList<ConfigItem> configItems;
    Context context;
    LayoutInflater inflater;
    HashMap<String, Boolean> config;

    public ConfigAdapter(Context context, ArrayList<ConfigItem> configItems, HashMap<String, Boolean> config){
        Log.i("Adapter", "Se crea ConfigAdapter ");
        this.configItems = configItems;
        this.context = context;
        this.config = config;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ItemConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_config_layout, parent,false);
        ItemConfigViewHolder item = new ItemConfigViewHolder(view);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemConfigViewHolder holder, final int position) {
        holder.switch1.setText(configItems.get(position).name);
        holder.switch1.setChecked(config.get("item"+configItems.get(position).ID));
        Log.i("Adapter", configItems.get(position).name+" - "+config.get("item"+configItems.get(position).ID)+" tamanio names: "+configItems.size());

        holder.switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat item = (SwitchCompat)v;
                Log.i("Adapter", "Se modifica "+configItems.get(position).name);
                if (item.isChecked())  config.put("item"+ configItems.get(position).ID,true);
                else config.put("item"+configItems.get(position).ID,false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return configItems.size();
    }

    public HashMap<String, Boolean> getConfig(){
        return config;
    }


}
