//
//  KalturaPlayerRN.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/06/2022.
//

import Foundation
import KalturaPlayer
import PlayKit
import PlayKitProviders

public enum KalturaPlayerRNError: PKError {
    case setupFailed(message: String)
    case loadMediaFailed(message: String)
    case retrieveCurrentPositionFailed
    case retrieveIsPlayingFailed
    case retrieveIsLiveFailed
    
    public static let domain = "com.kaltura.player.rn.error"
    public static let errorMessageKey = "message"
    
    public var code: Int {
        switch self {
        case .setupFailed: return 9000
        case .loadMediaFailed: return 9001
        case .retrieveCurrentPositionFailed: return 9002
        case .retrieveIsPlayingFailed: return 9003
        case .retrieveIsLiveFailed: return 9004
        }
    }
    
    public var errorDescription: String {
        switch self {
        case .setupFailed(let message):
            return "The setup could not be completed. \(message)"
        case .loadMediaFailed(let message):
            return "Loading the media failed. \(message)"
        case .retrieveCurrentPositionFailed:
            return "Retrieving the current position failed."
        case .retrieveIsPlayingFailed:
            return "Retrieving whether the player is playing or not failed."
        case .retrieveIsLiveFailed:
            return "Retrieving whether the media is a live content or not failed."
        }
    }
    
    public var userInfo: [String : Any] {
        switch self {
        case .setupFailed(let message):
            return [KalturaPlayerRNError.errorMessageKey: message]
        case .loadMediaFailed(let message):
            return [KalturaPlayerRNError.errorMessageKey: message]
        default:
            return [String: Any]()
        }
    }
}


/** Do not create an instance of this class.
    Create one of it's subclasses: BasicKalturaPlayerRN / OTTKalturaPlayerRN / OVPKalturaPlayerRN
 **/
class KalturaPlayerRN: NSObject {
    
    var kalturaPlayer: KalturaPlayer? {
        return nil
    }
    
    private(set) var initOptions: RNKPInitOptions
    private(set) var playerOptions: PlayerOptions
    
    /** Do not create an instance of this class.
        Create one of it's subclasses: BasicKalturaPlayerRN / OTTKalturaPlayerRN / OVPKalturaPlayerRN
     **/
    internal init(withOptions initOptions: RNKPInitOptions) {
        self.initOptions = initOptions
        self.playerOptions = KalturaPlayerRN.fetchPlayerOptions(initOptions)
        super.init()
    }
    
    private static func fetchPlayerOptions(_ initOptions: RNKPInitOptions) -> PlayerOptions {
        let playerOptions = PlayerOptions()
        
        if let preload = initOptions.preload {
            playerOptions.preload = preload
        }
        
        if let autoPlay = initOptions.autoplay {
            playerOptions.autoPlay = autoPlay
        }
        
        if let plugins = initOptions.plugins {
            if let pluginConfigs = RNKPKnownPlugins.configs(plugins) {
                playerOptions.pluginConfig = pluginConfigs
            }
        }
        
        if let ks = initOptions.ks {
            playerOptions.ks = ks
        }
        
        return playerOptions
    }
    
    private func updateNetworkSetting() {
        // ABRSettings, in iOS it's inside NetworkSettings.
        if let maxVideoBitrate = initOptions.abrSettings?.maxVideoBitrate {
            kalturaPlayer?.settings.network.preferredPeakBitRate =  maxVideoBitrate
        }
        
        guard let networkSettings = initOptions.networkSettings else { return }
        
        if let autoBuffer = networkSettings.autoBuffer {
            kalturaPlayer?.settings.network.autoBuffer = autoBuffer
        }
        
        if let automaticallyWaitsToMinimizeStalling = networkSettings.automaticallyWaitsToMinimizeStalling {
            kalturaPlayer?.settings.network.automaticallyWaitsToMinimizeStalling = automaticallyWaitsToMinimizeStalling
        }
        
        if let preferredForwardBufferDuration = networkSettings.preferredForwardBufferDuration {
            kalturaPlayer?.settings.network.preferredForwardBufferDuration = preferredForwardBufferDuration
        }
    }
    
    private func updateLowLatencySettings () {
        if let targetOffsetMs = initOptions.lowLatencyConfig?.targetOffsetMs {
            kalturaPlayer?.settings.lowLatency.targetOffsetMs = targetOffsetMs
        }
    }
    
    private func updateTrackSelectionSettings() {
        guard let trackSelection = initOptions.trackSelection else { return }
        
        if let textMode = trackSelection.textMode {
            kalturaPlayer?.settings.trackSelection.textSelectionMode = getTrackSelectionMode(textMode)
        }
        
        if let textLanguage = trackSelection.textLanguage {
            kalturaPlayer?.settings.trackSelection.textSelectionLanguage = textLanguage
        }
        
        if let audioMode = trackSelection.audioMode {
            kalturaPlayer?.settings.trackSelection.audioSelectionMode = getTrackSelectionMode(audioMode)
        }
        
        if let audioLanguage = trackSelection.audioLanguage {
            kalturaPlayer?.settings.trackSelection.audioSelectionLanguage = audioLanguage
        }
    }
    
    private func getTrackSelectionMode(_ mode: String) -> TrackSelectionMode {
        switch mode.lowercased() {
        case "auto":
            return TrackSelectionMode.auto
        case "selection":
            return TrackSelectionMode.selection
        default:
            return TrackSelectionMode.off
        }
    }
    
    func updateSettings() {
        updateNetworkSetting()
        updateLowLatencySettings()
        updateTrackSelectionSettings()
    }

// MARK: - Subclass implementations
    
    func load(assetId: String, mediaAsset: String, callback: @escaping (_ error: KalturaPlayerRNError?) -> Void) {
        // Shouldn't get here! Should be implemented in sub class.
        fatalError("Subclasses need to implement the `load` method.")
    }
}

// MARK: - Common implementations

extension KalturaPlayerRN {
    
    func connectView(_ view: KalturaPlayerView) {
        kalturaPlayer?.view = view
    }
    
    func observeAllEvents() {
        
        observePlayerEvents()
        
        
        // iOS doesn't have connectionAcquired
        
        // iOS doesn't have videoFramesDropped
        
        // iOS doesn't have outputBufferCountUpdate
        
        // iOS doesn't have bytesLoaded
        
        // iOS has that Android doesn't have: "loadedTimeRanges",  "errorLog", "playbackStalled"
        
        // TODO: Add adEvents
        
//        kalturaPlayer.addObserver(self, event: OttEvent.bookmarkError) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "bookmarkError", body: event.data)
//        }
//        kalturaPlayer.addObserver(self, event: OttEvent.concurrency) { event in
//            KalturaPlayerEvents.emitter.sendEvent(withName: "concurrencyError", body: event.data)
//        }
    }
    
    func removeObservationForAllEvents() {
        guard let kalturaPlayer = self.kalturaPlayer else { return }
        
        kalturaPlayer.removeObserver(self, events: PlayKit.PlayerEvent.allEventTypes)
    }
    
    func updatePlugins(_ plugins: Plugins) {
        guard let pluginConfig = RNKPKnownPlugins.configs(plugins) else { return }
        
        for (name, config) in pluginConfig.config {
            kalturaPlayer?.updatePluginConfig(pluginName: name, config: config)
        }
    }
}

extension KalturaPlayerRN {
    
    private func observePlayerEvents() {
        guard let kalturaPlayer = self.kalturaPlayer else { return }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.canPlay) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.canPlay.rawValue, body: [])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playing) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playing.rawValue, body: [])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.play) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.play.rawValue, body: [])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.pause) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.pause.rawValue, body: [])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.ended) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.ended.rawValue, body: []) //event.data)
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.stopped) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.stopped.rawValue, body: [])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.durationChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.durationChanged.rawValue, body: ["duration": event.duration])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playheadUpdate) { event in
            let currentTime = event.currentTime?.doubleValue ?? 0
            var bufferedTime = kalturaPlayer.bufferedTime

            if (bufferedTime < currentTime) {
                bufferedTime = currentTime
            }

            if (kalturaPlayer.isLive()) {
                let currentProgramTime = kalturaPlayer.currentProgramTime
                let currentProgramTimeEpochSeconds = currentProgramTime?.timeIntervalSince1970
                let currentProgramTimeDouble = ((currentProgramTimeEpochSeconds ?? 0) as Double) * 1000

                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playheadUpdated.rawValue, body: [
                    "position": currentTime,
                    "bufferPosition": bufferedTime,
                    "currentProgramTime": currentProgramTimeDouble
                    // TODO: currentLiveOffset
                ])
            } else {
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playheadUpdated.rawValue, body: [
                    "position": currentTime,
                    "bufferPosition": bufferedTime
                ])
            }
            // TODO: Different from Android, need to verify.
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.loadedTimeRanges) { event in
            var timeRanges = [] as Array
            let eventTimeRanges = event.timeRanges ?? []
            for range in eventTimeRanges {
                timeRanges.append([
                    "start": range.start,
                    "end": range.end,
                    "duration": range.duration
                ])
            }
            
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.loadedTimeRanges.rawValue, body: [
                "timeRanges": timeRanges
            ])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.stateChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.stateChanged.rawValue, body: ["newState": event.newState.description])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.tracksAvailable) { event in
            var audioTracks = [] as Array
            let selectedAudioTrackId = kalturaPlayer.currentAudioTrack
            let eventAudioTracks = event.tracks?.audioTracks ?? []
            for track in eventAudioTracks {
                audioTracks.append([
                    "id": track.id,
                    "bitrate": -1,
                    "language": track.language ?? "",
                    "label": track.title,
                    "channelCount": -1,
                    "isSelected": selectedAudioTrackId == track.id
                ])
            }

            var textTracks = [] as Array
            let selectedTextTrackId = kalturaPlayer.currentTextTrack;
            let eventTextTracks = event.tracks?.textTracks ?? []
            for track in eventTextTracks {
                textTracks.append([
                    "id": track.id,
                    "language": track.language ?? "",
                    "label": track.title,
                    "isSelected": selectedTextTrackId == track.id
                ])
            }
            
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.tracksAvailable.rawValue, body: [
                "audio": audioTracks,
                "text": textTracks,
                "video": [],
                "image": []
            ])
            
            // TODO: Different from Android, need to verify.
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.loadedMetadata) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.loadedMetadata.rawValue, body: [])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.replay) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.replay.rawValue, body: [])
        }
        
        // iOS doesn't have volumeChanged
        
        // iOS doesn't have surfaceAspectRationSizeModeChanged
        
        // iOS doesn't have subtitlesStyleChanged
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.videoTrackChanged) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.videoTrackChanged.rawValue, body: ["bitrate": event.bitrate])
            // iOS doesn't send the track, we only have the bitrate.
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.audioTrackChanged) { event in
            guard let audioTrack = event.selectedTrack else { return }
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.audioTrackChanged.rawValue, body: [
                "id": audioTrack.id,
                "label": audioTrack.title,
                "language": audioTrack.language ?? "",
                "isSelected": true
            ])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.textTrackChanged) { event in
            guard let textTrack = event.selectedTrack else { return }
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.textTrackChanged.rawValue, body: [
                "id": textTrack.id,
                "label": textTrack.title,
                "language": textTrack.language ?? "",
                "isSelected": true
            ])
        }
        
        // iOS doesn't have imageTrackChanged
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playbackInfo) { event in
            guard let playbackInfo = event.playbackInfo else { return }
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playbackInfoUpdated.rawValue, body: [
                "bitrate": playbackInfo.bitrate,
                "indicatedBitrate": playbackInfo.indicatedBitrate,
                "observedBitrate": playbackInfo.observedBitrate,
                "averageVideoBitrate": playbackInfo.averageVideoBitrate,
                "averageAudioBitrate": playbackInfo.averageAudioBitrate,
                "uri": playbackInfo.uri ?? ""
            ])
            
            // TODO: Different from Android, need to verify.
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.seeking) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.seeking.rawValue, body: [
                "targetPosition": event.targetSeekPosition
            ])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.seeked) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.seeked.rawValue, body: [])
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.error) { event in
            let errorMessage = event.error?.localizedDescription
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

            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.error.rawValue, body: [
                "errorType": errorType,
                "errorCode": errorCode,
                "errorSeverity": "Fatal",
                "errorMessage": errorMessage,
                "errorCause": errorCause
            ])
            
            // TODO: Need to verify
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.timedMetadata) { event in
            guard let metadata = event.timedMetadata else { return }
            
            // TODO: Need to define what to send.
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.sourceSelected) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.sourceSelected.rawValue, body: event.mediaSource)
        }
        
        kalturaPlayer.addObserver(self, event: PlayKit.PlayerEvent.playbackRate) { event in
            KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playbackRateChanged.rawValue, body: event.palybackRate)
        }
        
        // iOS has in addition errorLog and playbackStalled
    }
}
