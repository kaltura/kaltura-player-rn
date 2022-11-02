package com.reactnativekalturaplayer

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.kaltura.netkit.utils.ErrorElement
import com.kaltura.netkit.utils.NKLog
import com.kaltura.playkit.*
import com.kaltura.playkit.ads.AdController
import com.kaltura.playkit.ads.PKAdErrorType
import com.kaltura.playkit.player.*
import com.kaltura.playkit.player.thumbnail.ThumbnailInfo
import com.kaltura.playkit.plugins.ads.AdCuePoints
import com.kaltura.playkit.plugins.ads.AdEvent
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent
import com.kaltura.playkit.utils.Consts
import com.kaltura.tvplayer.*
import com.reactnativekalturaplayer.events.KalturaPlayerAdEvents
import com.reactnativekalturaplayer.events.KalturaPlayerAnalyticsEvents
import com.reactnativekalturaplayer.events.KalturaPlayerEvents
import com.reactnativekalturaplayer.model.*
import com.reactnativekalturaplayer.model.tracks.TracksInfo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class KalturaPlayerRN(
    private val context: ReactApplicationContext,
    private val kalturaPlayerRNView: KalturaPlayerRNView): LifecycleEventListener {

    private val log = PKLog.get(KalturaPlayerRN::class.java.simpleName)

    private var gson = Gson()
    private var mainHandler: Handler? = null

    private var player: KalturaPlayer? = null
    private var playerType: KalturaPlayer.Type? = null
    private var reportedDuration = Consts.TIME_UNSET
    private var playerViewAdded = false

    private val jsonKeyAndroid: String = "android"
    private val youboraAccountCode = "accountCode"

    fun createPlayerInstance(playerType: KalturaPlayer.Type, partnerId: Int, initOptions: String?, promise: Promise) {
        this.playerType = playerType

        log.d("createPlayerInstance PartnerId: $partnerId initOptions : $initOptions playerType: ${this.getPlayerType()}")

        if (partnerId > 0 && !TextUtils.isEmpty(initOptions) || getPlayerType() == KalturaPlayer.Type.basic) {
            if (getPlayerType() == KalturaPlayer.Type.basic) {
                createKalturaBasicPlayer(initOptions, promise)
            } else if (!TextUtils.isEmpty(initOptions) &&
                (getPlayerType() == KalturaPlayer.Type.ott || getPlayerType() == KalturaPlayer.Type.ovp)) {
                createKalturaOttOvpPlayer(partnerId, initOptions, promise)
            } else {
                val message = "Player can not be created. playerType is ${getPlayerType()} and partnerId is $partnerId"
                log.e(message)
                sendCallbackToJS(promise, message, true)
            }
        } else {
            val message = "PartnerId: $partnerId is not valid"
            log.e(message)
            sendCallbackToJS(promise, message, true)
        }
    }

    private fun createKalturaBasicPlayer(initOptions: String?, promise: Promise) {
        log.d("Creating Basic Player instance.")
        val initOptionsModel = getParsedJson(initOptions, InitOptions::class.java)
        val playerInitOptions = PlayerInitOptions()
        if (initOptionsModel == null) {
            playerInitOptions.setAutoPlay(true)
            playerInitOptions.setPKRequestConfig(PKRequestConfig(true))
        } else {
            setCommonPlayerInitOptions(playerInitOptions, initOptionsModel)
            val pkPluginConfigs = createOrUpdatePluginConfigs(initOptionsModel.plugins, true)
            playerInitOptions.setPluginConfigs(pkPluginConfigs)
        }

        createUiHandler()
        addLifeCycleEventListener(context)

        runOnUiThread {
            if (player == null) {
                player = KalturaBasicPlayer.create(context, playerInitOptions)
            }

            // This will let the apps know that Player has been created now
            // app can add the listeners and load the media
            sendCallbackToJS(promise, true)
            initDrm(context)
            addPlayerViewToRNView(player)
        }
    }

    private fun addPlayerViewToRNView(kalturaPlayer: KalturaPlayer?) {
        kalturaPlayer?.let {
            if (!playerViewAdded) {
                it.setPlayerView(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                kalturaPlayerRNView.addView(it.playerView)
                playerViewAdded = true
            }
        }
    }

    @NonNull
    private fun createMediaEntry(url: String?, basicMediaAsset: BasicMediaAsset): PKMediaEntry {
        log.d("createMediaEntry URL is: $url")

        //Create media entry.
        val mediaEntry = PKMediaEntry()
        //Set id for the entry.
        mediaEntry.id = basicMediaAsset.id
        mediaEntry.name = basicMediaAsset.name
        mediaEntry.duration = basicMediaAsset.duration
        mediaEntry.mediaType = basicMediaAsset.mediaEntryType
        mediaEntry.setIsVRMediaType(basicMediaAsset.isVRMediaType)
        if (basicMediaAsset.externalSubtitleList != null && basicMediaAsset.externalSubtitleList.isNotEmpty()) {
            mediaEntry.externalSubtitleList = basicMediaAsset.externalSubtitleList
        }
        if (basicMediaAsset.metadata != null && basicMediaAsset.metadata.isNotEmpty()) {
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
        url: String?,
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
        if (mediaSource != null && pkDrmParamsList != null && pkDrmParamsList.isNotEmpty()) {
            mediaSource.drmData = pkDrmParamsList
        }
    }

    private fun createKalturaOttOvpPlayer(partnerId: Int, playerInitOptionsJson: String?, promise: Promise) {
        log.d("createKalturaOttOvpPlayer:$partnerId, \n initOptions: \n $playerInitOptionsJson")
        val initOptionsModel = getParsedJson(
            playerInitOptionsJson,
            InitOptions::class.java
        )
        if (initOptionsModel == null || TextUtils.isEmpty(initOptionsModel.serverUrl) || getPlayerType() == KalturaPlayer.Type.basic) {
            val message = "Failed to create Player initOptionsModel : $initOptionsModel \n" +
                    "ServerURL: ${initOptionsModel?.serverUrl}"
            log.e(message)
            sendCallbackToJS(promise, message, true)
            return
        }

        // load the player and put it in the main frame
        if (getPlayerType() == KalturaPlayer.Type.ott) {
            KalturaOttPlayer.initialize(context, partnerId, initOptionsModel.serverUrl)
        } else {
            KalturaOvpPlayer.initialize(context, partnerId, initOptionsModel.serverUrl)
        }
        if (initOptionsModel.warmupUrls != null && initOptionsModel.warmupUrls.isNotEmpty()) {
            PKHttpClientManager.setHttpProvider("okhttp")
            PKHttpClientManager.warmUp(*initOptionsModel.warmupUrls.toTypedArray())
        }
        val playerInitOptions = PlayerInitOptions(partnerId)
        playerInitOptions.setKs(initOptionsModel.ks)
        playerInitOptions.setMediaEntryCacheConfig(initOptionsModel.mediaEntryCacheConfig)

        setCommonPlayerInitOptions(playerInitOptions, initOptionsModel)

        val pkPluginConfigs = createOrUpdatePluginConfigs(initOptionsModel.plugins, true)
        playerInitOptions.setPluginConfigs(pkPluginConfigs)

        createUiHandler()
        addLifeCycleEventListener(context)

        runOnUiThread {
            if (player == null && getPlayerType() == KalturaPlayer.Type.ott) {
                player = KalturaOttPlayer.create(context, playerInitOptions)
            }
            if (player == null && getPlayerType() == KalturaPlayer.Type.ovp) {
                player = KalturaOvpPlayer.create(context, playerInitOptions)
            }
            // This will let the apps know that Player has been created now
            // app can add the listeners and load the media
            sendCallbackToJS(promise, true)
            initDrm(context)
            addPlayerViewToRNView(player)
        }
    }

    fun addPlayerView() {
        if (playerViewAdded) {
            log.d("Player view is already added")
            return
        } else {
            player?.let {
                runOnUiThread {
                    log.d("addPlayerView")
                    addPlayerViewToRNView(it)
                }
            }
        }
    }

    fun removePlayerView() {
        runOnUiThread {
            player?.let {
                if (playerViewAdded) {
                    log.d("removePlayerView")
                    it.playerView?.parent?.let { parentView ->
                        (parentView as ViewGroup).removeAllViews() // Remove all view in order to remove views attached to the player
                        playerViewAdded = false
                    }
                }
            }
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
            kalturaPlayerRNView.reMeasureAndReLayout()
            player?.onApplicationResumed()
        }
    }

    fun updatePluginConfigs(pluginConfigJson: String?) {
        log.e("updatePluginConfigs")
        if (TextUtils.isEmpty(pluginConfigJson)) {
            log.e("pluginConfigJson is empty hence returning from here.")
            return
        }

        val pluginConfigs = getParsedJson(pluginConfigJson, RegisteredPlugins::class.java)
        createOrUpdatePluginConfigs(pluginConfigs, false)
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

    //NOOP
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
            subtitleStyling?.let {
                val style = getParsedSubtitleStyleSettings(it)
                style?.let {
                    runOnUiThread {
                        player?.updateSubtitleStyle(style)
                    }
                }
            }
        }
    }

    fun updateResizeMode(resizeMode: String) {
        log.d("updateResizeMode")
        if (!TextUtils.isEmpty(resizeMode)) {
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
            settings?.let {
                runOnUiThread {
                    player?.updateABRSettings(it)
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

    fun updateLLConfig(pkLowLatencyConfig: String?) {
        log.d("updateLLConfig")
        if (!TextUtils.isEmpty(pkLowLatencyConfig)) {
            val config = getParsedJson(
                pkLowLatencyConfig,
                PKLowLatencyConfig::class.java
            )
            config?.let {
                runOnUiThread {
                    player?.updatePKLowLatencyConfig(it)
                }
            }
        }
    }

    fun resetLLConfig() {
        log.d("resetLLConfig")
        runOnUiThread {
            player?.updatePKLowLatencyConfig(PKLowLatencyConfig.UNSET)
        }
    }

    /**
     * Send the current position of the player
     * for both Content or Ad playback
     *
     * If position is invalid then it send Unset position which is -1
     */
    fun getCurrentPosition(promise: Promise) {
        log.d("getCurrentPosition")
        player?.let {
            runOnUiThread {
                val adController = it.getController(AdController::class.java)
                if (adController != null && adController.isAdDisplayed) {
                    sendCallbackToJS(promise, adController.adCurrentPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
                } else {
                    sendCallbackToJS(promise, it.currentPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
                }
            }
        }
    }

    fun isPlaying(promise: Promise) {
        log.d("isPlaying")
        player?.let {
            runOnUiThread {
                sendCallbackToJS(promise, it.isPlaying)
            }
        }
    }

    fun isLive(promise: Promise) {
        log.d("isLive")
        player?.let {
            runOnUiThread {
                sendCallbackToJS(promise, it.isLive)
            }
        }
    }

    /**
     * Get the Information for a thumbnail image by position
     * if positionMS is not passed current position will be used
     *
     * @param positionMs - relevant image for given player position. (optional)
     */
    fun requestThumbnailInfo(positionMs: Float, promise: Promise) {
        log.d("requestThumbnailInfo position is: $positionMs")
        player?.let {
            runOnUiThread {
                val thumbnailInfo: ThumbnailInfo? = player?.getThumbnailInfo(positionMs.toLong())
                if (thumbnailInfo != null && positionMs >= 0) {
                    val thumbnailInfoJson =
                        "{ \"position\": $positionMs, \"thumbnailInfo\": " + gson.toJson(
                            thumbnailInfo
                        ) + " }"
                    sendCallbackToJS(promise, thumbnailInfoJson)
                } else {
                    val message = "requestThumbnailInfo: thumbnailInfo is null or position = $positionMs is invalid"
                    sendCallbackToJS(promise, message, true)
                }
            }
        }
    }

    fun setLogLevel(logLevel: String) {
        var level = logLevel
        log.d("setLogLevel: $level")
        if (TextUtils.isEmpty(level)) {
            return
        }
        level = level.uppercase(Locale.getDefault())
        when (level) {
            "VERBOSE" -> {
                PKLog.setGlobalLevel(PKLog.Level.verbose)
                NKLog.setGlobalLevel(NKLog.Level.verbose)
            }
            "DEBUG" -> {
                PKLog.setGlobalLevel(PKLog.Level.debug)
                NKLog.setGlobalLevel(NKLog.Level.debug)
            }
            "WARN" -> {
                PKLog.setGlobalLevel(PKLog.Level.warn)
                NKLog.setGlobalLevel(NKLog.Level.warn)
            }
            "INFO" -> {
                PKLog.setGlobalLevel(PKLog.Level.info)
                NKLog.setGlobalLevel(NKLog.Level.info)
            }
            "ERROR" -> {
                PKLog.setGlobalLevel(PKLog.Level.error)
                NKLog.setGlobalLevel(NKLog.Level.error)
            }
            "OFF" -> {
                PKLog.setGlobalLevel(PKLog.Level.off)
                NKLog.setGlobalLevel(NKLog.Level.off)
            }
            else -> {
                PKLog.setGlobalLevel(PKLog.Level.off)
                NKLog.setGlobalLevel(NKLog.Level.off)
            }
        }
    }

    /**
     * Load the Basic, OVP, OTT media to the player.
     * In case of error, it sends the Error JSON in promise
     * otherwise sends back the PKMediaEntry JSON in promise
     */
    fun load(assetId: String?, mediaAssetJson: String?, promise: Promise) {
        log.d(
            "load assetId: " + assetId +
                    "\n player type: " + getPlayerType() +
                    "\n , mediaAssetJson:" + mediaAssetJson
        )
        if (player == null) {
            val message = "Player instance is null while loading the media. Hence returning."
            log.e(message)
            sendCallbackToJS(promise, message, true)
            return
        }

        if (TextUtils.isEmpty(assetId)) {
            val message = "assetId $assetId is invalid"
            log.e(message)
            sendCallbackToJS(promise, message, true)
            return
        }

        if (getPlayerType() == KalturaPlayer.Type.basic || isBasicPlaybackRequired(assetId)) {
            var basicMediaAsset = getParsedJson(
                mediaAssetJson,
                BasicMediaAsset::class.java
            )

            if (basicMediaAsset == null) {
                basicMediaAsset = BasicMediaAsset()
            }

            val mediaEntry = createMediaEntry(assetId, basicMediaAsset)
            runOnUiThread {
                player?.setMedia(mediaEntry, basicMediaAsset.startPosition)
                sendCallbackToJS(promise, gson.toJson(mediaEntry))
            }
        } else if (getPlayerType() == KalturaPlayer.Type.ott || getPlayerType() == KalturaPlayer.Type.ovp) {
            var mediaAsset = getParsedJson(mediaAssetJson, MediaAsset::class.java)
            if (mediaAsset == null) {
                mediaAsset = MediaAsset()
            }

            if (getPlayerType() == KalturaPlayer.Type.ott) {
                runOnUiThread {
                    val ottMediaOptions = mediaAsset.buildOttMediaOptions(assetId, player?.ks)
                    player?.loadMedia(ottMediaOptions) { _: MediaOptions?, entry: PKMediaEntry?, error: ErrorElement? ->
                        if (error != null) {
                            log.e("ott media load error: " + error.name + " " + error.code + " " + error.message)
                            sendCallbackToJS(promise, gson.toJson(error), true)
                        } else {
                            log.d("ott media load success name = " + entry?.name + " initialVolume = " + mediaAsset.initialVolume)
                            sendCallbackToJS(promise, gson.toJson(entry))

                            if (mediaAsset.initialVolume >= 0 && mediaAsset.initialVolume < 1.0) {
                                player?.setVolume(mediaAsset.initialVolume)
                            }
                            player?.startPosition = mediaAsset.startPosition
                        }
                    }
                }
            } else {
                runOnUiThread {
                    val ovpMediaOptions = mediaAsset.buildOvpMediaOptions(assetId, "", player?.ks)

                    player?.loadMedia(ovpMediaOptions) { _: MediaOptions?, entry: PKMediaEntry?, error: ErrorElement? ->
                        if (error != null) {
                            log.e("ovp media load error: " + error.name + " " + error.code + " " + error.message)
                            sendCallbackToJS(promise, gson.toJson(error), true)
                        } else {
                            log.d("ovp media load success name = " + entry?.name + " initialVolume = " + mediaAsset.initialVolume)
                            sendCallbackToJS(promise, gson.toJson(gson.toJson(entry)))
                            if (mediaAsset.initialVolume >= 0 && mediaAsset.initialVolume < 1.0) {
                                player?.setVolume(mediaAsset.initialVolume)
                            }
                            player?.startPosition = mediaAsset.startPosition
                        }
                    }
                }
            }
        } else {
            val message = "No Player type defined hence can not load the media. PlayerType ${getPlayerType()}"
            log.e(message)
            sendCallbackToJS(promise, message, true)
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
        subtitleStyleSettings?.let {
            playerInitOptions.setSubtitleStyle(subtitleStyleSettings)
        }
        if (initOptionsModel.wakeMode != null && !TextUtils.isEmpty(initOptionsModel.wakeMode.toString())) {
            try {
                playerInitOptions.setWakeMode(PKWakeMode.valueOf(initOptionsModel.wakeMode.toString()))
            } catch (exception: IllegalArgumentException) {
                log.e("Invalid wake mode passed which is ${initOptionsModel.wakeMode}")
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
                log.e("Invalid subtitlePreference passed which is ${initOptionsModel.subtitlePreference}")
            }
        }
        initOptionsModel.videoCodecSettings?.let {
            val videoCodecSettings = VideoCodecSettings()
            videoCodecSettings.allowMixedCodecAdaptiveness = it.allowMixedCodecAdaptiveness
            videoCodecSettings.isAllowSoftwareDecoder = it.isAllowSoftwareDecoder
            if (it.codecPriorityList != null && it.codecPriorityList.isNotEmpty()) {
                videoCodecSettings.codecPriorityList = it.codecPriorityList
            }
            playerInitOptions.setVideoCodecSettings(videoCodecSettings)
        }

        initOptionsModel.audioCodecSettings?.let {
            val audioCodecSettings = AudioCodecSettings()
            audioCodecSettings.allowMixedCodecs = it.allowMixedCodecs
            audioCodecSettings.allowMixedBitrates = it.allowMixedBitrates
            if (it.codecPriorityList != null && it.codecPriorityList.isNotEmpty()
            ) {
                audioCodecSettings.codecPriorityList = it.codecPriorityList
            }
            playerInitOptions.setAudioCodecSettings(audioCodecSettings)
        }
        initOptionsModel.loadControlBuffers?.let {
            playerInitOptions.setLoadControlBuffers(it)
        }
        initOptionsModel.vrSettings?.let {
            playerInitOptions.setVRSettings(it)
        }
        initOptionsModel.contentRequestAdapter?.let {
            playerInitOptions.setContentRequestAdapter(it)
        }
        initOptionsModel.licenseRequestAdapter?.let {
            playerInitOptions.setLicenseRequestAdapter(it)
        }
        initOptionsModel.drmSettings?.let {
            playerInitOptions.setDrmSettings(it)
        }
    }

    /**
     * Create `PKPluginConfigs` object for `PlayerInitOptions`
     * Update the plugin config
     *
     * @param initOptions class which contains all the configuration for PlayerInitOptions
     * @param isPluginRegistration if `true` then register(set) plugin or update plugin config
     *
     * @return `PKPluginConfig` object
     */
    @NonNull
    private fun createOrUpdatePluginConfigs(plugins: RegisteredPlugins?, isPluginRegistration: Boolean): PKPluginConfigs {
        val pkPluginConfigs = PKPluginConfigs()
        plugins?.let { registeredPlugins ->
            registeredPlugins.ima?.let {
                createOrUpdatePlugin(PlayerPluginClass.ima, pkPluginConfigs, it, isPluginRegistration)
            }
            registeredPlugins.imadai?.let {
                createOrUpdatePlugin(PlayerPluginClass.imadai, pkPluginConfigs, it, isPluginRegistration)
            }
            registeredPlugins.youbora?.let {
                // This key is only for Youbora Android, in iOS they have it inside `AnalyticsConfig` which is Youbora Config
                val youboraSpecialKey = "params"
                if (it.has(youboraSpecialKey) && it.get(youboraSpecialKey) != null) {
                    val youboraJson: JsonObject = it.getAsJsonObject(youboraSpecialKey)
                    if (youboraJson.has(youboraAccountCode) && youboraJson[youboraAccountCode] != null) {
                        createOrUpdatePlugin(
                            PlayerPluginClass.youbora,
                            pkPluginConfigs,
                            it,
                            isPluginRegistration
                        )
                    }
                }
            }
            registeredPlugins.kava?.let {
                createOrUpdatePlugin(PlayerPluginClass.kava, pkPluginConfigs, it, isPluginRegistration)
            }
            registeredPlugins.ottAnalytics?.let {
                createOrUpdatePlugin(
                    PlayerPluginClass.ottAnalytics,
                    pkPluginConfigs,
                    it,
                    isPluginRegistration
                )
            }
            registeredPlugins.broadpeak?.let {
                createOrUpdatePlugin(
                    PlayerPluginClass.broadpeak,
                    pkPluginConfigs,
                    it,
                    isPluginRegistration
                )
            }
        }
        return pkPluginConfigs
    }

    private fun runOnUiThread(runnable: Runnable) {
        mainHandler?.post(runnable)
    }

    @Throws(IllegalArgumentException::class)
    private fun getPlayerType(): KalturaPlayer.Type {
        if (playerType == null) {
            throw IllegalArgumentException("Invalid Player type $playerType. \n" +
                    "PlayerType should be passed in the Props from React Native app.")
        }
        return playerType as KalturaPlayer.Type
    }

    /**
     * This method checks if the Player type is OVP/OTT
     * but still app wants to play a media using URL or DRM license URL
     * instead of using our backend.
     * @param assetURL Playback URL for basic player
     *
     * @return `true` if basic playback required
     */
    private fun isBasicPlaybackRequired(assetURL: String?): Boolean {
        if (!TextUtils.isEmpty(assetURL) && playerType != null && playerType != KalturaPlayer.Type.basic) {
            val isBasicPlayback = URLUtil.isValidUrl(assetURL) && PKMediaFormat.valueOfUrl(assetURL) != null
            log.d("isBasicPlaybackRequired = $isBasicPlayback")
            return isBasicPlayback
        }
        return false
    }

    private fun initDrm(context: ReactApplicationContext) {
        context.runOnNativeModulesQueueThread {
            MediaSupport.initializeDrm(context) { pkDeviceSupportInfo: PKDeviceCapabilitiesInfo, provisionError: Exception? ->
                if (pkDeviceSupportInfo.isProvisionPerformed) {
                    if (provisionError != null) {
                        log.e("DRM Provisioning failed $provisionError")
                    } else {
                        log.d("DRM Provisioning succeeded")
                    }
                }
                log.d("DRM initialized; supported: ${pkDeviceSupportInfo.supportedDrmSchemes} isHardwareDrmSupported = ${pkDeviceSupportInfo.isHardwareDrmSupported}")
                val gson = Gson()
                sendPlayerEvent(
                    KalturaPlayerEvents.DRM_INITIALIZED,
                    gson.toJson(pkDeviceSupportInfo)
                )
            }
        }
    }

    @NonNull
    private fun getTracksInfo(pkTracksInfo: PKTracks): TracksInfo {
        val videoTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.VideoTrack> =
            ArrayList()
        val audioTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.AudioTrack> =
            ArrayList()
        val textTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.TextTrack> =
            ArrayList()
        val imageTracksInfo: MutableList<com.reactnativekalturaplayer.model.tracks.ImageTrack> =
            ArrayList()
        for ((videoTrackIndex, videoTrack: VideoTrack) in pkTracksInfo.videoTracks.withIndex()) {
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
        }
        for ((audioTrackIndex, audioTrack: AudioTrack) in pkTracksInfo.audioTracks.withIndex()) {
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
        }
        for ((textTrackIndex, textTrack: TextTrack) in pkTracksInfo.textTracks.withIndex()) {
            textTracksInfo.add(
                com.reactnativekalturaplayer.model.tracks.TextTrack(
                    textTrack.uniqueId,
                    textTrack.language,
                    textTrack.label,
                    pkTracksInfo.defaultTextTrackIndex == textTrackIndex
                )
            )
        }
        for ((imageTrackIndex, imageTrack: ImageTrack) in pkTracksInfo.imageTracks.withIndex()) {
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
        }

        val tracksInfo = TracksInfo()
        tracksInfo.setVideoTracks(videoTracksInfo)
        tracksInfo.setAudioTracks(audioTracksInfo)
        tracksInfo.setTextTracks(textTracksInfo)
        tracksInfo.setImageTracks(imageTracksInfo)
        return tracksInfo
    }

    /**
     * Prepare JSON for the Error
     */
    private fun getErrorJson(error: PKError): String? {
        val errorJson = JsonObject()

        val errorCause = if ((error.exception != null)) error.exception?.cause.toString() + "" else ""
        errorJson.addProperty("errorType", error.errorType.name)

        when (error.errorType) {
            is PKPlayerErrorType -> {
                errorJson.addProperty(
                    "errorCode",
                    (error.errorType as PKPlayerErrorType).errorCode.toString()
                )
            }
            is PKAdErrorType -> {
                errorJson.addProperty(
                    "errorCode",
                    (error.errorType as PKAdErrorType).errorCode.toString()
                )
            }
            else -> {
                errorJson.addProperty("errorCode", PKPlayerErrorType.UNEXPECTED.errorCode.toString())
            }
        }
        errorJson.addProperty("errorSeverity", error.severity.name)
        errorJson.addProperty("errorMessage", error.message)
        errorJson.addProperty("errorCause", errorCause)
        return gson.toJson(errorJson)
    }

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
                "\"count\": " + adCuePointsArray.size +
                "," +
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
     * Method to register(set) or update the plugin config for the first time.
     *
     * @param pluginName `PlayerPlugins` enum
     * @param pluginConfigs `PKPluginConfigs` object for the `PlayerInitOptions`
     * @param pluginConfigJson plugin configuration json
     * @param isPluginRegistration `true` to register the plugin else update plugin config
     */
    private fun createOrUpdatePlugin(
        pluginName: PlayerPluginClass,
        pluginConfigs: PKPluginConfigs?,
        pluginConfigJson: JsonObject?,
        isPluginRegistration: Boolean
    ) {

        val pluginFactoryClass = getPluginFactory(pluginName)
        val pluginConfigClass = getPluginConfig(pluginName)

        if (pluginFactoryClass == null || pluginConfigClass == null) {
            log.e("Invalid Plugin factory $pluginFactoryClass or plugin config $pluginConfigClass")
            return;
        }

        if (pluginConfigJson == null || pluginConfigJson.size() == 0) {
            log.w("Plugins' config Json is not valid hence returning $pluginConfigClass")
            return
        }

        val strPluginJson = pluginConfigJson.toString()

        if (isPluginRegistration) {
            PlayKitManager.registerPlugins(context, pluginFactoryClass)
        }

        if (pluginConfigs != null && !TextUtils.isEmpty(strPluginJson)) {
            val parsedPluginConfig = getParsedJson(strPluginJson, pluginConfigClass)
            if (parsedPluginConfig != null) {
                if (isPluginRegistration) {
                    pluginConfigs.setPluginConfig(pluginFactoryClass.name, parsedPluginConfig)
                } else {
                    player?.updatePluginConfig(pluginFactoryClass.name, parsedPluginConfig)
                }
            } else {
                log.e("Invalid configuration for " + pluginConfigClass.simpleName)
            }
        } else {
            log.e(
                ("Can not create or update the plugin ${pluginConfigClass.simpleName} \n " +
                        "pluginConfig is: $pluginConfigs \n " +
                        "imaConfig json is: $pluginConfigJson \n " +
                        "isPluginRegistration $isPluginRegistration")
            )
        }
    }

    /**
     * Parse the [SubtitleStyling] object to
     * Player's [SubtitleStyleSettings] object
     *
     * @param subtitleStyling styling object from app
     * @return SubtitleStyleSettings object
     */
    @Nullable
    private fun getParsedSubtitleStyleSettings(subtitleStyling: SubtitleStyling?): SubtitleStyleSettings? {
        var subtitleStyleSettings: SubtitleStyleSettings? = null
        subtitleStyling?.let { sbtStyling ->

            var textColor: Int? = sbtStyling.getStringToColor(sbtStyling.subtitleTextColor)
            var backgroundColor: Int? = sbtStyling.getStringToColor(sbtStyling.subtitleBackgroundColor)

            sbtStyling.subtitleBackgroundColor?.let {
                if (TextUtils.isEmpty(sbtStyling.subtitleTextColor)){
                    sbtStyling.getStringToColor(it)?.let { bgColor ->
                        textColor = if (sbtStyling.isDarkColor(bgColor)) {
                            Color.WHITE
                        } else {
                            Color.BLACK
                        }
                    }
                }
            }

            sbtStyling.subtitleTextColor?.let {
                if (TextUtils.isEmpty(sbtStyling.subtitleBackgroundColor)){
                    sbtStyling.getStringToColor(it)?.let { txtColor ->
                        backgroundColor = if (sbtStyling.isDarkColor(txtColor)) {
                            Color.WHITE
                        } else {
                            Color.BLACK
                        }
                    }
                }
            }

            subtitleStyleSettings = SubtitleStyleSettings(sbtStyling.subtitleStyleName)
                .setBackgroundColor(backgroundColor ?: Color.BLACK)
                .setTextColor(textColor ?: Color.WHITE)
                .setTextSizeFraction(sbtStyling.getSubtitleTextSizeFraction())
                .setTypeface(sbtStyling.getSubtitleStyleTypeface())
                .setEdgeType(sbtStyling.getSubtitleEdgeType())

            sbtStyling.subtitleWindowColor?.let {
                sbtStyling.getStringToColor(it)?.let { windowColor ->
                    subtitleStyleSettings?.setWindowColor(windowColor)
                }
            }

            sbtStyling.subtitleEdgeColor?.let {
                sbtStyling.getStringToColor(it)?.let { edgeColor ->
                    subtitleStyleSettings?.setEdgeColor(edgeColor)
                }
            }

            val pkSubtitlePosition = PKSubtitlePosition(sbtStyling.overrideInlineCueConfig)
            if (sbtStyling.horizontalPositionPercentage == null) {
                sbtStyling.verticalPositionPercentage?.let {
                    pkSubtitlePosition.setVerticalPosition(it)
                }
            } else {
                sbtStyling.verticalPositionPercentage?.let { verticalPercentage ->
                    sbtStyling.horizontalPositionPercentage?.let { horizontalPercentage ->
                        pkSubtitlePosition.setPosition(
                            horizontalPercentage,
                            verticalPercentage,
                            sbtStyling.getHorizontalAlignment()
                        )
                    }
                }
            }

            subtitleStyleSettings?.setSubtitlePosition(pkSubtitlePosition)
        }

        return subtitleStyleSettings
    }

    @Nullable
    private fun <T> getParsedJson(parsableJson: String?, parsingClass: Class<T>?): T? {
        if (TextUtils.isEmpty(parsableJson)) {
            log.d("getParsedJson parsable Json is empty.")
            return null
        }

        if (parsingClass == null) {
            log.d("parsingClass is null.")
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

    /**
     * Send the callback to react native apps
     * args does not support Long
     */
    private fun sendCallbackToJS(promise: Promise, args: Any, isError: Boolean = false, throwable: Throwable? = null) {
        log.d("sendCallbackToJS $args")
        if (isError) {
            promise.reject(throwable ?: Throwable(args.toString()))
        } else {
            promise.resolve(args)
        }
    }

    private fun createUiHandler() {
        if (mainHandler == null) {
            mainHandler = Handler(Looper.getMainLooper())
        }
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
        // This should also be controlled by the RN FE apps
    }

    fun removeKalturaPlayerListeners() {
        runOnUiThread {
            player?.removeListeners(this)
            log.d("Player listeners are removed.")
        }
    }

    /**
     * Listen to the KalturaPlayer events
     * and simultaneously create an event for RN JS side
     */
    fun addKalturaPlayerListeners() {
        log.d("addKalturaPlayerListeners")

        if (player == null) {
            log.d("Player is null. Not able to add the Kaltura Player Listeners hence returning.")
            return
        }

        player?.addListener(context, PlayerEvent.canPlay) { sendPlayerEvent(KalturaPlayerEvents.CAN_PLAY) }

        player?.addListener(context, PlayerEvent.playing) { sendPlayerEvent(KalturaPlayerEvents.PLAYING) }

        player?.addListener(context, PlayerEvent.play) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.PLAY) }

        player?.addListener(context, PlayerEvent.pause) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.PAUSE) }

        player?.addListener(context, PlayerEvent.ended) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.ENDED) }

        player?.addListener(context, PlayerEvent.stopped) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.STOPPED) }

        player?.addListener(context, PlayerEvent.durationChanged) { event: PlayerEvent.DurationChanged ->
            reportedDuration = event.duration
            val durationJson: String = createJSONForEventPayload(
                "duration",
                (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
            )
            sendPlayerEvent(KalturaPlayerEvents.DURATION_CHANGE, durationJson)
        }

        player?.addListener(context, PlayerEvent.playheadUpdated) { event: PlayerEvent.PlayheadUpdated ->
            var timeUpdatePayload: String =
                ("\"position\": " + (event.position / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                        ", \"bufferPosition\": " + (event.bufferPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT))

            timeUpdatePayload = if ((player != null) && player?.isLive!! && (player?.currentProgramTime!! > 0)) {
                ("{ " + timeUpdatePayload +
                        ", \"currentProgramTime\": " + player?.currentProgramTime +
                        ", \"currentLiveOffset\": " + player?.currentLiveOffset +
                        " }")
            } else {
                "{ $timeUpdatePayload }"
            }

            sendPlayerEvent(KalturaPlayerEvents.PLAYHEAD_UPDATED, timeUpdatePayload)

            if (reportedDuration != event.duration && event.duration > 0) {
                reportedDuration = event.duration
                if ((player != null) && (player?.mediaEntry != null) && (player?.mediaEntry!!
                        .mediaType != PKMediaEntry.MediaEntryType.Vod) /*|| player.isLive()*/) {
                    sendPlayerEvent(
                        KalturaPlayerEvents.LOAD_TIME_RANGES,
                        ("{\"timeRanges\": [ { \"start\": " + 0 +
                                ", \"end\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                                " } ] }")
                    )
                }
            }
        }

        player?.addListener(context, PlayerEvent.stateChanged) { event: PlayerEvent.StateChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.STATE_CHANGED,
                createJSONForEventPayload("newState", event.newState.name)
            )
        }

        player?.addListener(context, PlayerEvent.tracksAvailable) { event: PlayerEvent.TracksAvailable ->
            sendPlayerEvent(
                KalturaPlayerEvents.TRACKS_AVAILABLE,
                gson.toJson(getTracksInfo(event.tracksInfo))
            )
        }
        player?.addListener(context, PlayerEvent.loadedMetadata) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.LOADED_METADATA) }

        player?.addListener(context, PlayerEvent.replay) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.REPLAY) }

        player?.addListener(context, PlayerEvent.volumeChanged) { event: PlayerEvent.VolumeChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.VOLUME_CHANGED,
                createJSONForEventPayload("volume", event.volume)
            )
        }

        player?.addListener(context,
            PlayerEvent.surfaceAspectRationSizeModeChanged) { event: PlayerEvent.SurfaceAspectRationResizeModeChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.ASPECT_RATIO_RESIZE_MODE_CHANGED,
                createJSONForEventPayload(
                    "surfaceAspectRationSizeModeChanged",
                    event.resizeMode.name
                )
            )
        }

        player?.addListener(context,
            PlayerEvent.subtitlesStyleChanged) { event: PlayerEvent.SubtitlesStyleChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.SUBTITLE_STYLE_CHANGED,
                createJSONForEventPayload("subtitlesStyleChanged", event.styleName)
            )
        }

        player?.addListener(context, PlayerEvent.videoTrackChanged) { event: PlayerEvent.VideoTrackChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.VIDEO_TRACK_CHANGED,
                createJSONForEventPayload(jsonKeyAndroid, gson.toJson(event.newTrack))
            )
        }

        player?.addListener(context, PlayerEvent.audioTrackChanged) { event: PlayerEvent.AudioTrackChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.AUDIO_TRACK_CHANGED,
                createJSONForEventPayload(jsonKeyAndroid, gson.toJson(event.newTrack))
            )
        }

        player?.addListener(context, PlayerEvent.textTrackChanged) { event: PlayerEvent.TextTrackChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.TEXT_TRACK_CHANGED,
                createJSONForEventPayload(jsonKeyAndroid, gson.toJson(event.newTrack))
            )
        }

        player?.addListener(context, PlayerEvent.imageTrackChanged) { event: PlayerEvent.ImageTrackChanged ->
            sendPlayerEvent(
                KalturaPlayerEvents.IMAGE_TRACK_CHANGED,
                createJSONForEventPayload(jsonKeyAndroid, gson.toJson(event.newTrack))
            )
        }
        player?.addListener(context,
            PlayerEvent.playbackInfoUpdated) { event: PlayerEvent.PlaybackInfoUpdated ->
            sendPlayerEvent(
                KalturaPlayerEvents.PLAYBACK_INFO_UPDATED,
                createJSONForEventPayload(jsonKeyAndroid, gson.toJson(event.playbackInfo))
            )
        }

        player?.addListener(context, PlayerEvent.seeking) { event: PlayerEvent.Seeking ->
            sendPlayerEvent(
                KalturaPlayerEvents.SEEKING,
                createJSONForEventPayload(
                    "targetPosition",
                    (event.targetPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
                )
            )
        }
        player?.addListener(context, PlayerEvent.seeked) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerEvents.SEEKED) }

        player?.addListener(context, PlayerEvent.error) { event: PlayerEvent.Error ->
            sendPlayerEvent(KalturaPlayerEvents.ERROR, getErrorJson(event.error))
        }

        player?.addListener(context, PlayerEvent.metadataAvailable) { event: PlayerEvent.MetadataAvailable ->
            if (event.metadataList.isNotEmpty()) {
                sendPlayerEvent(
                    KalturaPlayerEvents.METADATA_AVAILABLE,
                    gson.toJson(event.metadataList)
                )
            }
        }

        player?.addListener(context, PlayerEvent.eventStreamChanged) { event: PlayerEvent.EventStreamChanged ->
            if (event.eventStreamList.isNotEmpty()) {
                sendPlayerEvent(
                    KalturaPlayerEvents.EVENT_STREAM_CHANGED,
                    gson.toJson(event.eventStreamList)
                )
            }
        }
        player?.addListener(context, PlayerEvent.sourceSelected) { event: PlayerEvent.SourceSelected ->
            if (event.source != null) {
                sendPlayerEvent(KalturaPlayerEvents.SOURCE_SELECTED, gson.toJson(event.source))
            }
        }

        player?.addListener(context,
            PlayerEvent.playbackRateChanged) { event: PlayerEvent.PlaybackRateChanged ->
            if (event.rate > 0) {
                sendPlayerEvent(
                    KalturaPlayerEvents.PLAYBACK_RATE_CHANGED,
                    createJSONForEventPayload("rate", event.rate)
                )
            }
        }

        player?.addListener(context, PlayerEvent.connectionAcquired) { event: PlayerEvent.ConnectionAcquired ->
            if (event.uriConnectionAcquiredInfo != null) {
                sendPlayerEvent(
                    KalturaPlayerEvents.CONNECTION_ACQUIRED,
                    gson.toJson(event.uriConnectionAcquiredInfo)
                )
            }
        }

        player?.addListener(context, PlayerEvent.videoFramesDropped) { event: PlayerEvent.VideoFramesDropped ->
            sendPlayerEvent(
                KalturaPlayerEvents.VIDEO_FRAMES_DROPPED,
                ("{ \"droppedVideoFrames\": " + event.droppedVideoFrames +
                        ", \"droppedVideoFramesPeriod\": " + event.droppedVideoFramesPeriod +
                        ", \"totalDroppedVideoFrames\": " + event.totalDroppedVideoFrames +
                        " }")
            )
        }

        player?.addListener(context,
            PlayerEvent.outputBufferCountUpdate) { event: PlayerEvent.OutputBufferCountUpdate ->
            sendPlayerEvent(
                KalturaPlayerEvents.OUTPUT_BUFFER_COUNT_UPDATE,
                ("{ \"skippedOutputBufferCount\": " + event.skippedOutputBufferCount +
                        ", \"renderedOutputBufferCount\": " + event.renderedOutputBufferCount +
                        " }")
            )
        }

        player?.addListener(context, PlayerEvent.bytesLoaded) { event: PlayerEvent.BytesLoaded ->
            sendPlayerEvent(
                KalturaPlayerEvents.BYTES_LOADED, ("{ \"bytesLoaded\": " + event.bytesLoaded +
                        ", \"dataType\": " + event.dataType +
                        ", \"loadDuration\": " + event.loadDuration +
                        ", \"totalBytesLoaded\": " + event.totalBytesLoaded +
                        ", \"trackType\": " + event.trackType +
                        " }")
            )
        }

        player?.addListener(context, AdEvent.adProgress) { event: AdEvent.AdProgress ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.AD_PROGRESS,
                createJSONForEventPayload(
                    "currentAdPosition",
                    (event.currentAdPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT)
                )
            )
        }

        player?.addListener(context, AdEvent.loaded) { event: AdEvent.AdLoadedEvent ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.LOADED,
                gson.toJson(event.adInfo)
            )
        }

        player?.addListener(context, AdEvent.cuepointsChanged) { event: AdEvent.AdCuePointsUpdateEvent ->
            val cuePointsJson: String? = getCuePointsJson(event.cuePoints)
            cuePointsJson?.let {
                sendPlayerEvent(
                    KalturaPlayerAdEvents.CUEPOINTS_CHANGED,
                    createJSONForEventPayload(event.cuePoints.adPluginName, it)
                )
            }
        }

        player?.addListener(context, AdEvent.started) { _: AdEvent.AdStartedEvent? ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.STARTED
            )
        }

        player?.addListener(context, AdEvent.completed) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.COMPLETED) }

        player?.addListener(context, AdEvent.paused) { _: AdEvent.AdPausedEvent? ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.PAUSED
            )
        }

        player?.addListener(context, AdEvent.resumed) { _: AdEvent.AdResumedEvent? ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.RESUMED
            )
        }

        player?.addListener(context, AdEvent.adBufferStart) { event: AdEvent.AdBufferStart ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.AD_BUFFER_START,
                createJSONForEventPayload("adPosition", event.adPosition)
            )
        }

        player?.addListener(context, AdEvent.adBufferEnd) { event: AdEvent.AdBufferEnd ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.AD_BUFFER_END,
                createJSONForEventPayload("adPosition", event.adPosition)
            )
        }

        player?.addListener(context, AdEvent.adClickedEvent) { event: AdEvent.AdClickedEvent ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.CLICKED,
                createJSONForEventPayload("clickThroughUrl", event.clickThruUrl)
            )
        }

        player?.addListener(context, AdEvent.skipped) { _: AdEvent.AdSkippedEvent? ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.SKIPPED
            )
        }

        player?.addListener(context, AdEvent.adRequested) { event: AdEvent.AdRequestedEvent ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.AD_REQUESTED,
                createJSONForEventPayload("adTagUrl", event.adTagUrl)
            )
        }

        player?.addListener(context, AdEvent.contentPauseRequested) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.CONTENT_PAUSE_REQUESTED) }

        player?.addListener(context, AdEvent.contentResumeRequested) { _: PKEvent? ->
            kalturaPlayerRNView.reMeasureAndReLayout()
            sendPlayerEvent(KalturaPlayerAdEvents.CONTENT_RESUME_REQUESTED)
        }

        player?.addListener(context, AdEvent.allAdsCompleted) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.ALL_ADS_COMPLETED) }

        player?.addListener(context, AdEvent.error) { event: AdEvent.Error ->
            if (event.error.isFatal) {
                sendPlayerEvent(KalturaPlayerAdEvents.ERROR, getErrorJson(event.error))
            }
        }

        player?.addListener(context, AdEvent.adFirstPlay) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_FIRST_PLAY) }

        player?.addListener(context, AdEvent.firstQuartile) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.FIRST_QUARTILE) }

        player?.addListener(context, AdEvent.midpoint) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.MIDPOINT) }

        player?.addListener(context, AdEvent.thirdQuartile) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.THIRD_QUARTILE) }

        player?.addListener(context, AdEvent.skippableStateChanged) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.SKIPPABLE_STATE_CHANGED) }

        player?.addListener(context, AdEvent.tapped) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.TAPPED) }

        player?.addListener(context, AdEvent.iconFallbackImageClosed) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.ICON_FALLBACK_IMAGE_CLOSED) }

        player?.addListener(context, AdEvent.iconTapped) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.ICON_TAPPED) }

        player?.addListener(context, AdEvent.adBreakReady) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_READY) }

        player?.addListener(context, AdEvent.adBreakStarted) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_STARTED) }

        player?.addListener(context, AdEvent.adBreakEnded) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_ENDED) }

        player?.addListener(context, AdEvent.adBreakFetchError) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_FETCH_ERROR) }

        player?.addListener(context, AdEvent.adBreakIgnored) { _: PKEvent? -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BREAK_IGNORED) }

        player?.addListener(context, AdEvent.playHeadChanged
        ) { event: AdEvent.AdPlayHeadEvent ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.PLAY_HEAD_CHANGED,
                createJSONForEventPayload("adPlayHead", event.adPlayHead)
            )
        }
        player?.addListener(context, AdEvent.adPlaybackInfoUpdated
        ) { event: AdEvent.AdPlaybackInfoUpdated ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.AD_PLAYBACK_INFO_UPDATED,
                ("{ \"bitrate\": " + event.bitrate +
                        ", \"height\": " + event.height +
                        ", \"width\": " + event.width +
                        " }")
            )
        }
        player?.addListener(context, AdEvent.daiSourceSelected
        ) { event: AdEvent.DAISourceSelected ->
            sendPlayerEvent(
                KalturaPlayerAdEvents.DAI_SOURCE_SELECTED,
                createJSONForEventPayload("sourceURL", event.sourceURL)
            )
        }
        player?.addListener(context, PhoenixAnalyticsEvent.bookmarkError) { event: PhoenixAnalyticsEvent.BookmarkErrorEvent ->
            sendPlayerEvent(
                KalturaPlayerAnalyticsEvents.PHOENIX_BOOKMARK_ERROR,
                ("{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                        ", \"errorCode\": \"" + event.errorCode + "\" " +
                        ", \"errorType\": \"" + event.type + "\" " +
                        " }")
            )
        }
        player?.addListener(context, PhoenixAnalyticsEvent.concurrencyError) { event: PhoenixAnalyticsEvent.ConcurrencyErrorEvent ->
            sendPlayerEvent(
                KalturaPlayerAnalyticsEvents.PHOENIX_CONCURRENCY_ERROR,
                ("{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                        ", \"errorCode\": \"" + event.errorCode + "\" " +
                        ", \"errorType\": \"" + event.type + "\" " +
                        " }")
            )
        }
        player?.addListener(context, PhoenixAnalyticsEvent.error) { event: PhoenixAnalyticsEvent.ErrorEvent ->
            sendPlayerEvent(
                KalturaPlayerAnalyticsEvents.PHOENIX_ERROR,
                ("{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                        ", \"errorCode\": \"" + event.errorCode + "\" " +
                        ", \"errorType\": \"" + event.type + "\" " +
                        " }")
            )
        }
        player?.addListener(context, InterceptorEvent.sourceUrlSwitched) { event ->
            sendPlayerEvent(
                KalturaPlayerAnalyticsEvents.SOURCE_URL_SWITCHED,
                ("{ \"originalUrl\": \"" + event.originalUrl + "\" " +
                        ", \"updatedUrl\": \"" + event.updatedUrl + "\" " +
                        " }")
            )
        }
        player?.addListener(context, InterceptorEvent.cdnSwitched) { event ->
            sendPlayerEvent(
                KalturaPlayerAnalyticsEvents.CDN_SWITCHED,
                ("{ \"cdnCode\": \"" + event.cdnCode + "\" " +
                        " }")
            )
        }

        val events = getEventUsingReflection(ReflectiveEvents.broadPeak)
        events?.let {
            if (it.isNotEmpty()) {
                for (broadPeakEvent in it) {
                    player?.addListener(context, broadPeakEvent) { event ->
                        if (event.eventType().name == KalturaPlayerAnalyticsEvents.BROADPEAK_ERROR) {
                            val error: Pair<Int, String>? = getBroadPeakError(event)
                            error?.let {
                                sendPlayerEvent(
                                    event.eventType().name,
                                    ("{ \"errorCode\": \"" + error.first + "\" " +
                                            ", \"errorMessage\": \"" + error.second + "\" " +
                                            " }")
                                )
                            }
                        }
                    }
                }
            }
        }

        log.d("Player listeners are added.")
    }

    /*****************************************************
     * Device Event Emitter and event helper methods for *
     * React Native to android event communication       *
     * ***************************************************
     */
    private fun emitter(): DeviceEventManagerModule.RCTDeviceEventEmitter {
        return context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
    }

    /**
     * Create a JSON with only one Key-Value pair
     * @param key JSON object key
     * @param value JSON object value
     * @return JSON object
     */
    @NonNull
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
        log.v("sendPlayerEvent to JS $eventName")
        val eventPayloadMap = convertStringToWritableMap(payloadString)
        if (eventPayloadMap == null) {
            log.e("Event payload is null hence returning event is: $eventName")
            return
        }
        emitter().emit(eventName, eventPayloadMap)
    }

    @Nullable
    private fun convertStringToWritableMap(payload: String?): Any? {
        //log.v("convertStringToJson");
        if (TextUtils.isEmpty(payload)) {
            log.e("Payload string is empty $payload")
            return null
        }

        try {
            val jsonObject = JSONObject(payload)
            return convertWritableMapFromJsonObject(jsonObject)
        } catch (exception: JSONException) {
            //log.e("Payload string can not be converted to JSON object $exception")
            try {
                log.d("Json Object parsing failed. Trying with Json Array.")
                val jsonArray = JSONArray(payload)
                return convertWritableArrayFromJsonArray(jsonArray)
            } catch (jsonArrayException: JSONException) {
                log.e("Payload string can not be converted to JSON Array $exception")
            }
        }
        return null
    }

    @NonNull
    private fun convertWritableMapFromJsonObject(jsonObject: JSONObject): WritableMap? {
        //log.v("convertJsonToMap");
        val map: WritableMap = WritableNativeMap()
        val iterator = jsonObject.keys()

        while (iterator.hasNext()) {
            val key = iterator.next()
            var value: Any? = null

            try {
                value = jsonObject[key]
            } catch (e: JSONException) {
                log.e("Exception while parsing Json object : $key Exception is : ${e.message}")
            }

            if (value == null) {
                log.e("Exception while parsing Json object value is null, hence returning null.")
                return null
            }

            when (value) {
                is JSONObject -> {
                    map.putMap(key, convertWritableMapFromJsonObject(value))
                }
                is JSONArray -> {
                    try {
                        map.putArray(key, convertWritableArrayFromJsonArray(value))
                    } catch (e: JSONException) {
                        log.e("Exception while parsing Json Array key: " + key + "value is: " + value + "Exception is : " + e.message)
                    }
                }
                is Boolean -> {
                    map.putBoolean(key, value)
                }
                is Int -> {
                    map.putInt(key, value)
                }
                is Double -> {
                    map.putDouble(key, value)
                }
                is String -> {
                    map.putString(key, value as String?)
                }
                else -> {
                    map.putString(key, value.toString())
                }
            }
        }
        return map
    }

    @Throws(JSONException::class)
    private fun convertWritableArrayFromJsonArray(jsonArray: JSONArray): WritableArray {
        val array: WritableArray = WritableNativeArray()

        for (i in 0 until jsonArray.length()) {
            when (val value = jsonArray[i]) {
                is JSONObject -> {
                    array.pushMap(convertWritableMapFromJsonObject(value))
                }
                is JSONArray -> {
                    array.pushArray(convertWritableArrayFromJsonArray(value))
                }
                is Boolean -> {
                    array.pushBoolean(value)
                }
                is Int -> {
                    array.pushInt(value)
                }
                is Double -> {
                    array.pushDouble(value)
                }
                is String -> {
                    array.pushString(value)
                }
                else -> {
                    array.pushString(value.toString())
                }
            }
        }
        return array
    }
}
