import KalturaPlayer
import PlayKit
import PlayKitProviders

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
            "canPlay", "durationChanged", "stopped", "ended", "loadedMetadata", "play", "pause", "playing", "seeking", "seeked", "replay",
            "tracksAvailable", "textTrackChanged", "audioTrackChanged", "videoTrackChanged", "playbackInfo", "stateChanged",
            "timedMetadata", "sourceSelected", "loadedTimeRanges", "playheadUpdate", "error", "errorLog", "playbackStalled", "playbackRate"
        ]
    }
}

@objc(KalturaPlayerViewManager)
class KalturaPlayerViewManager: RCTViewManager {
    var player: KalturaPlayerRNView!
    var kalturaPlayer: KalturaOTTPlayer!

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
            // setVolume not found
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
    
    @objc func destroy() {
        DispatchQueue.main.async {
            self.kalturaPlayer.destroy()
        }
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
            KalturaPlayerEvents.emitter.sendEvent(withName: "playheadUpdate", body: event.data)
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.sourceSelected) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "sourceSelected", body: event.data)
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.tracksAvailable) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "tracksAvailable", body: event.data)
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.videoTrackChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "videoTrackChanged", body: ["bitrate": event.bitrate])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.audioTrackChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "audioTrackChanged", body: event.data)
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.textTrackChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "textTrackChanged", body: event.data)
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playbackInfo) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "playbackInfo", body: ["totalBitrate": event.playbackInfo?.indicatedBitrate])
        }
        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.error) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: "error", body: event.data)
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
            KalturaPlayerEvents.emitter.sendEvent(withName: "concurrencyError", body: event.data)
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
        
        kalturaPlayer.loadMedia(options: mediaOptions) { error in
            if (error != nil) {
                print("Error in loadMedia: %@", error!)
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
        if (str.caseInsensitiveCompare("startOver") == ComparisonResult.orderedSame) { return PlaybackContextType.startOver }
        
        return PlaybackContextType.unset
    }
}
