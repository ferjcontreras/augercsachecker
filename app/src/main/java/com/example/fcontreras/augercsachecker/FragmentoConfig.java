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
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class FragmentoConfig extends Fragment {

    SwitchCompat showNot;
    SwitchCompat Sounds;
    SwitchCompat Vibrate;
    TextView User, Token, Email;

    ArrayList<ConfigItem> configItems;

    RecyclerView recConfig;
    ConfigAdapter adapter;

    HashMap<String, Boolean>  configValues;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.configuration, container, false);

        showNot = (SwitchCompat) v.findViewById(R.id.notification);
        Sounds = (SwitchCompat) v.findViewById(R.id.sound);
        Vibrate = (SwitchCompat) v.findViewById(R.id.vibrate);
        recConfig = (RecyclerView) v.findViewById(R.id.recyclerConfig);
        User = (TextView) v.findViewById(R.id.usuario);
        Token = (TextView) v.findViewById(R.id.token);
        Email = (TextView) v.findViewById(R.id.email);

        SharedPreferences preferences = getActivity().getSharedPreferences("general", Context.MODE_PRIVATE);
        User.setText(preferences.getString("User", ""));
        Token.setText(preferences.getString("Token", ""));
        Email.setText(preferences.getString("Email", ""));


        configValues = new HashMap<String, Boolean>();

        configItems = new ArrayList<ConfigItem>();


        new BackgroundTaskConfig().execute();


        //SharedPreferences monitor = getActivity().getSharedPreferences("monitor", Context.MODE_PRIVATE);
        adapter = new ConfigAdapter(getContext(), configItems, configValues);
        recConfig.setAdapter(adapter);

        recConfig.setHasFixedSize(true);
        recConfig.setNestedScrollingEnabled(false);
        recConfig.setLayoutManager(new LinearLayoutManager(getContext()));

        SetGeneralPreferences();

        return v;
    }

    private void SetGeneralPreferences() {
        // Just get the paramenters from the preferences file...

        SharedPreferences preferences = getActivity().getSharedPreferences("general", Context.MODE_PRIVATE);

        showNot.setChecked(preferences.getBoolean("showNot", false));
        Sounds.setChecked(preferences.getBoolean("Sounds", false));
        Vibrate.setChecked(preferences.getBoolean("Vibrate", false));



    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i("Stop", "Pasa por aqui al hacer atras");

        SharedPreferences preferences = getActivity().getSharedPreferences("general", Context.MODE_PRIVATE);
        SharedPreferences monitor = getActivity().getSharedPreferences("monitor", Context.MODE_PRIVATE);


        // General settings
        SharedPreferences.Editor editor = preferences.edit();
        //editor.clear();
        editor.putBoolean("showNot", showNot.isChecked());
        editor.putBoolean("Sounds", Sounds.isChecked());
        editor.putBoolean("Vibrate", Vibrate.isChecked());
        editor.apply();

        // Monitor Settings

        SharedPreferences.Editor editor1 = monitor.edit();
        editor1.clear();
        HashMap<String, Boolean> ConfigToSave = adapter.getConfig();
        for (int i = 0 ; i<configItems.size(); i++) {
            editor1.putBoolean("item"+configItems.get(i).ID, ConfigToSave.get("item"+configItems.get(i).ID));
        }
        editor1.apply();
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
                            configValues.put("item"+registro.getString("ID"), monitor.getBoolean("item"+registro.getString("ID"), false));
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
