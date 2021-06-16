package com.example.fcontreras.augercsachecker;


import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity implements FragmentoLista.ToActivityMain, FragmentoRegisterUser.MainRegisterUser, FragmentoMonitor.MainMonitor {

    RecyclerView lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Checking if we have configuration stuffs
        SharedPreferences preferences = getSharedPreferences("general", MODE_PRIVATE);
        String user = preferences.getString("User", "");
        Boolean valid = preferences.getBoolean("Valid", false);





        //Toast.makeText(this,"Token: "+preferences.getString("Token", ""), Toast.LENGTH_LONG).show();
        Log.i("Debug", "Valid: "+valid+ " user: "+user+" user.equals "+user.equals(""));

        if (!(user.equals("")) && valid) {
            // Getting parent parameter
            Bundle b = getIntent().getExtras();
            int parent;
            if (b != null) parent= b.getInt("PARENT");
            else parent = 0;
            Log.i("Debug", "Parent variable received : "+parent);


            // Fragment creation
            FragmentoLista listaFragmento = new FragmentoLista();
            Bundle args = new Bundle();
            args.putInt("PARENT", parent);
            listaFragmento.setArguments(args);
            fragmentTransaction.add(R.id.contenido, listaFragmento);
            fragmentTransaction.commit();
        }
        else if (user.equals("")){
            // We need to setup user, mail and montoring
            FragmentoRegisterUser registerUser = new FragmentoRegisterUser();
            fragmentTransaction.add(R.id.contenido, registerUser);
            fragmentTransaction.commit();
        }
        else if ( !valid ) {
            // It has a user registered, but it didn't be accepted yet
            FragmentoMensaje fragmentoMensaje = new FragmentoMensaje();
            fragmentTransaction.add(R.id.contenido, fragmentoMensaje);
            fragmentTransaction.commit();
        }

    }

    public void ReplaceFragment(int parent) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        FragmentoLista fragment = new FragmentoLista();
        Bundle args = new Bundle();
        args.putInt("PARENT", parent);
        fragment.setArguments(args);

        fragmentTransaction.replace(R.id.contenido, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    @Override
    public void PutMonitFragment(int position) {
        ReplaceFragment(position);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (item.getItemId()) {
            case R.id.settings:
                FragmentoConfig fragmentoConfig = new FragmentoConfig();
                ft.replace(R.id.contenido, fragmentoConfig);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.about:
                FragmentoAbout fragmentoAbout = new FragmentoAbout();
                ft.replace(R.id.contenido, fragmentoAbout);
                ft.addToBackStack(null);
                ft.commit();
                break;
             default:
                 break;
        }
        return true;
    }

    @Override
    public void ReplaceFragmentToMonitor() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentoMonitor fragmentoMonitor = new FragmentoMonitor();
        ft.replace(R.id.contenido, fragmentoMonitor);
        ft.commit();
    }

    @Override
    public void ReplaceFragmentToMain() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //FragmentoLista fragmento = new FragmentoLista();
        //Bundle args = new Bundle();
        //args.putInt("PARENT", 0);

        FragmentoMensaje fragmento = new FragmentoMensaje();
        //fragmento.setArguments(args);
        ft.replace(R.id.contenido, fragmento);
        ft.commit();
    }
}
