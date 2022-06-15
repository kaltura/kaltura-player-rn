package com.reactnativekalturaplayer

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.FrameLayout
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.kaltura.netkit.utils.ErrorElement
import com.kaltura.netkit.utils.NKLog
import com.kaltura.playkit.*
import com.kaltura.playkit.ads.PKAdErrorType
import com.kaltura.playkit.player.*
import com.kaltura.playkit.player.thumbnail.ThumbnailInfo
import com.kaltura.playkit.plugins.ads.AdCuePoints
import com.kaltura.playkit.plugins.ads.AdEvent
import com.kaltura.playkit.plugins.broadpeak.BroadpeakConfig
import com.kaltura.playkit.plugins.broadpeak.BroadpeakEvent
import com.kaltura.playkit.plugins.ima.IMAConfig
import com.kaltura.playkit.plugins.imadai.IMADAIConfig
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent
import com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig
import com.kaltura.playkit.utils.Consts
import com.kaltura.tvplayer.*
import com.npaw.youbora.lib6.YouboraLog
import com.reactnativekalturaplayer.events.KalturaPlayerAdEvents
import com.reactnativekalturaplayer.events.KalturaPlayerAnalyticsEvents
import com.reactnativekalturaplayer.events.KalturaPlayerEvents
import com.reactnativekalturaplayer.model.*
import com.reactnativekalturaplayer.model.tracks.TracksInfo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class KalturaPlayerRN(
    private val context: ReactApplicationContext,
    private val kalturaPlayerRNView: KalturaPlayerRNView): LifecycleEventListener {

    private val log = PKLog.get(KalturaPlayerRN::class.java.simpleName)

    private var player: KalturaPlayer? = null
    private var playerType: KalturaPlayer.Type? = null

    private var gson = Gson()
    private var reportedDuration = Consts.TIME_UNSET
    private var playerViewAdded = false
    private val YOUBORA_ACCOUNT_CODE = "accountCode"
    private var mainHandler: Handler? = null

    private fun runOnUiThread(runnable: Runnable) {
        mainHandler?.post(runnable)
    }

    fun createPlayerInstance(partnerId: Int, initOptions: String?, callback: Callback) {
        this.playerType = kalturaPlayerRNView.playerType

        log.d("createPlayerInstance PartnerId: $partnerId initOptions : $initOptions playerType: ${this.playerType}")

        if (partnerId > 0 && !TextUtils.isEmpty(initOptions) || playerType == KalturaPlayer.Type.basic) {
            if (playerType == KalturaPlayer.Type.basic) {
                createKalturaBasicPlayer(initOptions!!, callback)
            } else if (!TextUtils.isEmpty(initOptions) &&
                (playerType == KalturaPlayer.Type.ott || playerType == KalturaPlayer.Type.ovp)
            ) {
                createKalturaOttOvpPlayer(partnerId, initOptions!!, callback)
            } else {
                log.e("Player can not be created. playerType is $playerType and partnerId is $partnerId")
            }
        } else {
            log.e("PartnerId is not valid.")
        }
    }

    fun onApplicationPaused() {
        log.d("onApplicationPaused")
        runOnUiThread {
            player?.onApplicationPaused()
        }
    }

    fun onApplicationResumed() {
        log.d("onApplicationResumed")
        runOnUiThread {
            kalturaPlayerRNView?.reMeasureAndReLayout()
            player?.onApplicationResumed()
        }
    }

    fun updatePluginConfigs(pluginConfigJson: String?) {
        log.e("updatePluginConfigs")
        if (TextUtils.isEmpty(pluginConfigJson)) {
            log.e("pluginConfigJson is empty hence returning from here.")
            return
        }
        val pluginConfig = getParsedJson(
            pluginConfigJson,
            UpdatePluginConfigJson::class.java
        )
        if (pluginConfig != null &&
            !TextUtils.isEmpty(pluginConfig.pluginName) && pluginConfig.pluginConfig != null
        ) {
            val pluginName = pluginConfig.pluginName
            if (TextUtils.equals(pluginName, PlayerPlugins.ima.name)) {
                val imaConfig = getParsedJson(
                    pluginConfig.pluginConfig.toString(),
                    IMAConfig::class.java
                )
                if (imaConfig != null) {
                    updatePlugin(PlayerPlugins.ima, imaConfig)
                }
            } else if (TextUtils.equals(pluginName, PlayerPlugins.imadai.name)) {
                val imadaiConfig = getParsedJson(
                    pluginConfig.pluginConfig.toString(),
                    IMADAIConfig::class.java
                )
                if (imadaiConfig != null) {
                    updatePlugin(PlayerPlugins.imadai, imadaiConfig)
                }
            } else if (TextUtils.equals(pluginName, PlayerPlugins.youbora.name)) {
                val youboraConfig = getParsedJson(
                    pluginConfig.pluginConfig.toString(),
                    YouboraConfig::class.java
                )
                if (youboraConfig != null) {
                    updatePlugin(PlayerPlugins.youbora, youboraConfig)
                }
            } else if (TextUtils.equals(pluginName, PlayerPlugins.kava.name)) {
                val kavaAnalyticsConfig = getParsedJson(
                    pluginConfig.pluginConfig.toString(),
                    KavaAnalyticsConfig::class.java
                )
                if (kavaAnalyticsConfig != null) {
                    updatePlugin(PlayerPlugins.kava, kavaAnalyticsConfig)
                }
            } else if (TextUtils.equals(pluginName, PlayerPlugins.ottAnalytics.name)) {
                val phoenixAnalyticsConfig = getParsedJson(
                    pluginConfig.pluginConfig.toString(),
                    PhoenixAnalyticsConfig::class.java
                )
                if (phoenixAnalyticsConfig != null) {
                    updatePlugin(PlayerPlugins.ottAnalytics, phoenixAnalyticsConfig)
                }
            } else if (TextUtils.equals(pluginName, PlayerPlugins.broadpeak.name)) {
                val broadpeakConfig = getParsedJson(
                    pluginConfig.pluginConfig.toString(),
                    BroadpeakConfig::class.java
                )
                if (broadpeakConfig != null) {
                    updatePlugin(PlayerPlugins.broadpeak, broadpeakConfig)
                }
            } else {
                log.w("No Plugin can be registered PluginName is: $pluginName")
            }
        } else {
            log.e("Plugin config or Plugin Name is not valid.")
        }
    }

    fun play() {
        log.d("play")
        runOnUiThread {
            player?.let {
                if (!it.isPlaying) {
                    it.play()
                }
            }
        }
    }

    fun pause() {
        log.d("pause")
        runOnUiThread {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        }
    }

    fun replay() {
        log.d("replay")
        runOnUiThread {
            player?.replay()
        }
    }

    fun seekTo(position: Float) {
        val posMS = (position * Consts.MILLISECONDS_MULTIPLIER).toLong()
        log.d("seekTo:$posMS")
        runOnUiThread {
            player?.seekTo(posMS)
        }
    }

    fun changeTrack(uniqueId: String?) {
        log.d("changeTrack:$uniqueId")
        runOnUiThread {
            player?.changeTrack(uniqueId)
        }
    }

    fun changePlaybackRate(playbackRate: Float) {
        log.d("changePlaybackRate:$playbackRate")
        runOnUiThread {
            player?.playbackRate = playbackRate
        }
    }

    fun destroy() {
        log.d("destroy")
        runOnUiThread {
            player?.destroy()
            player = null
            playerViewAdded = false
            mainHandler = null
            removeLifeCycleEventListener(context)
        }

    }

    fun stop() {
        log.d("stop")
        runOnUiThread {
            player?.stop()
        }
    }

    fun setAutoplay(autoplay: Boolean) {
        log.d("setAutoplay: $autoplay")
        runOnUiThread {
            player?.isAutoPlay = autoplay
        }
    }

    fun setKS(ks: String) {
        log.d("setKS: $ks")
        runOnUiThread {
            player?.ks = ks
        }
    }

    fun setZIndex(index: Float) {
        log.d("setZIndex: $index")
        runOnUiThread {
            player?.let {
                it.playerView?.z = index
            }
        }
    }

    fun setVolume(volume: Float) {
        log.d("setVolume: $volume")
        var vol = volume
        if (volume < 0) {
            vol = 0f
        } else if (volume > 1) {
            vol = 1.0f
        }
        runOnUiThread {
            player?.setVolume(vol)
        }
    }

    fun seekToLiveDefaultPosition() {
        log.d("seekToLiveDefaultPosition")
        runOnUiThread {
            player?.seekToLiveDefaultPosition()
        }
    }

    fun updateSubtitleStyle(subtitleStyleSettings: String?) {
        log.d("updateSubtitleStyle")
        if (!TextUtils.isEmpty(subtitleStyleSettings)) {
            val subtitleStyling = getParsedJson(
                subtitleStyleSettings,
                SubtitleStyling::class.java
            )
            if (subtitleStyling != null) {
                val style = getParsedSubtitleStyleSettings(subtitleStyling)
                runOnUiThread {
                    player?.updateSubtitleStyle(style)
                }
            }
        }
    }

    fun updateResizeMode(resizeMode: String) {
        log.d("updateResizeMode")
        if (player != null && !TextUtils.isEmpty(resizeMode)) {
            try {
                runOnUiThread {
                    player?.updateSurfaceAspectRatioResizeMode(
                        PKAspectRatioResizeMode.valueOf(
                            resizeMode
                        )
                    )
                }
            } catch (exception: IllegalArgumentException) {
                log.e("Invalid resize mode is passed hence can not update it and resizeMode is $resizeMode")
            }
        }
    }

    fun updateAbrSettings(abrSettings: String?) {
        log.d("updateAbrSettings")
        if (!TextUtils.isEmpty(abrSettings)) {
            val settings = getParsedJson(abrSettings, ABRSettings::class.java)
            if (settings != null) {
                runOnUiThread {
                    player?.updateABRSettings(settings)
                }
            }
        }
    }

    fun resetAbrSettings() {
        log.d("resetAbrSettings")
        runOnUiThread {
            player?.resetABRSettings()
        }
    }

    fun updateLlConfig(pkLowLatencyConfig: String?) {
        log.d("updateLlConfig")
        if (!TextUtils.isEmpty(pkLowLatencyConfig)) {
            val config = getParsedJson(
                pkLowLatencyConfig,
                PKLowLatencyConfig::class.java
            )
            if (config != null) {
                runOnUiThread {
                    player?.updatePKLowLatencyConfig(config)
                }
            }
        }
    }

    fun resetLlConfig() {
        log.d("resetLlConfig")
        runOnUiThread {
            player?.updatePKLowLatencyConfig(PKLowLatencyConfig.UNSET)
        }
    }

    //TODO: NOT ADDED YET AS PROPS
    protected fun requestThumbnailInfo(positionMs: Float) {
        log.d("requestThumbnailInfo:$positionMs")
        if (player != null) {
            runOnUiThread {
                val thumbnailInfo: ThumbnailInfo = player?.getThumbnailInfo(positionMs.toLong())!!
                if (thumbnailInfo != null && positionMs >= 0) {
                    val thumbnailInfoJson =
                        "{ \"position\": " + positionMs + ", \"thumbnailInfo\": " + gson.toJson(
                            thumbnailInfo
                        ) + " }"
                    sendPlayerEvent(KalturaPlayerEvents.THUMBNAIL_INFO_RESPONSE, thumbnailInfoJson)
                } else {
                    log.e("requestThumbnailInfo: thumbnailInfo is null or position is invalid")
                }
            }
        }
    }

    // TODO: NOT ADDED YET AS PROPS
    protected fun setLogLevel(logLevel: String) {
        var logLevel = logLevel
        log.d("setLogLevel: $logLevel")
        if (TextUtils.isEmpty(logLevel)) {
            return
        }
        logLevel = logLevel.toUpperCase()
        when (logLevel) {
            "VERBOSE" -> {
                PKLog.setGlobalLevel(PKLog.Level.verbose)
                NKLog.setGlobalLevel(NKLog.Level.verbose)
                YouboraLog.setDebugLevel(YouboraLog.Level.VERBOSE)
            }
            "DEBUG" -> {
                PKLog.setGlobalLevel(PKLog.Level.debug)
                NKLog.setGlobalLevel(NKLog.Level.debug)
                YouboraLog.setDebugLevel(YouboraLog.Level.DEBUG)
            }
            "WARN" -> {
                PKLog.setGlobalLevel(PKLog.Level.warn)
                NKLog.setGlobalLevel(NKLog.Level.warn)
                YouboraLog.setDebugLevel(YouboraLog.Level.WARNING)
            }
            "INFO" -> {
                PKLog.setGlobalLevel(PKLog.Level.info)
                NKLog.setGlobalLevel(NKLog.Level.info)
                YouboraLog.setDebugLevel(YouboraLog.Level.NOTICE)
            }
            "ERROR" -> {
                PKLog.setGlobalLevel(PKLog.Level.error)
                NKLog.setGlobalLevel(NKLog.Level.error)
                YouboraLog.setDebugLevel(YouboraLog.Level.ERROR)
            }
            "OFF" -> {
                PKLog.setGlobalLevel(PKLog.Level.off)
                NKLog.setGlobalLevel(NKLog.Level.off)
                YouboraLog.setDebugLevel(YouboraLog.Level.SILENT)
            }
            else -> {
                PKLog.setGlobalLevel(PKLog.Level.off)
                NKLog.setGlobalLevel(NKLog.Level.off)
                YouboraLog.setDebugLevel(YouboraLog.Level.SILENT)
            }
        }
    }

    private fun createKalturaBasicPlayer(initOptions: String, callback: Callback) {
        log.d("Creating Basic Player instance.")
        val initOptionsModel = getParsedJson(initOptions, InitOptions::class.java)
        val playerInitOptions = PlayerInitOptions()
        if (initOptionsModel == null) {
            playerInitOptions.setAutoPlay(true)
            playerInitOptions.setPKRequestConfig(PKRequestConfig(true))
        } else {
            setCommonPlayerInitOptions(playerInitOptions, initOptionsModel)
            val pkPluginConfigs = createPluginConfigs(initOptionsModel)
            playerInitOptions.setPluginConfigs(pkPluginConfigs)
        }

        createUiHandler()
        addLifeCycleEventListener(context)

        runOnUiThread {
            if (player == null) {
                player = KalturaBasicPlayer.create(context, playerInitOptions)
            }

            callback.invoke(true)

            initDrm(context)

            addPlayerViewToRNView(player)
        }
    }

    private fun createUiHandler() {
        mainHandler = Handler(Looper.getMainLooper())
    }

    private fun addLifeCycleEventListener(context: ReactApplicationContext) {
        context.addLifecycleEventListener(this)
    }

    private fun removeLifeCycleEventListener(context: ReactApplicationContext) {
        context.removeLifecycleEventListener(this)
    }

    override fun onHostResume() {
        // This is being controlled by the RN FE apps
    }

    override fun onHostPause() {
        // This is being controlled by the RN FE apps
    }

    override fun onHostDestroy() {
        destroy()
    }

    private fun addPlayerViewToRNView(kalturaPlayer: KalturaPlayer?) {
        if (!playerViewAdded && kalturaPlayer != null) {
            kalturaPlayer.setPlayerView(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            kalturaPlayerRNView?.addView(kalturaPlayer.playerView)
            playerViewAdded = true
        }
    }

    private fun createMediaEntry(url: String, basicMediaAsset: BasicMediaAsset): PKMediaEntry {
        log.d("createMediaEntry URL is: $url")

        //Create media entry.
        val mediaEntry = PKMediaEntry()
        //Set id for the entry.
        mediaEntry.id = basicMediaAsset.id
        mediaEntry.name = basicMediaAsset.name
        mediaEntry.duration = basicMediaAsset.duration
        mediaEntry.mediaType = basicMediaAsset.mediaEntryType
        mediaEntry.setIsVRMediaType(basicMediaAsset.isVRMediaType)
        if (basicMediaAsset.externalSubtitleList != null && !basicMediaAsset.externalSubtitleList.isEmpty()) {
            mediaEntry.externalSubtitleList = basicMediaAsset.externalSubtitleList
        }
        if (basicMediaAsset.metadata != null && !basicMediaAsset.metadata.isEmpty()) {
            mediaEntry.metadata = basicMediaAsset.metadata
        }
        if (!TextUtils.isEmpty(basicMediaAsset.externalVttThumbnailUrl)) {
            mediaEntry.externalVttThumbnailUrl = basicMediaAsset.externalVttThumbnailUrl
        }
        val mediaSources = createMediaSources(url, basicMediaAsset)
        mediaEntry.sources = mediaSources
        return mediaEntry
    }

    /**
     * Create list of [PKMediaSource].
     * @return - the list of sources.
     */
    private fun createMediaSources(
        url: String,
        basicMediaAsset: BasicMediaAsset
    ): List<PKMediaSource> {
        log.d("createMediaSources URL is: $url")
        //Create new PKMediaSource instance.
        val mediaSource = PKMediaSource()
        //Set the id.
        mediaSource.id = "basicPlayerTestSource"
        //Set the content url.
        mediaSource.url = url
        //Set the format of the source.
        mediaSource.mediaFormat = basicMediaAsset.mediaFormat
        // Set the DRM Params if available
        setDrmParams(basicMediaAsset.drmData, mediaSource)
        return listOf(mediaSource)
    }

    private fun setDrmParams(pkDrmParamsList: List<PKDrmParams>?, mediaSource: PKMediaSource?) {
        if (mediaSource != null && pkDrmParamsList != null && !pkDrmParamsList.isEmpty()) {
            mediaSource.drmData = pkDrmParamsList
        }
    }

    private fun createKalturaOttOvpPlayer(partnerId: Int, playerInitOptionsJson: String, callback: Callback) {
        log.d("createKalturaOttOvpPlayer:$partnerId, \n initOptions: \n $playerInitOptionsJson")
        val initOptionsModel = getParsedJson(
            playerInitOptionsJson,
            InitOptions::class.java
        )
        if (initOptionsModel == null || TextUtils.isEmpty(initOptionsModel.serverUrl) || playerType == null || playerType == KalturaPlayer.Type.basic) {
            // TODO : write log message
            return
        }

        // load the player and put it in the main frame
        if (playerType == KalturaPlayer.Type.ott) {
            KalturaOttPlayer.initialize(context, partnerId, initOptionsModel.serverUrl)
        } else {
            KalturaOvpPlayer.initialize(context, partnerId, initOptionsModel.serverUrl)
        }
        if (initOptionsModel.warmupUrls != null && !initOptionsModel.warmupUrls.isEmpty()) {
            PKHttpClientManager.setHttpProvider("okhttp")
//            PKHttpClientManager.warmUp(
//                *initOptionsModel.warmupUrls.toArray<String>(
//                    arrayOfNulls<String>(
//                        0
//                    )
//                )
//            )
        }
        val playerInitOptions = PlayerInitOptions(partnerId)
        playerInitOptions.setKs(initOptionsModel.ks)
        playerInitOptions.setMediaEntryCacheConfig(initOptionsModel.mediaEntryCacheConfig)
        setCommonPlayerInitOptions(playerInitOptions, initOptionsModel)
        val pkPluginConfigs = createPluginConfigs(initOptionsModel)
        playerInitOptions.setPluginConfigs(pkPluginConfigs)

        createUiHandler()
        addLifeCycleEventListener(context)

        //playerInitOptions.setVideoCodecSettings(appPlayerInitConfig.videoCodecSettings)
        //playerInitOptions.setAudioCodecSettings(appPlayerInitConfig.audioCodecSettings)
        if (player == null && playerType == KalturaPlayer.Type.ott) {
            player = KalturaOttPlayer.create(context, playerInitOptions)
        }
        if (player == null && playerType == KalturaPlayer.Type.ovp) {
            player = KalturaOvpPlayer.create(context, playerInitOptions)
        }
        initDrm(context)

        addPlayerViewToRNView(player)
    }

    fun load(assetId: String?, mediaAssetJson: String?) {
        log.d(
            "load assetId: " + assetId +
                    "\n player type: " + playerType +
                    "\n , mediaAssetJson:" + mediaAssetJson
        )
        if (player == null) {
            log.e("Player instance is null while loading the media. Hence returning.")
            return
        }
        if (playerType == KalturaPlayer.Type.basic || isBasicPlaybackRequired(mediaAssetJson)) {
            val basicMediaAsset = getParsedJson(
                mediaAssetJson,
                BasicMediaAsset::class.java
            )
            if (basicMediaAsset == null || basicMediaAsset.mediaFormat == null) {
                log.e("Invalid Media Asset for player type $playerType \n and media asset is $basicMediaAsset")
                return
            }
            val mediaEntry = createMediaEntry(assetId!!, basicMediaAsset)
            runOnUiThread {
                player?.setMedia(mediaEntry, basicMediaAsset.startPosition)
            }
        } else if (playerType == KalturaPlayer.Type.ott || playerType == KalturaPlayer.Type.ovp) {
            val mediaAsset = getParsedJson(mediaAssetJson, MediaAsset::class.java)
            if (mediaAsset == null || player == null) {
                log.e("Invalid Media Asset for player type $playerType \n and media asset is $mediaAsset")
                return
            }
            if (playerType == KalturaPlayer.Type.ott) {
                runOnUiThread {
                    val ottMediaOptions = mediaAsset.buildOttMediaOptions(assetId, player?.getKS())
                    player?.loadMedia(ottMediaOptions) { mediaOptions: MediaOptions?, entry: PKMediaEntry, error: ErrorElement? ->
                        if (error != null) {
                            log.e("ott media load error: " + error.getName() + " " + error.getCode() + " " + error.getMessage())
                            sendPlayerEvent(
                                KalturaPlayerEvents.LOAD_MEDIA_FAILED,
                                gson.toJson(error)
                            )
                        } else {
                            log.d("ott media load success name = " + entry.getName() + " initialVolume = " + mediaAsset.getInitialVolume())
                            sendPlayerEvent(
                                KalturaPlayerEvents.LOAD_MEDIA_SUCCESS,
                                gson.toJson(entry)
                            )
                            if (mediaAsset.getInitialVolume() >= 0 && mediaAsset.getInitialVolume() < 1.0) {
                                player?.setVolume(mediaAsset.getInitialVolume())
                            }
                        }
                    }
                }
            } else {
                runOnUiThread {
                    val ovpMediaOptions = mediaAsset.buildOvpMediaOptions(assetId, "", player?.getKS())

                    player?.loadMedia(ovpMediaOptions) { mediaOptions: MediaOptions?, entry: PKMediaEntry, error: ErrorElement? ->
                        if (error != null) {
                            log.e("ovp media load error: " + error.getName() + " " + error.getCode() + " " + error.getMessage())
                            sendPlayerEvent(
                                KalturaPlayerEvents.LOAD_MEDIA_FAILED,
                                gson.toJson(error)
                            )
                        } else {
                            log.d("ovp media load success name = " + entry.getName() + " initialVolume = " + mediaAsset.getInitialVolume())
                            sendPlayerEvent(
                                KalturaPlayerEvents.LOAD_MEDIA_SUCCESS,
                                gson.toJson(entry)
                            )
                            if (mediaAsset.getInitialVolume() >= 0 && mediaAsset.getInitialVolume() < 1.0) {
                                player?.setVolume(mediaAsset.getInitialVolume())
                            }
                        }
                    }
                }
            }
        } else {
            log.e("No Player type defined hence can not load the media. PlayerType $playerType")
        }
    }

    /**
     * PlayerInitOptions which can be used for
     * OVP, OTT and Basic Player types
     *
     * @param playerInitOptions PlayerInitOptions
     * @param initOptionsModel InitOptions model passed by FE apps
     */
    private fun setCommonPlayerInitOptions(
        playerInitOptions: PlayerInitOptions,
        initOptionsModel: InitOptions
    ) {
        playerInitOptions.setAutoPlay(initOptionsModel.autoplay)
        playerInitOptions.setPreload(initOptionsModel.preload)
        if (initOptionsModel.requestConfig != null) {
            playerInitOptions.setPKRequestConfig(initOptionsModel.requestConfig)
        } else {
            playerInitOptions.setAllowCrossProtocolEnabled(initOptionsModel.allowCrossProtocolRedirect)
        }
        playerInitOptions.setReferrer(initOptionsModel.referrer)
        playerInitOptions.setPKLowLatencyConfig(initOptionsModel.lowLatencyConfig)
        playerInitOptions.setAbrSettings(initOptionsModel.abrSettings)
        playerInitOptions.setPreferredMediaFormat(initOptionsModel.preferredMediaFormat)
        playerInitOptions.setSecureSurface(initOptionsModel.secureSurface)
        playerInitOptions.setAspectRatioResizeMode(initOptionsModel.aspectRatioResizeMode)
        playerInitOptions.setAllowClearLead(initOptionsModel.allowClearLead)
        playerInitOptions.setEnableDecoderFallback(initOptionsModel.enableDecoderFallback)
        playerInitOptions.setAdAutoPlayOnResume(initOptionsModel.adAutoPlayOnResume)
        playerInitOptions.setIsVideoViewHidden(initOptionsModel.isVideoViewHidden)
        playerInitOptions.forceSinglePlayerEngine(initOptionsModel.forceSinglePlayerEngine)
        playerInitOptions.setTunneledAudioPlayback(initOptionsModel.isTunneledAudioPlayback)
        playerInitOptions.setMaxAudioBitrate(initOptionsModel.maxAudioBitrate)
        playerInitOptions.setMaxAudioChannelCount(initOptionsModel.maxAudioChannelCount)
        playerInitOptions.setMaxVideoBitrate(initOptionsModel.maxVideoBitrate)
        playerInitOptions.setMaxVideoSize(initOptionsModel.maxVideoSize)
        playerInitOptions.setHandleAudioBecomingNoisy(initOptionsModel.handleAudioBecomingNoisyEnabled)
        playerInitOptions.setHandleAudioFocus(initOptionsModel.handleAudioFocus)
        playerInitOptions.setMulticastSettings(initOptionsModel.multicastSettings)
        if (initOptionsModel.networkSettings != null && initOptionsModel.networkSettings.preferredForwardBufferDuration > 0) {
            playerInitOptions.setLoadControlBuffers(
                LoadControlBuffers().setMaxPlayerBufferMs(
                    initOptionsModel.networkSettings.preferredForwardBufferDuration
                )
            )
        }
        if ((initOptionsModel.trackSelection != null) && (initOptionsModel.trackSelection.audioLanguage != null) && (initOptionsModel.trackSelection.audioMode != null)) {
            playerInitOptions.setAudioLanguage(
                initOptionsModel.trackSelection.audioLanguage,
                initOptionsModel.trackSelection.audioMode
            )
        }
        if ((initOptionsModel.trackSelection != null) && (initOptionsModel.trackSelection.textLanguage != null) && (initOptionsModel.trackSelection.textMode != null)) {
            playerInitOptions.setTextLanguage(
                initOptionsModel.trackSelection.textLanguage,
                initOptionsModel.trackSelection.textMode
            )
        }
        val subtitleStyleSettings = getParsedSubtitleStyleSettings(initOptionsModel.subtitleStyling)
        if (subtitleStyleSettings != null) {
            playerInitOptions.setSubtitleStyle(subtitleStyleSettings)
        }
        if (initOptionsModel.wakeMode != null && !TextUtils.isEmpty(initOptionsModel.wakeMode.toString())) {
            try {
                playerInitOptions.setWakeMode(PKWakeMode.valueOf(initOptionsModel.wakeMode.toString()))
            } catch (exception: IllegalArgumentException) {
                log.e("Illegal wake mode passed which is " + initOptionsModel.wakeMode.toString())
            }
        }
        if (initOptionsModel.subtitlePreference != null && !TextUtils.isEmpty(initOptionsModel.subtitlePreference.toString())) {
            try {
                playerInitOptions.setSubtitlePreference(
                    PKSubtitlePreference.valueOf(
                        initOptionsModel.subtitlePreference.toString()
                    )
                )
            } catch (exception: IllegalArgumentException) {
                log.e("Illegal subtitlePreference passed which is " + initOptionsModel.subtitlePreference.toString())
            }
        }
        if (initOptionsModel.videoCodecSettings != null) {
            val videoCodecSettings = VideoCodecSettings()
            videoCodecSettings.allowMixedCodecAdaptiveness =
                initOptionsModel.videoCodecSettings.allowMixedCodecAdaptiveness
            videoCodecSettings.isAllowSoftwareDecoder =
                initOptionsModel.videoCodecSettings.isAllowSoftwareDecoder
            if (initOptionsModel.videoCodecSettings.codecPriorityList != null &&
                !initOptionsModel.videoCodecSettings.codecPriorityList.isEmpty()
            ) {
                videoCodecSettings.codecPriorityList =
                    initOptionsModel.videoCodecSettings.codecPriorityList
            }
            playerInitOptions.setVideoCodecSettings(videoCodecSettings)
        }
        if (initOptionsModel.audioCodecSettings != null) {
            val audioCodecSettings = AudioCodecSettings()
            audioCodecSettings.allowMixedCodecs =
                initOptionsModel.audioCodecSettings.allowMixedCodecs
            audioCodecSettings.allowMixedBitrates =
                initOptionsModel.audioCodecSettings.allowMixedBitrates
            if (initOptionsModel.audioCodecSettings.codecPriorityList != null &&
                !initOptionsModel.audioCodecSettings.codecPriorityList.isEmpty()
            ) {
                audioCodecSettings.codecPriorityList =
                    initOptionsModel.audioCodecSettings.codecPriorityList
            }
            playerInitOptions.setAudioCodecSettings(audioCodecSettings)
        }
        if (initOptionsModel.loadControlBuffers != null) {
            playerInitOptions.setLoadControlBuffers(initOptionsModel.loadControlBuffers)
        }
        if (initOptionsModel.vrSettings != null) {
            playerInitOptions.setVRSettings(initOptionsModel.vrSettings)
        }
    }

    /**
     * Create `PKPluginConfigs` object for `PlayerInitOptions`
     *
     * @param initOptions class which contains all the configuration for PlayerInitOptions
     * @return `PKPluginConfig` object
     */
    private fun createPluginConfigs(initOptions: InitOptions): PKPluginConfigs {
        val pkPluginConfigs = PKPluginConfigs()
        if (initOptions.plugins != null) {
            if (initOptions.plugins.ima != null) {
                createPlugin(PlayerPlugins.ima, pkPluginConfigs, initOptions.plugins.ima)
            }
            if (initOptions.plugins.imadai != null) {
                createPlugin(PlayerPlugins.imadai, pkPluginConfigs, initOptions.plugins.imadai)
            }
            if (initOptions.plugins.youbora != null) {
                val youboraConfigJson = initOptions.plugins.youbora
                if (youboraConfigJson!!.has(YOUBORA_ACCOUNT_CODE) && youboraConfigJson[YOUBORA_ACCOUNT_CODE] != null) {
                    createPlugin(
                        PlayerPlugins.youbora,
                        pkPluginConfigs,
                        initOptions.plugins.youbora
                    )
                }
            }
            if (initOptions.plugins.kava != null) {
                createPlugin(PlayerPlugins.kava, pkPluginConfigs, initOptions.plugins.kava)
            }
            if (initOptions.plugins.ottAnalytics != null) {
                createPlugin(
                    PlayerPlugins.ottAnalytics,
                    pkPluginConfigs,
                    initOptions.plugins.ottAnalytics
                )
            }
            if (initOptions.plugins.broadpeak != null) {
                createPlugin(
                    PlayerPlugins.broadpeak,
                    pkPluginConfigs,
                    initOptions.plugins.broadpeak
                )
            }
        }
        return pkPluginConfigs
    }

    /**
     * This method checks if the Player type is OVP/OTT
     * but still app wants to play a media using URL or DRM license URL
     * instead of using our backend.
     *
     * @return `true` if basic playback required
     */
    private fun isBasicPlaybackRequired(basicMediaAsset: String?): Boolean {
        if ((basicMediaAsset != null) && (playerType != null) && (playerType != KalturaPlayer.Type.basic)) {
            val mediaAsset = getParsedJson(
                basicMediaAsset,
                BasicMediaAsset::class.java
            )
            return mediaAsset != null && mediaAsset.mediaFormat != null
        }
        return false
    }

    private fun initDrm(context: ReactApplicationContext) {
        context.runOnNativeModulesQueueThread {
            MediaSupport.initializeDrm(context,
                MediaSupport.DrmInitCallback { pkDeviceSupportInfo: PKDeviceCapabilitiesInfo, provisionError: Exception? ->
                    if (pkDeviceSupportInfo.isProvisionPerformed()) {
                        if (provisionError != null) {
                            log.e("DRM Provisioning failed" + provisionError)
                        } else {
                            log.d("DRM Provisioning succeeded")
                        }
                    }
                    log.d("DRM initialized; supported: " + pkDeviceSupportInfo.getSupportedDrmSchemes() + " isHardwareDrmSupported = " + pkDeviceSupportInfo.isHardwareDrmSupported())
                    val gson: Gson = Gson()
                    sendPlayerEvent(
                        KalturaPlayerEvents.DRM_INITIALIZED,
                        gson.toJson(pkDeviceSupportInfo)
                    )
                })
        }
    }

    private fun getTracksInfo(pkTracksInfo: PKTracks): TracksInfo? {
        val videoTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.VideoTrack> =
            ArrayList()
        val audioTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.AudioTrack> =
            ArrayList()
        val textTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.TextTrack> =
            ArrayList()
        val imageTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.ImageTrack> =
            ArrayList()
        var videoTrackIndex = 0
        for (videoTrack: VideoTrack in pkTracksInfo.videoTracks) {
            videoTracksInfo.add(
                com.reactnativekalturaplayer.model.tracks.VideoTrack(
                    videoTrack.uniqueId,
                    videoTrack.width,
                    videoTrack.height,
                    videoTrack.bitrate,
                    pkTracksInfo.defaultVideoTrackIndex == videoTrackIndex,
                    videoTrack.isAdaptive
                )
            )
            videoTrackIndex++
        }
        var audioTrackIndex = 0
        for (audioTrack: AudioTrack in pkTracksInfo.audioTracks) {
            audioTracksInfo.add(
                com.reactnativekalturaplayer.model.tracks.AudioTrack(
                    audioTrack.uniqueId,
                    audioTrack.bitrate,
                    audioTrack.language,
                    audioTrack.label,
                    audioTrack.channelCount,
                    pkTracksInfo.defaultAudioTrackIndex == audioTrackIndex
                )
            )
            audioTrackIndex++
        }
        var textTrackIndex = 0
        for (textTrack: TextTrack in pkTracksInfo.textTracks) {
            textTracksInfo.add(
                com.reactnativekalturaplayer.model.tracks.TextTrack(
                    textTrack.uniqueId,
                    textTrack.language,
                    textTrack.label,
                    pkTracksInfo.defaultTextTrackIndex == textTrackIndex
                )
            )
            textTrackIndex++
        }
        var imageTrackIndex = 0
        for (imageTrack: ImageTrack in pkTracksInfo.imageTracks) {
            imageTracksInfo.add(
                com.reactnativekalturaplayer.model.tracks.ImageTrack(
                    imageTrack.uniqueId,
                    imageTrack.label,
                    imageTrack.bitrate,
                    imageTrack.width,
                    imageTrack.height,
                    imageTrack.cols,
                    imageTrack.rows,
                    imageTrack.duration,
                    imageTrack.url,
                    imageTrackIndex == 0
                )
            )
            imageTrackIndex++
        }
        val tracksInfo = TracksInfo()
        tracksInfo.setVideoTracks(videoTracksInfo)
        tracksInfo.setAudioTracks(audioTracksInfo)
        tracksInfo.setTextTracks(textTracksInfo)
        tracksInfo.setImageTracks(imageTracksInfo)
        return tracksInfo
    }

    /**
     * NOOP
     */
    private fun getErrorJson(error: PKError): String? {
        val errorCause =
            if ((error.exception != null)) error.exception!!.cause.toString() + "" else ""
        val errorJson = JsonObject()
        errorJson.addProperty("errorType", error.errorType.name)
        if (error.errorType is PKPlayerErrorType) {
            errorJson.addProperty(
                "errorCode",
                (error.errorType as PKPlayerErrorType).errorCode.toString()
            )
        } else if (error.errorType is PKAdErrorType) {
            errorJson.addProperty(
                "errorCode",
                (error.errorType as PKAdErrorType).errorCode.toString()
            )
        } else {
            errorJson.addProperty("errorCode", PKPlayerErrorType.UNEXPECTED.errorCode.toString())
        }
        errorJson.addProperty("errorSeverity", error.severity.name)
        errorJson.addProperty("errorMessage", error.message)
        errorJson.addProperty("errorCause", errorCause)
        return gson.toJson(errorJson)
    }

    /**
     * NOOP
     */
    private fun getCuePointsJson(adCuePoints: AdCuePoints?): String? {
        if (adCuePoints == null) {
            return null
        }
        val cuePointsList = StringBuilder("[ ")
        val adCuePointsArray = adCuePoints.adCuePoints
        for (i in adCuePointsArray.indices) {
            cuePointsList.append(adCuePointsArray[i])
            if (i + 1 != adCuePointsArray.size) {
                cuePointsList.append(", ")
            }
        }
        cuePointsList.append(" ]")
        return ("{ " +
                "\"adPluginName\": \"" + adCuePoints.adPluginName +
                "\"," +
                "\"cuePoints\": " + cuePointsList +
                ", " +
                "\"hasPreRoll\": " + adCuePoints.hasPreRoll() +
                ", " +
                "\"hasMidRoll\": " + adCuePoints.hasMidRoll() +
                "," +
                "\"hasPostRoll\": " + adCuePoints.hasPostRoll() +
                " }")
    }

    /**
     * Method to register and set the plugin config for the first time.
     * Method can not be used to update the plugin config
     *
     * @param pluginName `PlayerPlugins` enum
     * @param pluginConfigs `PKPluginConfigs` object for the `PlayerInitOptions`
     * @param pluginConfigJson plugin configuration json
     */
    private fun createPlugin(
        pluginName: PlayerPlugins,
        pluginConfigs: PKPluginConfigs?,
        pluginConfigJson: JsonObject?
    ) {
        PlayKitManager.registerPlugins(context, getPluginFactory(pluginName))
        if (pluginConfigJson == null || pluginConfigJson.size() == 0) {
            log.w("Plugins' config Json is not valid hence returning" + getPluginClass(pluginName))
            return
        }
        val strPluginJson = pluginConfigJson.toString()
        if (pluginConfigs != null && !TextUtils.isEmpty(strPluginJson)) {
            val pluginConfig = getParsedJson(strPluginJson, getPluginClass(pluginName))
            if (pluginConfig != null) {
                pluginConfigs.setPluginConfig(getPluginFactory(pluginName).name, pluginConfig)
            } else {
                log.e("Invalid configuration for " + getPluginClass(pluginName).simpleName)
            }
        } else {
            log.e(
                ("Can not create the plugin " + getPluginClass(pluginName).simpleName + " \n " +
                        "pluginConfig is: " + pluginConfigs + " imaConfig json is: " + pluginConfigJson)
            )
        }
    }

    /**
     * Method to update the plugin config on Player
     *
     * @param pluginName `PlayerPlugins` enum
     * @param updatePluginConfig updated plugin configuration json
     */
    private fun updatePlugin(pluginName: PlayerPlugins, updatePluginConfig: Any?) {
        val pluginFactory = getPluginFactory(pluginName)
        if ((player != null) && (updatePluginConfig != null) && !TextUtils.isEmpty(pluginFactory.name)) {
            runOnUiThread {
                player?.updatePluginConfig(pluginFactory.name, updatePluginConfig)
            }
        }
    }

    /**
     * Parse the [SubtitleStyling] object to
     * Player's [SubtitleStyleSettings] object
     *
     * @param subtitleStyling styling object from app
     * @return SubtitleStyleSettings object
     */
    private fun getParsedSubtitleStyleSettings(subtitleStyling: SubtitleStyling?): SubtitleStyleSettings? {
        var subtitleStyleSettings: SubtitleStyleSettings? = null
        if (subtitleStyling != null) {
            subtitleStyleSettings = SubtitleStyleSettings(subtitleStyling.subtitleStyleName)
                .setBackgroundColor(subtitleStyling.getStringToColor(subtitleStyling.subtitleBackgroundColor))
                .setTextColor(subtitleStyling.getStringToColor(subtitleStyling.subtitleTextColor))
                .setWindowColor(subtitleStyling.getStringToColor(subtitleStyling.subtitleWindowColor))
                .setEdgeColor(subtitleStyling.getStringToColor(subtitleStyling.subtitleEdgeColor))
                .setTextSizeFraction(subtitleStyling.getSubtitleTextSizeFraction())
                .setTypeface(subtitleStyling.getSubtitleStyleTypeface())
                .setEdgeType(subtitleStyling.getSubtitleEdgeType())
            val pkSubtitlePosition = PKSubtitlePosition(subtitleStyling.overrideInlineCueConfig)
            if (subtitleStyling.horizontalPositionPercentage == null && subtitleStyling.verticalPositionPercentage != null) {
                pkSubtitlePosition.setVerticalPosition(subtitleStyling.verticalPositionPercentage!!)
            } else if (subtitleStyling.horizontalPositionPercentage != null &&
                subtitleStyling.verticalPositionPercentage != null
            ) {
                pkSubtitlePosition.setPosition(
                    subtitleStyling.horizontalPositionPercentage!!,
                    subtitleStyling.verticalPositionPercentage!!,
                    subtitleStyling.getHorizontalAlignment()
                )
            }
            subtitleStyleSettings.setSubtitlePosition(pkSubtitlePosition)
        }
        return subtitleStyleSettings
    }

    private fun <T> getParsedJson(parsableJson: String?, parsingClass: Class<T>): T? {
        if (TextUtils.isEmpty(parsableJson)) {
            log.e("getParsedJson parsable Json is empty.")
            return null
        }
        try {
            return gson.fromJson(parsableJson, parsingClass)
        } catch (exception: JsonSyntaxException) {
            log.e(
                ("JsonSyntaxException while parsing " + parsingClass.simpleName + "\n and the exception is \n" +
                        exception.message)
            )
        }
        return null
    }

    fun removeKalturaPlayerListeners() {
        runOnUiThread {
            player?.removeListeners(this)
            log.d("Player listeners are removed.")
        }
    }

    fun addKalturaPlayerListeners() {
        log.d("addKalturaPlayerListeners")
        if (player == null) {
            log.d("Player is null. Not able to add the Kaltura Player Listeners hence returning.")
            return
        }
        log.d("Player listeners are added.")
        player?.addListener(context, PlayerEvent.canPlay) {
            sendPlayerEvent(
                KalturaPlayerEvents.CAN_PLAY
            )
        }

        player?.addListener(context, PlayerEvent.playing,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.PLAYING) })
        player?.addListener(context, PlayerEvent.play,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.PLAY) })
        player?.addListener(context, PlayerEvent.pause,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.PAUSE) })
        player?.addListener(context, PlayerEvent.ended,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.ENDED) })
        player?.addListener(context, PlayerEvent.stopped,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.STOPPED) })
        player?.addListener<PlayerEvent.DurationChanged>(context, PlayerEvent.durationChanged,
            PKEvent.Listener { event: PlayerEvent.DurationChanged ->
                reportedDuration = event.duration
                val durationJson: String = createJSONForEventPayload(
                    "duration",
                    (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
                )
                sendPlayerEvent(KalturaPlayerEvents.DURATION_CHANGE, durationJson)
            })
        player?.addListener<PlayerEvent.PlayheadUpdated>(context, PlayerEvent.playheadUpdated,
            PKEvent.Listener { event: PlayerEvent.PlayheadUpdated ->
                var timeUpdatePayload: String =
                    ("\"position\": " + (event.position / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                            ", \"bufferPosition\": " + (event.bufferPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT))
                if ((player != null) && player?.isLive()!! && (player?.getCurrentProgramTime()!! > 0)) {
                    timeUpdatePayload = ("{ " + timeUpdatePayload +
                            ", \"currentProgramTime\": " + player?.getCurrentProgramTime() +
                            ", \"currentLiveOffset\": " + player?.getCurrentLiveOffset() +
                            " }")
                } else {
                    timeUpdatePayload = "{ " + timeUpdatePayload + " }"
                }
                sendPlayerEvent(KalturaPlayerEvents.PLAYHEAD_UPDATED, timeUpdatePayload)
                if (reportedDuration != event.duration && event.duration > 0) {
                    reportedDuration = event.duration
                    if ((player != null) && (player?.getMediaEntry() != null) && (player?.getMediaEntry()!!
                            .getMediaType() != PKMediaEntry.MediaEntryType.Vod) /*|| player.isLive()*/) {
                        sendPlayerEvent(
                            KalturaPlayerEvents.LOAD_TIME_RANGES,
                            ("{\"timeRanges\": [ { \"start\": " + 0 +
                                    ", \"end\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                                    " } ] }")
                        )
                    }
                }
            })
        player?.addListener<PlayerEvent.StateChanged>(context, PlayerEvent.stateChanged,
            PKEvent.Listener { event: PlayerEvent.StateChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.STATE_CHANGED,
                    createJSONForEventPayload("newState", event.newState.name)
                )
            })
        player?.addListener<PlayerEvent.TracksAvailable>(context, PlayerEvent.tracksAvailable,
            PKEvent.Listener { event: PlayerEvent.TracksAvailable ->
                sendPlayerEvent(
                    KalturaPlayerEvents.TRACKS_AVAILABLE,
                    gson.toJson(getTracksInfo(event.tracksInfo))
                )
            })
        player?.addListener(context, PlayerEvent.loadedMetadata,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.LOADED_METADATA) })
        player?.addListener(context, PlayerEvent.replay,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.REPLAY) })
        player?.addListener<PlayerEvent.VolumeChanged>(context, PlayerEvent.volumeChanged,
            PKEvent.Listener { event: PlayerEvent.VolumeChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.VOLUME_CHANGED,
                    createJSONForEventPayload("volume", event.volume)
                )
            })
        player?.addListener<PlayerEvent.SurfaceAspectRationResizeModeChanged>(context,
            PlayerEvent.surfaceAspectRationSizeModeChanged,
            PKEvent.Listener { event: PlayerEvent.SurfaceAspectRationResizeModeChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.ASPECT_RATIO_RESIZE_MODE_CHANGED,
                    createJSONForEventPayload(
                        "surfaceAspectRationSizeModeChanged",
                        event.resizeMode.name
                    )
                )
            })
        player?.addListener<PlayerEvent.SubtitlesStyleChanged>(context,
            PlayerEvent.subtitlesStyleChanged,
            PKEvent.Listener { event: PlayerEvent.SubtitlesStyleChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.SUBTITLE_STYLE_CHANGED,
                    createJSONForEventPayload("subtitlesStyleChanged", event.styleName)
                )
            })
        player?.addListener<PlayerEvent.VideoTrackChanged>(context, PlayerEvent.videoTrackChanged,
            PKEvent.Listener { event: PlayerEvent.VideoTrackChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.VIDEO_TRACK_CHANGED,
                    gson.toJson(event.newTrack)
                )
            })
        player?.addListener<PlayerEvent.AudioTrackChanged>(context, PlayerEvent.audioTrackChanged,
            PKEvent.Listener { event: PlayerEvent.AudioTrackChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.AUDIO_TRACK_CHANGED,
                    gson.toJson(event.newTrack)
                )
            })
        player?.addListener<PlayerEvent.TextTrackChanged>(context, PlayerEvent.textTrackChanged,
            PKEvent.Listener { event: PlayerEvent.TextTrackChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.TEXT_TRACK_CHANGED,
                    gson.toJson(event.newTrack)
                )
            })
        player?.addListener<PlayerEvent.ImageTrackChanged>(context, PlayerEvent.imageTrackChanged,
            PKEvent.Listener { event: PlayerEvent.ImageTrackChanged ->
                sendPlayerEvent(
                    KalturaPlayerEvents.IMAGE_TRACK_CHANGED,
                    gson.toJson(event.newTrack)
                )
            })
        player?.addListener<PlayerEvent.PlaybackInfoUpdated>(context,
            PlayerEvent.playbackInfoUpdated,
            PKEvent.Listener { event: PlayerEvent.PlaybackInfoUpdated ->
                sendPlayerEvent(
                    KalturaPlayerEvents.PLAYBACK_INFO_UPDATED,
                    gson.toJson(event.playbackInfo)
                )
            })
        player?.addListener<PlayerEvent.Seeking>(context, PlayerEvent.seeking,
            PKEvent.Listener { event: PlayerEvent.Seeking ->
                sendPlayerEvent(
                    KalturaPlayerEvents.SEEKING,
                    createJSONForEventPayload(
                        "targetPosition",
                        (event.targetPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
                    )
                )
            })
        player?.addListener(context, PlayerEvent.seeked,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.SEEKED) })
        player?.addListener<PlayerEvent.Error>(context, PlayerEvent.error,
            PKEvent.Listener { event: PlayerEvent.Error ->
                if (event.error.isFatal()) {
                    sendPlayerEvent(KalturaPlayerEvents.ERROR, gson.toJson(event.error))
                }
            })
        player?.addListener<PlayerEvent.MetadataAvailable>(context, PlayerEvent.metadataAvailable,
            PKEvent.Listener { event: PlayerEvent.MetadataAvailable ->
                if (!event.metadataList.isEmpty()) {
                    sendPlayerEvent(
                        KalturaPlayerEvents.METADATA_AVAILABLE,
                        gson.toJson(event.metadataList)
                    )
                    //TODO: Add event stream after the player release v4.23.0
                }
            })
        player?.addListener<PlayerEvent.SourceSelected>(context, PlayerEvent.sourceSelected,
            PKEvent.Listener { event: PlayerEvent.SourceSelected ->
                if (event.source != null) {
                    sendPlayerEvent(KalturaPlayerEvents.SOURCE_SELECTED, gson.toJson(event.source))
                }
            })
        player?.addListener<PlayerEvent.PlaybackRateChanged>(context,
            PlayerEvent.playbackRateChanged,
            PKEvent.Listener { event: PlayerEvent.PlaybackRateChanged ->
                if (event.rate > 0) {
                    sendPlayerEvent(
                        KalturaPlayerEvents.PLAYBACK_RATE_CHANGED,
                        createJSONForEventPayload("rate", event.rate)
                    )
                }
            })
        player?.addListener<PlayerEvent.ConnectionAcquired>(context, PlayerEvent.connectionAcquired,
            PKEvent.Listener { event: PlayerEvent.ConnectionAcquired ->
                if (event.uriConnectionAcquiredInfo != null) {
                    sendPlayerEvent(
                        KalturaPlayerEvents.CONNECTION_ACQUIRED,
                        gson.toJson(event.uriConnectionAcquiredInfo)
                    )
                }
            })
        player?.addListener<PlayerEvent.VideoFramesDropped>(context, PlayerEvent.videoFramesDropped,
            PKEvent.Listener { event: PlayerEvent.VideoFramesDropped ->
                sendPlayerEvent(
                    KalturaPlayerEvents.VIDEO_FRAMES_DROPPED,
                    ("{ \"droppedVideoFrames\": " + event.droppedVideoFrames +
                            ", \"droppedVideoFramesPeriod\": " + event.droppedVideoFramesPeriod +
                            ", \"totalDroppedVideoFrames\": " + event.totalDroppedVideoFrames +
                            " }")
                )
            })
        player?.addListener<PlayerEvent.OutputBufferCountUpdate>(context,
            PlayerEvent.outputBufferCountUpdate,
            PKEvent.Listener { event: PlayerEvent.OutputBufferCountUpdate ->
                sendPlayerEvent(
                    KalturaPlayerEvents.OUTPUT_BUFFER_COUNT_UPDATE,
                    ("{ \"skippedOutputBufferCount\": " + event.skippedOutputBufferCount +
                            ", \"renderedOutputBufferCount\": " + event.renderedOutputBufferCount +
                            " }")
                )
            })
        player?.addListener<PlayerEvent.BytesLoaded>(context, PlayerEvent.bytesLoaded,
            PKEvent.Listener { event: PlayerEvent.BytesLoaded ->
                sendPlayerEvent(
                    KalturaPlayerEvents.BYTES_LOADED, ("{ \"bytesLoaded\": " + event.bytesLoaded +
                            ", \"dataType\": " + event.dataType +
                            ", \"loadDuration\": " + event.loadDuration +
                            ", \"totalBytesLoaded\": " + event.totalBytesLoaded +
                            ", \"trackType\": " + event.trackType +
                            " }")
                )
            })
        player?.addListener<AdEvent.AdProgress>(context, AdEvent.adProgress,
            PKEvent.Listener { event: AdEvent.AdProgress ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.AD_PROGRESS,
                    createJSONForEventPayload(
                        "currentAdPosition",
                        (event.currentAdPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
                    )
                )
            })
        player?.addListener<AdEvent.AdLoadedEvent>(context, AdEvent.loaded,
            PKEvent.Listener { event: AdEvent.AdLoadedEvent ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.LOADED,
                    gson.toJson(event.adInfo)
                )
            })
        player?.addListener<AdEvent.AdCuePointsUpdateEvent>(context, AdEvent.cuepointsChanged,
            PKEvent.Listener { event: AdEvent.AdCuePointsUpdateEvent ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.CUEPOINTS_CHANGED,
                    gson.toJson(event.cuePoints)
                )
            })
        player?.addListener<AdEvent.AdStartedEvent>(context, AdEvent.started,
            PKEvent.Listener { event: AdEvent.AdStartedEvent? ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.STARTED
                )
            })
        player?.addListener(context, AdEvent.completed,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.COMPLETED) })
        player?.addListener<AdEvent.AdPausedEvent>(context, AdEvent.paused,
            PKEvent.Listener { event: AdEvent.AdPausedEvent? ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.PAUSED
                )
            })
        player?.addListener<AdEvent.AdResumedEvent>(context, AdEvent.resumed,
            PKEvent.Listener { event: AdEvent.AdResumedEvent? ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.RESUMED
                )
            })
        player?.addListener<AdEvent.AdBufferStart>(context, AdEvent.adBufferStart,
            PKEvent.Listener { event: AdEvent.AdBufferStart ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.AD_BUFFER_START,
                    createJSONForEventPayload("adPosition", event.adPosition)
                )
            })
        player?.addListener<AdEvent.AdBufferEnd>(context, AdEvent.adBufferEnd,
            PKEvent.Listener { event: AdEvent.AdBufferEnd ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.AD_BUFFER_END,
                    createJSONForEventPayload("adPosition", event.adPosition)
                )
            })
        player?.addListener<AdEvent.AdClickedEvent>(context, AdEvent.adClickedEvent,
            PKEvent.Listener { event: AdEvent.AdClickedEvent ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.CLICKED,
                    createJSONForEventPayload("clickThruUrl", event.clickThruUrl)
                )
            })
        player?.addListener<AdEvent.AdSkippedEvent>(context, AdEvent.skipped,
            PKEvent.Listener { event: AdEvent.AdSkippedEvent? ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.SKIPPED
                )
            })
        player?.addListener<AdEvent.AdRequestedEvent>(context, AdEvent.adRequested,
            PKEvent.Listener { event: AdEvent.AdRequestedEvent ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.AD_REQUESTED,
                    createJSONForEventPayload("adTagUrl", event.adTagUrl)
                )
            })
        player?.addListener(context, AdEvent.contentPauseRequested,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.CONTENT_PAUSE_REQUESTED) })
        player?.addListener(context, AdEvent.contentResumeRequested,
            PKEvent.Listener { event: PKEvent? ->
                kalturaPlayerRNView?.reMeasureAndReLayout()
                sendPlayerEvent(KalturaPlayerAdEvents.CONTENT_RESUME_REQUESTED)
            })
        player?.addListener(context, AdEvent.allAdsCompleted,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.ALL_ADS_COMPLETED) })
        player?.addListener<AdEvent.Error>(context, AdEvent.error,
            PKEvent.Listener { event: AdEvent.Error ->
                if (event.error.isFatal()) {
                    sendPlayerEvent(KalturaPlayerAdEvents.ERROR, gson.toJson(event.error))
                }
            })
        player?.addListener(context, AdEvent.adFirstPlay,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_FIRST_PLAY) })
        player?.addListener(context, AdEvent.firstQuartile,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.FIRST_QUARTILE) })
        player?.addListener(context, AdEvent.midpoint,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.MIDPOINT) })
        player?.addListener(context, AdEvent.thirdQuartile,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.THIRD_QUARTILE) })
        player?.addListener(context, AdEvent.skippableStateChanged,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.SKIPPABLE_STATE_CHANGED) })
        player?.addListener(context, AdEvent.tapped,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.TAPPED) })
        player?.addListener(context, AdEvent.iconFallbackImageClosed,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.ICON_FALLBACK_IMAGE_CLOSED) })
        player?.addListener(context, AdEvent.iconTapped,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.ICON_TAPPED) })
        player?.addListener(context, AdEvent.adBreakReady,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_READY) })
        player?.addListener(context, AdEvent.adBreakStarted,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_STARTED) })
        player?.addListener(context, AdEvent.adBreakEnded,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_ENDED) })
        player?.addListener(context, AdEvent.adBreakFetchError,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_FETCH_ERROR) })
        player?.addListener(context, AdEvent.adBreakIgnored,
            PKEvent.Listener { event: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_IGNORED) })
        player?.addListener<AdEvent.AdPlayHeadEvent>(context, AdEvent.playHeadChanged,
            PKEvent.Listener { event: AdEvent.AdPlayHeadEvent ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.PLAY_HEAD_CHANGED,
                    createJSONForEventPayload("adPlayHead", event.adPlayHead)
                )
            })
        player?.addListener<AdEvent.AdPlaybackInfoUpdated>(context, AdEvent.adPlaybackInfoUpdated,
            PKEvent.Listener { event: AdEvent.AdPlaybackInfoUpdated ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.AD_PLAYBACK_INFO_UPDATED,
                    ("{ \"bitrate\": " + event.bitrate +
                            ", \"height\": " + event.height +
                            ", \"width\": " + event.width +
                            " }")
                )
            })
        player?.addListener<AdEvent.DAISourceSelected>(context, AdEvent.daiSourceSelected,
            PKEvent.Listener { event: AdEvent.DAISourceSelected ->
                sendPlayerEvent(
                    KalturaPlayerAdEvents.DAI_SOURCE_SELECTED,
                    createJSONForEventPayload("sourceURL", event.sourceURL)
                )
            })
        player?.addListener<PhoenixAnalyticsEvent.BookmarkErrorEvent>(context, PhoenixAnalyticsEvent.bookmarkError,
            PKEvent.Listener { event: PhoenixAnalyticsEvent.BookmarkErrorEvent ->
                sendPlayerEvent(
                    KalturaPlayerAnalyticsEvents.PHOENIX_BOOKMARK_ERROR,
                    ("{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                            ", \"errorCode\": \"" + event.errorCode + "\" " +
                            ", \"errorType\": \"" + event.type + "\" " +
                            " }")
                )
            })
        player?.addListener<PhoenixAnalyticsEvent.ConcurrencyErrorEvent>(context, PhoenixAnalyticsEvent.concurrencyError,
            PKEvent.Listener { event: PhoenixAnalyticsEvent.ConcurrencyErrorEvent ->
                sendPlayerEvent(
                    KalturaPlayerAnalyticsEvents.PHOENIX_CONCURRENCY_ERROR,
                    ("{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                            ", \"errorCode\": \"" + event.errorCode + "\" " +
                            ", \"errorType\": \"" + event.type + "\" " +
                            " }")
                )
            })
        player?.addListener<PhoenixAnalyticsEvent.ErrorEvent>(context, PhoenixAnalyticsEvent.error,
            PKEvent.Listener { event: PhoenixAnalyticsEvent.ErrorEvent ->
                sendPlayerEvent(
                    KalturaPlayerAnalyticsEvents.PHOENIX_ERROR,
                    ("{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                            ", \"errorCode\": \"" + event.errorCode + "\" " +
                            ", \"errorType\": \"" + event.type + "\" " +
                            " }")
                )
            })
        player?.addListener<BroadpeakEvent.ErrorEvent>(context, BroadpeakEvent.error,
            PKEvent.Listener { event: BroadpeakEvent.ErrorEvent ->
                sendPlayerEvent(
                    "broadpeakError", ("{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                            ", \"errorCode\": \"" + event.errorCode + "\" " +
                            ", \"errorType\": \"" + event.type + "\" " +
                            " }")
                )
            })
    }

    /*****************************************************
     * Device Event Emitter and event helper methods for *
     * React Native to android event communication       *
     */
    private fun emitter(): DeviceEventManagerModule.RCTDeviceEventEmitter {
        return context.getJSModule<DeviceEventManagerModule.RCTDeviceEventEmitter>(
            DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
    }

    /**
     * Create a JSON with only one Key-Value pair
     * @param key JSON object key
     * @param value JSON object value
     * @return JSON object
     */
    private fun createJSONForEventPayload(key: String, value: Any): String {
        return "{ \"$key\": $value }"
    }

    /**
     * Send Event without any payload
     * @param eventName name of the event
     */
    private fun sendPlayerEvent(eventName: String) {
        val params = Arguments.createMap()
        emitter().emit(eventName, params)
    }

    /**
     * Send Event with payload JSON object
     * @param eventName name of the event
     * @param payloadString payload JSON data
     */
    private fun sendPlayerEvent(eventName: String, payloadString: String?) {
        val eventPayloadMap = convertStringToWritableMap(payloadString)
        if (eventPayloadMap == null) {
            log.e("Event payload is null hence returning event is: $eventName")
            return
        }
        emitter().emit(eventName, eventPayloadMap)
    }

    private fun convertStringToWritableMap(payload: String?): WritableMap? {
        //log.v("convertStringToJson");
        if (TextUtils.isEmpty(payload)) {
            log.e("Payload string is invalid $payload")
            return null
        }
        try {
            val jsonObject = JSONObject(payload)
            return convertJsonToMap(jsonObject)
        } catch (e: JSONException) {
            log.e("Payload string can not be converted to JSON object $payload")
        }
        return null
    }

    private fun convertJsonToMap(jsonObject: JSONObject): WritableMap? {
        //log.v("convertJsonToMap");
        val map: WritableMap = WritableNativeMap()
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            var value: Any? = null
            try {
                value = jsonObject[key]
            } catch (e: JSONException) {
                log.e("Exception while parsing Json object : " + key + " Exception is : " + e.message)
            }
            if (value == null) {
                log.e("Exception while parsing Json object value is null, hence returning null.")
                return null
            }
            if (value is JSONObject) {
                map.putMap(key, convertJsonToMap(value))
            } else if (value is JSONArray) {
                try {
                    map.putArray(key, convertJsonToArray(value))
                } catch (e: JSONException) {
                    log.e("Exception while parsing Json Array key: " + key + "value is: " + value + "Exception is : " + e.message)
                }
            } else if (value is Boolean) {
                map.putBoolean(key, (value as Boolean?)!!)
            } else if (value is Int) {
                map.putInt(key, (value as Int?)!!)
            } else if (value is Double) {
                map.putDouble(key, (value as Double?)!!)
            } else if (value is String) {
                map.putString(key, value as String?)
            } else {
                map.putString(key, value.toString())
            }
        }
        return map
    }

    @Throws(JSONException::class)
    private fun convertJsonToArray(jsonArray: JSONArray): WritableArray {
        val array: WritableArray = WritableNativeArray()
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray[i]
            if (value is JSONObject) {
                array.pushMap(convertJsonToMap(value))
            } else if (value is JSONArray) {
                array.pushArray(convertJsonToArray(value))
            } else if (value is Boolean) {
                array.pushBoolean(value)
            } else if (value is Int) {
                array.pushInt(value)
            } else if (value is Double) {
                array.pushDouble(value)
            } else if (value is String) {
                array.pushString(value)
            } else {
                array.pushString(value.toString())
            }
        }
        return array
    }
}
