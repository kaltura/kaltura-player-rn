import KalturaPlayer
//import PlayKit
//import PlayKitProviders



@objc(KalturaPlayerViewManager)
class KalturaPlayerViewManager: RCTViewManager {
    var kalturaPlayerRNView: KalturaPlayerRNView!
    var kalturaPlayer: KalturaOTTPlayer?

    override func view() -> (KalturaPlayerRNView) {
        kalturaPlayerRNView = KalturaPlayerRNView()
        return kalturaPlayerRNView
    }
    
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }
    

    
    
//    @objc func addListeners(_ value: Bool) {
//
//    }
//
//    @objc func removeListeners(_ value: Bool) {
//
//    }
    
//    @objc func partnerId(_ value: Int64) {
//        
//    }
    
    
//    @objc func setup(_ partnerId: Int64, options: NSDictionary) {
//        DispatchQueue.main.async {
//            self.player = KalturaPlayerRNView()
//            self.kalturaPlayer = self.player.setup(partnerId: partnerId, options: options)
//            self.observeAllEvents()
//        }
//    }
//
//    @objc func load(_ assetId: String, options: NSDictionary) {
//        DispatchQueue.main.async {
//            self.player.load(assetId: assetId, options: options)
//        }
//    }
//
//    @objc func play() {
//        DispatchQueue.main.async {
//            self.kalturaPlayer.play()
//        }
//    }
//
//    @objc func pause() {
//        DispatchQueue.main.async {
//            self.kalturaPlayer.pause()
//        }
//    }
//
//    @objc func replay() {
//        DispatchQueue.main.async {
//            self.kalturaPlayer.replay()
//        }
//    }
//
//    @objc func stop() {
//        DispatchQueue.main.async {
//            self.kalturaPlayer.stop()
//        }
//    }
//
//    @objc func setVolume(_ volume: Float) {
//        DispatchQueue.main.async {
//            // setVolume not found
//        }
//    }
//
//    @objc func seekTo(_ to: TimeInterval) {
//        DispatchQueue.main.async {
//            self.kalturaPlayer.seek(to: to)
//        }
//    }
//
//    @objc func setPlayerVisibility(_ isVisible: Bool) {
//        DispatchQueue.main.async {
//            self.kalturaPlayer!.view?.isHidden = !isVisible;
//        }
//    }
//
//    @objc func setAutoplay(_ value: Bool) {
//        DispatchQueue.main.async {
//            let options = PlayerOptions()
//            options.autoPlay = value
//            self.kalturaPlayer.updatePlayerOptions(options)
//        }
//    }
//
//    @objc func destroy() {
//        DispatchQueue.main.async {
//            self.kalturaPlayer.destroy()
//        }
//    }
//
//    @objc func observeAllEvents() {
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.canPlay) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "canPlay", body: [])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playing) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "playing", body: [])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.play) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "play", body: [])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.pause) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "pause", body: [])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.stopped) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "stopped", body: [])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.durationChanged) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "durationChanged", body: ["duration": event.duration])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playheadUpdate) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "playheadUpdate", body: event.data)
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.sourceSelected) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "sourceSelected", body: event.data)
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.tracksAvailable) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "tracksAvailable", body: event.data)
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.videoTrackChanged) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "videoTrackChanged", body: ["bitrate": event.bitrate])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.audioTrackChanged) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "audioTrackChanged", body: event.data)
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.textTrackChanged) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "textTrackChanged", body: event.data)
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playbackInfo) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "playbackInfo", body: ["totalBitrate": event.playbackInfo?.indicatedBitrate])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.error) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "error", body: event.data)
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.stateChanged) { event in
//            let state: PlayerState = event.newState
//            var stateName: String = ""
//            switch (state) {
//                case PlayKit.PlayerState.buffering:
//                    stateName = "BUFFERING";
//                    break;
//                case PlayKit.PlayerState.idle:
//                    stateName = "IDLE";
//                    break;
//                case PlayKit.PlayerState.ready:
//                    stateName = "READY";
//                    break;
//                default:
//                    return; // discard event
//            }
//            KalturaPlayerEvents.emitter.sendEvent(withName: "stateChanged", body: ["newState": stateName])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.seeking) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "seeking", body: ["targetPosition": event.targetSeekPosition])
//        }
//        self.kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.seeked) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "seeked", body: [])
//        }
//        self.kalturaPlayer.addObserver(self, event: OttEvent.bookmarkError) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "bookmarkError", body: event.data)
//        }
//        self.kalturaPlayer.addObserver(self, event: OttEvent.concurrency) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "concurrencyError", body: event.data)
//        }
//    }
}


