package com.kalturaplayerrn;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.jetbrains.annotations.NotNull;



public class KalturaPlayerModule extends ReactContextBaseJavaModule {
    KalturaPlayerModule(ReactApplicationContext context) {
        super(context);
    }

    @NotNull
    @Override
    public String getName() {
        return "KalturaPlayerModule";
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void createKalturaPlayerEvent(String name, String location) {
        Log.d("KalturaPlayerModule", "Create event called with name: " + name
                + " and location: " + location);
    }

}