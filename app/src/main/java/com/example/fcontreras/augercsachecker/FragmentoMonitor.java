package com.example.fcontreras.augercsachecker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import java.util.HashMap;

public class FragmentoMonitor extends Fragment {

    MainMonitor mlistener;
    RecyclerView recyclerConfig;
    ArrayList<ConfigItem> configItems;

    ConfigAdapter adapter;

    HashMap<String, Boolean> configValues;

    Button btnRegisterMonitor;


    interface MainMonitor {
        public void ReplaceFragmentToMain();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainMonitor) {
            mlistener = (MainMonitor)context;
        }
        else throw new RuntimeException(context.toString()
                + " must implement ToActivityMain interface");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.register_monitoring_list, container, false);
        recyclerConfig = (RecyclerView) v.findViewById(R.id.recyclerC);
        btnRegisterMonitor = (Button) v.findViewById(R.id.registerM);


        configValues = new HashMap<String, Boolean>();
        configItems = new ArrayList<ConfigItem>();


        new BackgroundTaskConfig().execute();


        //SharedPreferences monitor = getActivity().getSharedPreferences("monitor", Context.MODE_PRIVATE);
        adapter = new ConfigAdapter(getContext(), configItems, configValues);
        recyclerConfig.setAdapter(adapter);

        recyclerConfig.setHasFixedSize(true);
        recyclerConfig.setLayoutManager(new LinearLayoutManager(getContext()));

        btnRegisterMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configuring General stuff
                SharedPreferences preferences = getActivity().getSharedPreferences("general", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("showNot", true);
                editor.putBoolean("Sounds", true);
                editor.putBoolean("Vibrate", true);
                editor.apply();


                // Configuring monitoring list
                SharedPreferences monitor = getActivity().getSharedPreferences("monitor", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = monitor.edit();
                HashMap<String, Boolean> ConfigToSave = adapter.getConfig();
                for (int i = 0 ; i<configItems.size(); i++) {
                    editor1.putBoolean("item"+configItems.get(i).ID, ConfigToSave.get("item"+configItems.get(i).ID));
                }
                editor1.apply();

                mlistener.ReplaceFragmentToMain();
            }
        });


        return v;
    }

    class BackgroundTaskConfig extends AsyncTask {

        SharedPreferences monitor;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            monitor = getActivity().getSharedPreferences("monitor", Context.MODE_PRIVATE);

            configItems.clear();
            configValues.clear();
            dialog = ProgressDialog.show(getContext(), "Monitoring Data", "Loading Data");

        }


        @Override
        protected void onPostExecute(Object o) {
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            int respuesta;
            String linea;
            StringBuilder resul;
            URL url;


            try {
                url = new URL("https://amiga.auger.org.ar/json/AugerCSAChecker/getListConfig.php");
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
                            configItems.add(new ConfigItem(registro.getInt("ID"),registro.getString("Name")));

                            // Setting the hashmap values for configuration stuffs
                            configValues.put("item"+registro.getString("ID"), false);
                        }
                    }

                } else Log.e("JSON", "Error obteniendo el JSON String");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
