package com.reactnativekalturaplayer

import android.text.TextUtils
import androidx.annotation.NonNull
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.kaltura.playkit.PKLog

class KalturaPlayerModule(
    reactApplicationContext: ReactApplicationContext,
    kalturaPlayerViewManager: KalturaPlayerViewManager?) : ReactContextBaseJavaModule(reactApplicationContext) {

    private val log = PKLog.get(KalturaPlayerModule::class.java.simpleName)
    private val PLAYER_CLASS = "KalturaPlayerModule"
    private lateinit var kalturaPlayerRNView: KalturaPlayerRNView

    private var context: ReactApplicationContext = reactApplicationContext
    private val kalturaPlayerRN: KalturaPlayerRN

    init {
        if (kalturaPlayerViewManager != null) {
            kalturaPlayerRNView = kalturaPlayerViewManager.kalturaPlayerView
        }

        kalturaPlayerRN = KalturaPlayerRN(context, kalturaPlayerRNView)
    }

    override fun getName(): String {
        return PLAYER_CLASS
    }

    @ReactMethod
    fun setUpPlayer(partnerId: Int = 0, initOptions: String?, callback: Callback) {
        log.d("setPartnerId: $partnerId")
        kalturaPlayerRN.createPlayerInstance(partnerId, initOptions, callback)
    }

    @ReactMethod
    fun load(assetId: String?, mediaAsset: String?) {
        log.d(
            "load assetId: " + assetId +
                    "\n , mediaAssetJson:" + mediaAsset
        )
        checkArguments(this::load.name, assetId)
        checkArguments(this::load.name, mediaAsset)
        kalturaPlayerRN.load(assetId, mediaAsset)
    }

    @ReactMethod
    fun onApplicationPaused() {
        log.d("onApplicationPaused")
        kalturaPlayerRN.onApplicationPaused()
    }

    @ReactMethod
    fun onApplicationResumed() {
        log.d("onApplicationResumed")
        kalturaPlayerRN.onApplicationResumed()
    }

    @ReactMethod
    fun updatePluginConfigs(pluginConfigJson: String?) {
        log.d("updatePluginConfigs")
        checkArguments(this::updatePluginConfigs.name, pluginConfigJson)
        kalturaPlayerRN.updatePluginConfigs(pluginConfigJson)
    }

    @ReactMethod
    fun play() {
        log.d("play")
        kalturaPlayerRN.play()
    }

    @ReactMethod
    fun pause() {
        log.d("pause")
        kalturaPlayerRN.pause()
    }

    @ReactMethod
    fun replay() {
        log.d("replay")
        kalturaPlayerRN.replay()
    }

    @ReactMethod
    fun seekTo(position: Float) {
        log.d("seekTo:$position")
        if (position > 0f) {
            kalturaPlayerRN.seekTo(position);
        } else {
            log.d("Invalid seek position which is $position")
        }
    }

    @ReactMethod
    fun changeTrack(uniqueId: String?) {
        log.d("changeTrack:$uniqueId")
        checkArguments(this::changeTrack.name, uniqueId)
        kalturaPlayerRN.changeTrack(uniqueId)
    }

    @ReactMethod
    fun changePlaybackRate(playbackRate: Float) {
        log.d("changePlaybackRate:$playbackRate")
        if (playbackRate > 0f) {
            kalturaPlayerRN.changePlaybackRate(playbackRate);
        } else {
            log.d("Invalid playback rate which is $playbackRate");
        }
    }

    @ReactMethod
    fun destroy() {
        log.d("destroy")
        kalturaPlayerRN.destroy()
    }

    @ReactMethod
    fun stop() {
        log.d("stop")
        kalturaPlayerRN.stop()
    }

    @ReactMethod
    fun setAutoplay(autoplay: Boolean) {
        log.d("setAutoplay: $autoplay")
        kalturaPlayerRN.setAutoplay(autoplay)
    }

    @ReactMethod
    fun setKS(ks: String) {
        log.d("setKS: $ks")
        checkArguments(this::setKS.name, ks)
        kalturaPlayerRN.setKS(ks)
    }

    //TODO: If required expose it
    fun setZIndex(index: Float) {
        log.d("setZIndex: $index")
        kalturaPlayerRN.setZIndex(index)
    }

    @ReactMethod
    fun setVolume(volume: Float) {
        log.d("setVolume: $volume")
        if (volume > 0f) {
            kalturaPlayerRN.setVolume(volume);
        } else {
            log.d("Invalid Volume which is $volume");
        }
    }

    @ReactMethod
    fun seekToLiveDefaultPosition() {
        log.d("seekToLiveDefaultPosition")
        kalturaPlayerRN.seekToLiveDefaultPosition()
    }

    @ReactMethod
    fun updateSubtitleStyle(subtitleStyleSettings: String?) {
        log.d("updateSubtitleStyle")
        checkArguments(this::updateSubtitleStyle.name, subtitleStyleSettings)
        kalturaPlayerRN.updateSubtitleStyle(subtitleStyleSettings)
    }

    @ReactMethod
    fun updateResizeMode(resizeMode: String) {
        log.d("updateResizeMode")
        checkArguments(this::updateResizeMode.name, resizeMode)
        kalturaPlayerRN.updateResizeMode(resizeMode)
    }

    @ReactMethod
    fun updateAbrSettings(abrSettings: String?) {
        log.d("updateAbrSettings")
        checkArguments(this::updateAbrSettings.name, abrSettings)
        kalturaPlayerRN.updateAbrSettings(abrSettings)
    }

    @ReactMethod
    fun resetAbrSettings() {
        log.d("resetAbrSettings")
        kalturaPlayerRN.resetAbrSettings()
    }

    @ReactMethod
    fun updateLlConfig(pkLowLatencyConfig: String?) {
        log.d("updateLlConfig")
        checkArguments(this::updateLlConfig.name, pkLowLatencyConfig)
        kalturaPlayerRN.updateLlConfig(pkLowLatencyConfig)
    }

    @ReactMethod
    fun resetLlConfig() {
        log.d("resetLlConfig")
        kalturaPlayerRN.resetLlConfig()
    }

    @ReactMethod
    fun removeKalturaPlayerListeners() {
        kalturaPlayerRN.removeKalturaPlayerListeners()
    }

    @ReactMethod
    fun addKalturaPlayerListeners() {
        log.d("addKalturaPlayerListeners")
        kalturaPlayerRN.addKalturaPlayerListeners()
    }

    @Throws(IllegalArgumentException::class)
    @NonNull
    private fun checkArguments(methodName: String, arg: String?) {
        if (TextUtils.isEmpty(arg)) {
            throw IllegalArgumentException("$methodName argument is invalid for ", Throwable("$arg is invalid"))
        }
    }
}
