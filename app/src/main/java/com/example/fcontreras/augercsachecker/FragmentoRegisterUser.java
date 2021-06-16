package com.example.fcontreras.augercsachecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;


public class FragmentoRegisterUser extends Fragment {

    MainRegisterUser mlistener;
    Button btnRegistrar;
    EditText Ususario;
    EditText Email;

    interface MainRegisterUser {
        public void ReplaceFragmentToMonitor();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.register_user, container, false);
        Ususario = (EditText) v.findViewById(R.id.usuario);
        Email = (EditText) v.findViewById(R.id.email);
        btnRegistrar = (Button)v.findViewById(R.id.btnregisterUser);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(Ususario.getText().toString().equals("")) && !(Email.getText().toString().equals(""))) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("general", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("User", Ususario.getText().toString());
                    editor.putString("Email", Email.getText().toString());
                    editor.apply();
                    //RegisterToServer();
                    new DoInBackground().execute();
                    mlistener.ReplaceFragmentToMonitor();
                }
                else {
                    Toast.makeText(getContext(), R.string.incomplete_data, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    private void RegisterToServer() {
        SharedPreferences preferences = getActivity().getSharedPreferences("general", Context.MODE_PRIVATE);
        String token = preferences.getString("Token", "");
        if (!token.equals("")) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("user", Ususario.getText().toString())
                .add("email", Email.getText().toString())
                .build();

            Request request = new Request.Builder()
                .url("https://amiga.auger.org.ar/json/AugerCSAChecker/registrarToken.php")
                .post(body)
                .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Failed to get token, please contact the Administrator", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainRegisterUser) {
            mlistener = (MainRegisterUser) context;
        }
        else throw new RuntimeException(context.toString()
                + " must implement ToActivityMain interface");
    }

    public class DoInBackground extends AsyncTask {



        @Override
        protected Object doInBackground(Object[] objects) {
            RegisterToServer();
            return null;
        }
    }
}
