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
    
    private func updateVRSettings() {
        guard let vrSettings = initOptions.vrSettings else { return }
        
        if let vrController = kalturaPlayer?.getController(type: PKVRController.self) as? PKVRController {
            if let vrModeEnabled = vrSettings.vrModeEnabled {
                vrController.setVRModeEnabled(vrModeEnabled)
            }
        }
    }
    
    func updatePlayerSettings() {
        updateNetworkSetting()
        updateLowLatencySettings()
        updateTrackSelectionSettings()
    }
    
    func updateMediaSettings() {
        updateVRSettings()
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
        observeAdEvents()
        
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
        kalturaPlayer.removeObserver(self, events: PlayKit.AdEvent.allEventTypes)
    }
    
    func updatePlugins(_ plugins: Plugins) {
        guard let pluginConfig = RNKPKnownPlugins.configs(plugins) else { return }
        
        for (name, config) in pluginConfig.config {
            kalturaPlayer?.updatePluginConfig(pluginName: name, config: config)
        }
    }
    
    func updateViewContentMode(_ aspectRatioResizeMode: String?) {
        guard let resizeMode = aspectRatioResizeMode else { return }
        
        switch resizeMode.lowercased() {
        case "fit":
            kalturaPlayer?.view?.contentMode = .scaleAspectFit
        case "fixedWidth", "fixedHeight":
            // Not supported in iOS
            break
        case "fill":
            kalturaPlayer?.view?.contentMode = .scaleToFill
        case "zoom":
            kalturaPlayer?.view?.contentMode = .scaleAspectFill
        default:
            break
        }
    }
}

extension KalturaPlayerRN {
    
    private func observePlayerEvents() {
        guard let kalturaPlayer = self.kalturaPlayer else { return }
        
        kalturaPlayer.addObserver(self, events: PlayKit.PlayerEvent.allEventTypes) { event in
            switch event {
            case is PlayerEvent.CanPlay:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.canPlay.rawValue, body: [])
                
            case is PlayerEvent.Playing:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playing.rawValue, body: [])
                
            case is PlayerEvent.Play:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.play.rawValue, body: [])
                
            case is PlayerEvent.Pause:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.pause.rawValue, body: [])
                
            case is PlayerEvent.Ended:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.ended.rawValue, body: [])
                
            case is PlayerEvent.Stopped:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.stopped.rawValue, body: [])
                
            case is PlayerEvent.DurationChanged:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.durationChanged.rawValue, body: ["duration": event.duration])
                
            case is PlayerEvent.PlayheadUpdate:
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
                
            case is PlayerEvent.LoadedTimeRanges:
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
                
            case is PlayerEvent.StateChanged:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.stateChanged.rawValue, body: ["newState": event.newState.description])
                
            case is PlayerEvent.TracksAvailable:
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
                
            case is PlayerEvent.LoadedMetadata:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.loadedMetadata.rawValue, body: [])
                
            case is PlayerEvent.Replay:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.replay.rawValue, body: [])
                
            case is PlayerEvent.VideoTrackChanged:
                // iOS doesn't send the track, we only have the bitrate.
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.videoTrackChanged.rawValue, body: [
                    "ios": [
                        "bitrate": event.bitrate
                    ]
                ])
                
            case is PlayerEvent.AudioTrackChanged:
                guard let audioTrack = event.selectedTrack else { return }
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.audioTrackChanged.rawValue, body: [
                    "ios": [
                        "id": audioTrack.id,
                        "label": audioTrack.title,
                        "language": audioTrack.language ?? "",
                        "isSelected": true
                    ]
                ])
                
            case is PlayerEvent.TextTrackChanged:
                guard let textTrack = event.selectedTrack else { return }
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.textTrackChanged.rawValue, body: [
                    "ios": [
                        "id": textTrack.id,
                        "label": textTrack.title,
                        "language": textTrack.language ?? "",
                        "isSelected": true
                    ]
                ])
                
            case is PlayerEvent.PlaybackInfo:
                guard let playbackInfo = event.playbackInfo else { return }
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playbackInfoUpdated.rawValue, body: [
                    "ios": [
                        "bitrate": playbackInfo.bitrate,
                        "indicatedBitrate": playbackInfo.indicatedBitrate,
                        "observedBitrate": playbackInfo.observedBitrate,
                        "averageVideoBitrate": playbackInfo.averageVideoBitrate,
                        "averageAudioBitrate": playbackInfo.averageAudioBitrate,
                        "uri": playbackInfo.uri ?? ""
                    ]
                ])
                
            case is PlayerEvent.Seeking:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.seeking.rawValue, body: [
                    "targetPosition": event.targetSeekPosition
                ])
                
            case is PlayerEvent.Seeked:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.seeked.rawValue, body: [])
            
            case is PlayerEvent.Error:
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
                // TODO: Need to verify
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.error.rawValue, body: [
                    "errorType": errorType,
                    "errorCode": errorCode,
                    "errorSeverity": "Fatal",
                    "errorMessage": errorMessage,
                    "errorCause": errorCause
                ])
                
            case is PlayerEvent.TimedMetadata:
                guard let metadata = event.timedMetadata else { return }
                // TODO: Need to verify.
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.metadataAvailable.rawValue, body: [metadata])
                
            case is PlayerEvent.SourceSelected:
                guard let mediaSource = event.mediaSource else { return }
                
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.sourceSelected.rawValue, body: [
                    "id": mediaSource.id,
                    "url": mediaSource.contentUrl?.absoluteString,
                    "mediaFormat": mediaSource.mediaFormat.description
                ])
                
            case is PlayerEvent.PlaybackRate:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNEvents.playbackRateChanged.rawValue, body: [
                    "rate": event.palybackRate
                ])
                
            default:
                // Event is not supported
                break
            }
        }
    }
    
    func observeAdEvents() {
        guard let kalturaPlayer = self.kalturaPlayer else { return }
        
        kalturaPlayer.addObserver(self, events: PlayKit.AdEvent.allEventTypes) { event in
            switch event {
            case is AdEvent.AdsRequested:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.adRequested.rawValue, body: [
                    "adTagUrl": event.adTagUrl
                ])
                
            case is AdEvent.AdStarted:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.started.rawValue, body: [])
                
            case is AdEvent.AdPaused:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.paused.rawValue, body: [])
                
            case is AdEvent.AdResumed:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.resumed.rawValue, body: [])
                
            case is AdEvent.AdComplete:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.completed.rawValue, body: [])
                
            case is AdEvent.AdFirstQuartile:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.firstQuartile.rawValue, body: [])
                
            case is AdEvent.AdMidpoint:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.midpoint.rawValue, body: [])
                
            case is AdEvent.AdThirdQuartile:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.thirdQuartile.rawValue, body: [])
                
            case is AdEvent.AdSkipped:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.skipped.rawValue, body: [])
                
            case is AdEvent.AdClicked:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.adClickedEvent.rawValue, body: [
    //                "clickThroughUrl": event.clickThroughUrl // clickThroughUrl is missing in PKEvent extention inside AdEvent file.
                ])
                
            case is AdEvent.AdTapped:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.tapped.rawValue, body: [])
                
            case is AdEvent.AdBreakReady:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.adBreakReady.rawValue, body: [])
                
            case is AdEvent.AdDidProgressToTime:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.adProgress.rawValue, body: [
                    "currentAdPosition": event.adMediaTime
                ])
                
            case is AdEvent.AdBreakStarted:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.adBreakStarted.rawValue, body: [])
                
            case is AdEvent.AdBreakEnded:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.adBreakEnded.rawValue, body: [])
                
            case is AdEvent.AdCuePointsUpdate:
                if let imaCuePoint = event.adCuePoints {
                    KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.cuepointsChanged.rawValue, body: [
                        "ima": [
                            "cuePoints": imaCuePoint.cuePoints,
                            "count": imaCuePoint.count,
                            "hasPreRoll": imaCuePoint.hasPreRoll,
                            "hasMidRoll": imaCuePoint.hasMidRoll,
                            "hasPostRoll": imaCuePoint.hasPostRoll
                        ]
                    ])
                }
                
                if let imadaiCuePoint = event.adDAICuePoints {
                    var cuePoints: [Any] = []
                    for cuePoint in imadaiCuePoint.cuePoints {
                        cuePoints.append([
                            "startTime": cuePoint.startTime,
                            "endTime": cuePoint.endTime,
                            "played": cuePoint.played
                        ])
                    }
                    
                    KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.cuepointsChanged.rawValue, body: [
                        "imadai": [
                            "cuePoints": cuePoints,
                            "hasPreRoll": imadaiCuePoint.hasPreRoll
                        ]
                    ])
                }
                
            case is AdEvent.AdLoaded:
                guard let adInfo = event.adInfo else { return }
                
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.loaded.rawValue, body: [
                    "adDescription": adInfo.adDescription,
                    "adDuration": adInfo.duration,
                    "adPlayHead": adInfo.adPlayHead,
                    "adTitle": adInfo.title,
                    "streamId": "", // iOS doesn't have this value.
                    "isAdSkippable": adInfo.isSkippable,
                    "skipTimeOffset": adInfo.skipTimeOffset,
                    "adContentType": adInfo.contentType,
                    "adId": adInfo.adId,
                    "adSystem": adInfo.adSystem,
                    "creativeId": adInfo.creativeId,
                    "creativeAdId": adInfo.creativeAdId ?? "",
                    "advertiserName": adInfo.advertiserName,
                    "dealId": adInfo.dealId ?? "",
                    "surveyUrl": adInfo.surveyUrl ?? "",
                    "traffickingParams": adInfo.traffickingParams ?? "",
                    "adWrapperCreativeIds": [], // iOS doesn't have this value.
                    "adWrapperIds": [], // iOS doesn't have this value.
                    "adWrapperSystems": [], // iOS doesn't have this value.
                    "adHeight": adInfo.height,
                    "adWidth": adInfo.width,
                    "mediaBitrate": adInfo.mediaBitrate,
                    "totalAdsInPod": adInfo.totalAds,
                    "adIndexInPod": adInfo.adIndexInPod, // adInfo.adPosition is the same as adInfo.adIndexInPod.
                    "podIndex": adInfo.podIndex,
                    "podCount": adInfo.podCount,
                    "isBumper": adInfo.isBumper,
                    "adPodTimeOffset": adInfo.adPodTimeOffset // adInfo.timeOffset is the same as adInfo.adPodTimeOffset
                ])
                
            case is AdEvent.AdDidRequestContentPause:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.contentPauseRequested.rawValue, body: [])
                
            case is AdEvent.AdDidRequestContentResume:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.contentResumeRequested.rawValue, body: [])
                
            case is AdEvent.AllAdsCompleted:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.allAdsCompleted.rawValue, body: [])
                
            case is AdEvent.AdStartedBuffering:
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.adBufferStart.rawValue, body: [])
                
            case is AdEvent.Error:
                guard let error = event.error else { return }
                var errorSeverity = "Fatal"
                let errorType = error.userInfo["errorType"] as? Int
                // COMPANION_AD_LOADING_FAILED = 603
                if errorType == 603 {
                   errorSeverity = "Recoverable"
                }
                
                KalturaPlayerEvents.emitter.sendEvent(withName: KalturaPlayerRNAdEvents.error.rawValue, body: [
                    "errorType": errorType ?? -1,
                    "errorCode": error.code,
                    "errorSeverity": errorSeverity,
                    "errorMessage": error.localizedDescription,
                    "errorCause": error.localizedFailureReason ?? ""
                ])
                
            default:
                // Event is not supported
                break
            }
        }
    }
}
