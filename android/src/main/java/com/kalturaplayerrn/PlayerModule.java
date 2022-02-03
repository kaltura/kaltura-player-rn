package com.kalturaplayerrn;

import com.facebook.react.bridge.JavaScriptContextHolder;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class PlayerModule extends ReactContextBaseJavaModule {
  static {
    System.loadLibrary("kplayer");
  }

  private static native void nativeInstall(long jsiPtr);

  // called by KUXJSIModulePackage from main (JSI)
  public static void install(JavaScriptContextHolder jsContext, ReactApplicationContext reactContext) {
    PKPlayerWrapper.reactContext = reactContext;
    PKDownloadWrapper.reactContext = reactContext;
    nativeInstall(jsContext.get());
  }

  @NonNull
  @Override
  public String getName() {
    return "KPlayer";
  }

  // new JSI calls inside PlayerJSIHostObject.cpp
  // JS code:
  // const KPlayer = global.KPlayerCreateNewInstance();
  // KPlayer.pause();

  // old bridge calls
  // import { NativeModules } from 'react-native';
  // const { KPlayer } = NativeModules;
  // KPlayer.pause();
  @ReactMethod
  public void pause() {
  }
}
