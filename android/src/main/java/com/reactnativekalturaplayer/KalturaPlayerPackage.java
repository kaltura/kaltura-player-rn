package com.reactnativekalturaplayer;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KalturaPlayerPackage implements ReactPackage {

   public KalturaPlayerPackage() {
   }

   KalturaPlayerViewManager kalturaPlayerViewManager;

   @NonNull
   @Override
   public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
      if (kalturaPlayerViewManager == null) {
         kalturaPlayerViewManager = new KalturaPlayerViewManager(reactContext);
      }
      return Collections.singletonList(kalturaPlayerViewManager);
   }

   @NonNull
   @Override
   public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
      if (kalturaPlayerViewManager == null) {
         kalturaPlayerViewManager = new KalturaPlayerViewManager(reactContext);
      }
      List<NativeModule> modules = new ArrayList<>();
      modules.add(new KalturaPlayerModule(reactContext, kalturaPlayerViewManager));
      return modules;
   }
}


