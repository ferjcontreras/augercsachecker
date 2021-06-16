package com.example.fcontreras.augercsachecker;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;




public class MyFBMessagingService extends  FirebaseInstanceIdService{
    private static final String TAG = "TOKEN";

    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        RegisterToken(refreshedToken);
    }

    private void RegisterToken(String refreshedToken) {
        SharedPreferences preferences = getBaseContext().getSharedPreferences("general", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", refreshedToken);
        editor.apply();
    }
}
