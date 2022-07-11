import KalturaPlayer
import PlayKit
import PlayKitProviders
import PlayKitYoubora
import PlayKitBroadpeak

@objc(KalturaPlayerEvents)
class KalturaPlayerEvents: RCTEventEmitter {
    public static var emitter: RCTEventEmitter!
    var hasListener: Bool = false

    override init() {
        super.init()
        KalturaPlayerEvents.emitter = self
    }

    override func startObserving() {
        hasListener = true
    }

    override func stopObserving() {
        hasListener = false
    }

    override func supportedEvents() -> [String]! {
        return [
            "KPlayerEvent", "canPlay", "durationChanged", "stopped", "ended", "loadedMetadata", "play", "pause", "playing", "seeking", "seeked", "replay",
            "tracksAvailable", "textTrackChanged", "audioTrackChanged", "videoTrackChanged", "playbackInfo", "stateChanged",
            "timedMetadata", "sourceSelected", "loadedTimeRanges", "playheadUpdate", "error", "errorLog", "playbackStalled", "playbackRate", "timeUpdate", "loadMediaFailed", "bookmarkError"
        ]
    }
}

@objc(KalturaPlayerViewManager)
class KalturaPlayerViewManager: RCTViewManager {
    var player: KalturaPlayerRNView!
    var kalturaPlayer: KalturaOTTPlayer!
    var bufferedTime: Double = 0

    override func view() -> (KalturaPlayerRNView) {
        return player
    }

    @objc func setup(_ partnerId: Int64, options: NSDictionary) {
        DispatchQueue.main.async {
            self.player = KalturaPlayerRNView()
            self.kalturaPlayer = self.player.setup(partnerId: partnerId, options: options)
            self.observeAllEvents()
        }
    }

    @objc func load(_ assetId: String, options: NSDictionary) {
        DispatchQueue.main.async {
            self.player.load(assetId: assetId, options: options)
        }
    }

    @objc func play() {
        DispatchQueue.main.async {
            self.kalturaPlayer.play()
        }
    }

    @objc func pause() {
        DispatchQueue.main.async {
            self.kalturaPlayer.pause()
        }
    }

    @objc func replay() {
        DispatchQueue.main.async {
            self.kalturaPlayer.replay()
        }
    }

    @objc func stop() {
        DispatchQueue.main.async {
            self.kalturaPlayer.stop()
        }
    }

    @objc func setVolume(_ volume: Float) {
        DispatchQueue.main.async {
            self.kalturaPlayer.volume = volume
        }
    }

    @objc func seekTo(_ to: TimeInterval) {
        DispatchQueue.main.async {
            self.kalturaPlayer.seek(to: to)
        }
    }

    @objc func setPlayerVisibility(_ isVisible: Bool) {
        DispatchQueue.main.async {
            self.kalturaPlayer!.view?.isHidden = !isVisible;
        }
    }

    @objc func setKeepAwake(_ value: Bool) {
        DispatchQueue.main.async {
            UIApplication.shared.isIdleTimerDisabled = value
        }
    }

    @objc func setAutoplay(_ value: Bool) {
        DispatchQueue.main.async {
            let options = PlayerOptions()
            options.autoPlay = value
            self.kalturaPlayer.updatePlayerOptions(options)
        }
    }

    @objc func changeTrack(_ trackId: String) {
        DispatchQueue.main.async {
            self.kalturaPlayer.selectTrack(trackId: trackId)
        }
    }

    @objc func prepare() {
        DispatchQueue.main.async {
            self.kalturaPlayer.prepare()
        }
    }

    @objc func destroy() {
        DispatchQueue.main.async {
            self.kalturaPlayer.destroy()
        }
    }

    func safeJsonValue(value: String?) -> String? {
        return value == nil ? "" : value;
    }

    @objc func observeAllEvents() {
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.canPlay) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "canPlay", body: [])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playing) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "playing", body: [])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.play) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "play", body: [])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.pause) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "pause", body: [])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.stopped) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "stopped", body: [])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.durationChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "durationChanged", body: ["duration": event.duration])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playheadUpdate) { event in
            let currentTime = event.currentTime as! Double
            var bufferedTime = self.bufferedTime

            if (bufferedTime < currentTime) {
                bufferedTime = currentTime
            }

            if (self.kalturaPlayer.isLive()) {
                let currentProgramTime = self.kalturaPlayer.currentProgramTime
                let currentProgramTimeEpochSeconds = currentProgramTime?.timeIntervalSince1970
                let currentProgramTimeDouble = ((currentProgramTimeEpochSeconds ?? 0) as Double) * 1000

                KalturaPlayerEvents.emitter.sendEvent(withName: "timeUpdate", body: [
                    "position": currentTime,
                    "bufferPosition": bufferedTime,
                    "currentProgramTime": currentProgramTimeDouble
                ])
            } else {
                KalturaPlayerEvents.emitter.sendEvent(withName: "timeUpdate", body: [
                    "position": currentTime,
                    "bufferPosition": bufferedTime
                ])
            }
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.ended) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "ended", body: event.data)
        }

        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.sourceSelected) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "sourceSelected", body: event.data)
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.tracksAvailable) { event in
            var audioTracks = [] as Array
            let selectedAudioTrackId = self.kalturaPlayer.currentAudioTrack
            let eventAudioTracks = event.tracks?.audioTracks ?? []
            for track in eventAudioTracks {
                audioTracks.append([
                    "id": self.safeJsonValue(value: track.id) ?? "",
                    "label": self.safeJsonValue(value: track.title) ?? "",
                    "language": self.safeJsonValue(value: track.language) ?? "",
                    "isAdaptive": false, // Audio and text tracks are never adaptive. Video tracks don't exist in HLS/AVPlayer.
                    "isSelected": selectedAudioTrackId == track.id
                ])
            }

            var textTracks = [] as Array;
            let selectedTextTrackId = self.kalturaPlayer.currentTextTrack;
            let eventTextTracks = event.tracks?.textTracks ?? []
            for track in eventTextTracks {
                textTracks.append([
                    "id": self.safeJsonValue(value: track.id) ?? "",
                    "label": self.safeJsonValue(value: track.title) ?? "",
                    "language": self.safeJsonValue(value: track.language) ?? "",
                    "isAdaptive": false, // Audio and text tracks are never adaptive. Video tracks don't exist in HLS/AVPlayer.
                    "isSelected": selectedTextTrackId == track.id
                ])
            }

            KalturaPlayerEvents.emitter.sendEvent(withName: "tracksAvailable", body: [
                "audio": audioTracks,
                "text": textTracks,
                "video": []
            ])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.videoTrackChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "videoTrackChanged", body: ["bitrate": event.bitrate])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.audioTrackChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "audioTrackChanged", body: [
                "id": self.safeJsonValue(value: event.selectedTrack?.id) ?? "",
                "label": self.safeJsonValue(value: event.selectedTrack?.title) ?? "",
                "language": self.safeJsonValue(value: event.selectedTrack?.language) ?? "",
                "isSelected": true
            ])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.textTrackChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "textTrackChanged", body: [
                "id": self.safeJsonValue(value: event.selectedTrack?.id) ?? "",
                "label": self.safeJsonValue(value: event.selectedTrack?.title) ?? "",
                "language": self.safeJsonValue(value: event.selectedTrack?.language) ?? "",
                "isSelected": true
            ])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playbackInfo) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "playbackInfo", body: ["totalBitrate": event.playbackInfo?.indicatedBitrate])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.error) { event in
            let errorMessage = event.error?.userInfo[NSLocalizedDescriptionKey] as! String
            var errorCause: String? = (event.error?.localizedFailureReason)

            var errorCode = ""
            var errorType = ""
            switch (event.error?.code) {
                case 7001:
                    errorCode = "7007"
                    errorType = "LOAD_ERROR"
                    break;
                case 7003:
                    errorCode = "7000"
                    errorType = "SOURCE_ERROR"
                   break;
                default:
                    errorCode = "7002"
                    errorType = "UNEXPECTED"
                   break;
            }

            if (errorCause == nil || errorCause?.count == 0) {
                errorCause = errorMessage
            }

            KalturaPlayerEvents.emitter.sendEvent(withName: "error", body: [
                "errorType": errorType,
                "errorCode": errorCode,
                "errorSeverity": "Fatal",
                "errorMessage": errorMessage,
                "errorCause": errorCause
            ])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.stateChanged) { event in
            let state: PlayerState = event.newState
            var stateName: String = ""
            switch (state) {
                case PlayKit.PlayerState.buffering:
                    stateName = "BUFFERING";
                    break;
                case PlayKit.PlayerState.idle:
                    stateName = "IDLE";
                    break;
                case PlayKit.PlayerState.ready:
                    stateName = "READY";
                    break;
                default:
                    return; // discard event
            }
            KalturaPlayerEvents.emitter.sendEvent(withName: "stateChanged", body: ["newState": stateName])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.seeking) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "seeking", body: ["targetPosition": event.targetSeekPosition])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.seeked) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "seeked", body: [])
        }
        self.kalturaPlayer.addObserver(self, event: OttEvent.bookmarkError) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "bookmarkError", body: event.data)
        }
        self.kalturaPlayer.addObserver(self, event: OttEvent.concurrency) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "loadMediaFailed", body: [
                "code": "ConcurrencyLimitation",
                "message": "Concurrency limitation",
                "name": "OTTError",
                "type": "loadMediaFailed"
            ])
        }
    }
}

class KalturaPlayerRNView : UIView {
    var kalturaPlayer: KalturaOTTPlayer!

    @objc func setup(partnerId: Int64, options: NSDictionary) -> KalturaOTTPlayer {
        KalturaOTTPlayer.setup(partnerId: partnerId, serverURL: options["serverUrl"] as! String)

        let playerOptions = PlayerOptions()
        playerOptions.preload = options["preload"] as! Bool
        playerOptions.autoPlay = options["autoplay"] as! Bool
        playerOptions.ks = options["ks"] as? String

        if let plugins = options["plugins"] as? Dictionary<String, Any> {
            if let pluginsConfig = getPluginsConfigs(plugins:plugins) {
                playerOptions.pluginConfig = pluginsConfig
            }
        }
        kalturaPlayer = KalturaOTTPlayer(options: playerOptions)

        let playerView = KalturaPlayerView()
        playerView.contentMode = .scaleAspectFit
        playerView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        self.addSubview(playerView)
        kalturaPlayer.view = playerView
        return kalturaPlayer
    }

    @objc func load(assetId: String, options: NSDictionary) {
        let mediaOptions = OTTMediaOptions()
        mediaOptions.assetId = assetId
        mediaOptions.ks = options["ks"] as? String
        mediaOptions.assetType = getAssetType(str: options["assetType"] as! String)
        mediaOptions.playbackContextType = getPlaybackContextType(str: options["playbackContextType"] as! String)
        //mediaOptions.adapterData = options["adapterData"] as? [String : String]

        if (options["plugins"] != nil){
            updatePluginsConfig(plugins: options["plugins"] as! Dictionary<String, Any>)
        }

        if ((options["assetReferenceType"]) != nil) {
            mediaOptions.assetReferenceType = getAssetReferenceType(str: options["assetReferenceType"] as! String)
        }

        if ((options["protocol"]) != nil) {
            mediaOptions.networkProtocol = options["protocol"] as! String
        }

        if ((options["format"]) != nil) {
            mediaOptions.formats = [options["format"] as! String]
        }

        if ((options["urlType"]) != nil) {
            mediaOptions.urlType = options["urlType"] as? String
        }

        if ((options["fileId"]) != nil) {
            mediaOptions.fileIds = [options["urlType"] as! String]
        }

        if ((options["streamerType"]) != nil) {
            mediaOptions.streamerType = (options["streamerType"] as! String).lowercased()
        }

        if ((options["startPosition"]) != nil) {
            mediaOptions.startTime = ((options["startPosition"] as? TimeInterval)!)
        }

        var initialVolume = 1.0
        if ((options["initialVolume"]) != nil) {
            initialVolume = ((options["initialVolume"] as? Double)!)
        }

        kalturaPlayer.loadMedia(options: mediaOptions) { error in
            if (error != nil) {
                print("Error in loadMedia: %@", error!)
            }

            if (initialVolume < 1.0) {
                self.kalturaPlayer.volume = Float(initialVolume)
            }
        }
    }

    func getAssetType(str: String) -> AssetType {
        if (str.caseInsensitiveCompare("media") == ComparisonResult.orderedSame) { return AssetType.media }
        if (str.caseInsensitiveCompare("recording") == ComparisonResult.orderedSame) { return AssetType.recording }
        if (str.caseInsensitiveCompare("epg") == ComparisonResult.orderedSame) { return AssetType.epg }

        return AssetType.unset
    }

    func getAssetReferenceType(str: String) -> AssetReferenceType {
        if (str.caseInsensitiveCompare("media") == ComparisonResult.orderedSame) { return AssetReferenceType.media }
        if (str.caseInsensitiveCompare("epgInternal") == ComparisonResult.orderedSame) { return AssetReferenceType.epgInternal }
        if (str.caseInsensitiveCompare("epgExternal") == ComparisonResult.orderedSame) { return AssetReferenceType.epgExternal }
        if (str.caseInsensitiveCompare("npvr") == ComparisonResult.orderedSame) { return AssetReferenceType.npvr }

        return AssetReferenceType.unset
    }

    func getPlaybackContextType(str: String) -> PlaybackContextType {
        if (str.caseInsensitiveCompare("playback") == ComparisonResult.orderedSame) { return PlaybackContextType.playback }
        if (str.caseInsensitiveCompare("catchup") == ComparisonResult.orderedSame) { return PlaybackContextType.catchup }
        if (str.caseInsensitiveCompare("trailer") == ComparisonResult.orderedSame) { return PlaybackContextType.trailer }
        if (str.caseInsensitiveCompare("start_over") == ComparisonResult.orderedSame) { return PlaybackContextType.startOver }

        return PlaybackContextType.unset
    }

    func getPluginsConfigs(plugins: Dictionary<String, Any>)-> PluginConfig?{
        var pluginConfigs: [String: Any] = [:]
        if let youboraParams = plugins["youbora"] as? Dictionary<String, Any> {
            let youboraConfig = AnalyticsConfig(params: youboraParams)
            pluginConfigs[YouboraPlugin.pluginName] = youboraConfig
        }
        if let broadpeakParams = plugins["broadpeak"] as? Dictionary<String, Any> {
            let broadpeakConfig = getBroadpeakConfig(broadpeakParams: broadpeakParams)
            pluginConfigs[BroadpeakMediaEntryInterceptor.pluginName] = broadpeakConfig
        }
        if (!pluginConfigs.isEmpty){
            return PluginConfig(config: pluginConfigs)
        }
        return nil
    }

    func getBroadpeakConfig(broadpeakParams: Dictionary<String, Any>)-> BroadpeakConfig{
        let broadpeakConfig = BroadpeakConfig()
        if let analyticsAddress = broadpeakParams["analyticsAddress"] as? String {
            broadpeakConfig.analyticsAddress = analyticsAddress
        }
        if let broadpeakDomainNames = broadpeakParams["broadpeakDomainNames"] as? String {
            broadpeakConfig.broadpeakDomainNames = broadpeakDomainNames
        }
        if let nanoCDNHost = broadpeakParams["nanoCDNHost"] as? String {
            broadpeakConfig.nanoCDNHost = nanoCDNHost
        }
        if let uuid = broadpeakParams["uuid"] as? String {
            broadpeakConfig.uuid = uuid
        }   
        return broadpeakConfig
    }
    
    func updatePluginsConfig(plugins: Dictionary<String, Any>) {
        if (plugins["youbora"] != nil) {
            updateYouboraConfig(youboraPlugin: plugins["youbora"] as! Dictionary<String, Any>)
        }
    }
    
    func updateYouboraConfig(youboraPlugin: Dictionary<String,Any>) {
        let youboraConfig = AnalyticsConfig(params:youboraPlugin)
        kalturaPlayer.updatePluginConfig(pluginName: YouboraPlugin.pluginName, config: youboraConfig)
    }
}
