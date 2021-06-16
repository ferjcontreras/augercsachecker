package com.example.fcontreras.augercsachecker;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public  class AlarmAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private final FragmentoLista.ToActivityMain mListener;
    Context context;
    LayoutInflater inflater;
    ArrayList<AlarmItemList> listado_alarmas;
    //SharedPreferences preferences;


    public AlarmAdapter(Context context, ArrayList<AlarmItemList> listado, FragmentoLista.ToActivityMain mListener){
        this.listado_alarmas = listado;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mListener = mListener;
        //preferences = context.getSharedPreferences("monitor", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent,false);
        ItemViewHolder item = new ItemViewHolder(view);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        if (listado_alarmas.get(position).hijos.equals("null")) holder.nombre.setText(listado_alarmas.get(position).nombre);
        else holder.nombre.setText(listado_alarmas.get(position).nombre+" ➤");
        holder.detalle.setText(listado_alarmas.get(position).detalle);
        holder.last.setText(listado_alarmas.get(position).last);
        if (listado_alarmas.get(position).estado == 0) holder.imagen.setImageResource(R.drawable.ic_check_circle_black_24dp);
        else if (listado_alarmas.get(position).estado == 1) holder.imagen.setImageResource(R.drawable.ic_warning_black_24dp);
        else if (listado_alarmas.get(position).estado == 2) holder.imagen.setImageResource(R.drawable.ic_error_black_24dp);

        // Mostrar el nombre de reconocimiento de alarmas.
        if (listado_alarmas.get(position).ack.equals("null")) {
            holder.ack.setVisibility(View.INVISIBLE);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));
        }
        else {
            holder.ack.setVisibility(View.VISIBLE);
            holder.ack.setText(listado_alarmas.get(position).ack);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.AckItem));
        }

        // Mostrar el ToggleButton para silenciar la alarma en caso de ser una "hoja"
        if ((listado_alarmas.get(position).hijos.equals("null"))) {
            holder.silent.setVisibility(View.VISIBLE);
            holder.silent.setChecked(listado_alarmas.get(position).silent);


            holder.silent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences monitor = ((Activity)context).getSharedPreferences("monitor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = monitor.edit();
                    if (isChecked) {
                        Log.i("Silent", "Silenciado");
                        editor.putBoolean("silent"+listado_alarmas.get(position).ID, true);
                    }
                    else {
                        Log.i("Silent", "No silenciado");
                        editor.putBoolean("silent"+listado_alarmas.get(position).ID, false);
                    }
                    editor.commit();
                }
            });
        }
        else {
            holder.silent.setVisibility(View.INVISIBLE);
        }

        // Entrar a las ramas en caso de tener mas hijos...
        holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(listado_alarmas.get(position).hijos.equals("null")))  mListener.PutMonitFragment(listado_alarmas.get(position).ID);
                    else {
                        //Toast.makeText(context, "Length "+listado_alarmas.get(position).nombre.length(), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                        alerta.setTitle(listado_alarmas.get(position).nombre);
                        alerta.setMessage(listado_alarmas.get(position).detalle);
                        alerta.show();
                    }
                }
            });

        // Reconocer o Cancelar una alarma
        if (listado_alarmas.get(position).estado != 0 && listado_alarmas.get(position).hijos.equals("null")) holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("AlarmAdapter", "Long Click");
                SharedPreferences preferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);
                final String Usuario = preferences.getString("User","");
                final String RW = preferences.getString("RW", "0");

                if (RW.equals("1")) {
                    if (listado_alarmas.get(position).ack.equals("null")) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                RequestBody body = new FormBody.Builder()
                                        .add("user", Usuario)
                                        //.add("ack", "1")
                                        .add("id", listado_alarmas.get(position).ID + "")
                                        .build();

                                Request request = new Request.Builder()
                                        .url("https://amiga.auger.org.ar/json/AugerCSAChecker/registrarAck.php")
                                        .post(body)
                                        .build();

                                try {
                                    client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Acknowledged!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                        };
                        thread.start();
                    }
                    else if (listado_alarmas.get(position).ack.equals(Usuario)) {

                        // Cancel Ack by the User

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                RequestBody body = new FormBody.Builder()
                                        .add("user", "null")
                                        //.add("ack", "0")
                                        .add("id", listado_alarmas.get(position).ID + "")
                                        .build();

                                Request request = new Request.Builder()
                                        .url("https://amiga.auger.org.ar/json/AugerCSAChecker/registrarAck.php")
                                        .post(body)
                                        .build();

                                try {
                                    client.newCall(request).execute();
                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                        };
                        thread.start();
                    } else {
                        Toast.makeText(context, "You are not allowed to CANCEL this Alarm!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "You are a READONLY user!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });





    }

    @Override
    public int getItemCount() {
        return listado_alarmas.size();
    }
}
