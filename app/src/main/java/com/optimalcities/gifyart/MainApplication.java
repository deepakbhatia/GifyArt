package com.optimalcities.gifyart;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

/**
 * Created by obelix on 03/02/2016.
 */

//keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64

public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

    }
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

}
