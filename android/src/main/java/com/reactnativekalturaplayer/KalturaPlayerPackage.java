package com.reactnativekalturaplayer;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.kaltura.tvplayer.KalturaOttPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KalturaPlayerPackage implements ReactPackage {

   public KalturaPlayerPackage() {
   }

   @Override
   public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      return Arrays.asList(new KalturaPlayerViewManager(reactContext));
   }

   @Override
   public List<NativeModule> createNativeModules(
           ReactApplicationContext reactContext) {

      return Collections.emptyList();
//
//        List<NativeModule> modules = new ArrayList<>();
//        modules.add(new KalturaPlayerModule(reactContext));
//        return modules;
   }
}


