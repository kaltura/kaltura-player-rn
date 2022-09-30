package com.reactnativekalturaplayer

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext

class KalturaPlayerPackage: ReactPackage {

    private var kalturaPlayerViewManager: KalturaPlayerViewManager? = null

    override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
        if (kalturaPlayerViewManager == null) {
            kalturaPlayerViewManager = KalturaPlayerViewManager(reactContext)
        }
        val modules: MutableList<NativeModule> = ArrayList()
        modules.add(KalturaPlayerModule(reactContext, kalturaPlayerViewManager))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<KalturaPlayerViewManager?> {
        if (kalturaPlayerViewManager == null) {
            kalturaPlayerViewManager = KalturaPlayerViewManager(reactContext)
        }
        return listOf(kalturaPlayerViewManager)
    }
}
