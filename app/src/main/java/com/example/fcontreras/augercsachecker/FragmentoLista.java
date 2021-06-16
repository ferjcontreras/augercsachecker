package com.example.fcontreras.augercsachecker;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FragmentoLista extends Fragment {


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }



    ArrayList<AlarmItemList> listado_alarmas; // = new ArrayList<AlarmItemList>();
    AlarmAdapter adapter;
    RecyclerView recyclerView;
    int parent = 0;
    private ToActivityMain mListener;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean forceUpdate = true;
    long tsLong = System.currentTimeMillis();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToActivityMain) {
            mListener = (ToActivityMain)context;
        }
        else throw new RuntimeException(context.toString()
                + " must implement ToActivityMain interface");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.lista_fragment_layout, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.lista);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (listado_alarmas == null) listado_alarmas = new ArrayList<AlarmItemList>();

        // Getting paramenter PARENT comming from the Activity
        parent = getArguments().getInt("PARENT", 0);

        // It is used for get the data from the server when 1 minute have happened
        if ((System.currentTimeMillis()/1000 - tsLong/1000) > 60) {
            forceUpdate = true;
            tsLong = System.currentTimeMillis();
        }


        // Get the data from the server before creating the Adapter...
        new BackgroundTask().execute();


        // Setting the adapter
        adapter = new AlarmAdapter(getContext(), listado_alarmas, mListener);
        recyclerView.setAdapter(adapter);


        // Swipe refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                forceUpdate = true;
                new BackgroundTask().execute();
            }
        });

        // This fragment has menu in the ActionBar
        setHasOptionsMenu(true);

        return v;
    }



    class BackgroundTask extends AsyncTask<Void, AlarmItemList, Void> {

        SharedPreferences monitor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
            if (forceUpdate) listado_alarmas.clear();
            Log.i("onPreExecute", "Estoy pasando por aqui items: "+listado_alarmas.size()+" force: "+forceUpdate);

            monitor = getActivity().getSharedPreferences("monitor", Context.MODE_PRIVATE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //dialog.dismiss();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            //Log.i("onPostExecute", "Estoy pasando por aqui items: "+listado_alarmas.size()+" force: "+forceUpdate);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int respuesta;
            String linea;
            StringBuilder resul;
            URL url;

            if (forceUpdate) {
                Log.i("DebugDB", "Connecting to database");
                try {
                    url = new URL("https://amiga.auger.org.ar/json/AugerCSAChecker/consultar.php?n=" + parent);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    respuesta = connection.getResponseCode();
                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        resul = new StringBuilder();

                        while ((linea = reader.readLine()) != null) {
                            resul.append(linea);
                        }

                        Log.i("JSON", resul.toString());


                        JSONArray jsonArray = new JSONArray(resul.toString());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject registro = jsonArray.getJSONObject(i);
                                if (parent != 0 || monitor.getBoolean("item"+registro.getString("ID"),false)) {
                                    boolean silent = monitor.getBoolean("silent"+registro.getString("ID"),false);
                                    AlarmItemList alarmItemList = new AlarmItemList(registro.getInt("ID"), registro.getString("Name"), registro.getString("Detail"), registro.getInt("Status"), registro.getString("Last"), registro.getString("Hijo"), registro.getString("Ack"), silent);
                                    listado_alarmas.add(alarmItemList);
                                }
                            }
                        }
                    } else Log.e("JSON", "Error obteniendo el JSON String");
                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                forceUpdate = false;
            }
            return null;
        }

    }

    interface ToActivityMain {
        void PutMonitFragment(int position);
    }

}
