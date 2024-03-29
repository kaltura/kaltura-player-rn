//
//  KalturaPlayerModule.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 15/06/2022.
//

import Foundation
import PlayKit

@objc(KalturaPlayerEvents)
class KalturaPlayerEvents: RCTEventEmitter {
    public static var emitter: RCTEventEmitter!
    var hasListener: Bool = false

    override init() {
        super.init()
        KalturaPlayerEvents.emitter = self
    }
    
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func startObserving() {
        hasListener = true
    }

    override func stopObserving() {
        hasListener = false
    }

    override func supportedEvents() -> [String] {
        let playerEvents: [String] = KalturaPlayerRNEvents.allCases.map { $0.rawValue }
        let adEvents: [String] = KalturaPlayerRNAdEvents.allCases.map { $0.rawValue }
        let analyticsEvents: [String] = KalturaPlayerRNAnalyticsEvents.allCases.map { $0.rawValue }
        // If We don't return all supported Events in the lib, and the app listens to it, it will crash!
        let unsupportedPlayerEvents: [String] = KalturaPlayerUnsupportedRNEvents.allCases.map { $0.rawValue }
        let unsupportedAdEvents: [String] = KalturaPlayerUnsupportedRNAdEvents.allCases.map { $0.rawValue }
        
        return playerEvents + adEvents + analyticsEvents + unsupportedPlayerEvents + unsupportedAdEvents
    }
}

@objc(KalturaPlayerModule)
class KalturaPlayerModule: NSObject, RCTBridgeModule {
    
    var bridge: RCTBridge!
    var playerType: PlayerType = .basic
    var kalturaPlayerRN: KalturaPlayerRN?
    
    static func moduleName() -> String! {
        return "KalturaPlayerModule"
    }
    
    // Special method in order to expose static data.
    // Will enable to see the object in the JS console.
    // You can return any kind of data, but keep in mind that this data is static, computed on build.
    // This means that if you change this data on runtime, you won’t get the updated values.
    @objc func constantsToExport() -> [AnyHashable : Any]! {
        return [:]
    }
    
    @objc static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    @objc func setUpPlayer(_ type: String, partnerId: Int = 0, initOptions: String?,
                           resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        
        self.playerType = getPlayerType(from: type)
        var defaultOptions = initOptions
        
        if playerType == .basic {
            if initOptions == nil {
                defaultOptions = "{}"
            } else {
                if let initOptions = initOptions, initOptions.isEmpty {
                    defaultOptions = "{}"
                }
            }
        }
        
        guard let options = defaultOptions, !options.isEmpty else {
            let message = "The initOptions can not be empty."
            let error = KalturaPlayerRNError.setupFailed(message: message).asNSError
            reject("ERROR_SETUPPLAYER", message, error)
            return
        }
        
        let data = Data(options.utf8)
        let initOptions: RNKPInitOptions
        do {
            initOptions = try JSONDecoder().decode(RNKPInitOptions.self, from: data)
        } catch let error as NSError {
            let message = "The initOptions could not be parsed. \(error.localizedDescription)"
            let error = KalturaPlayerRNError.setupFailed(message: message).asNSError
            reject("ERROR_SETUPPLAYER", message, error)
            return
        }
        
        //TODO: Remove print
        print(initOptions)
        
        DispatchQueue.main.async {
            switch self.playerType {
            case .basic:
                self.kalturaPlayerRN = BasicKalturaPlayerRN(withOptions: initOptions)
            case .ott:
                self.kalturaPlayerRN = OTTKalturaPlayerRN(withOptions: initOptions, partnerId: partnerId)
            case .ovp:
                self.kalturaPlayerRN = OVPKalturaPlayerRN(withOptions: initOptions, partnerId: partnerId)
            }
            
            // Connect the player view
            guard self.bridge.moduleIsInitialized(KalturaPlayerViewManager.self),
                    let playerViewManager = self.bridge.module(for: KalturaPlayerViewManager.self) as? KalturaPlayerViewManager else {
                let message = "The KalturaPlayerViewManager was not yet initialised."
                let error = KalturaPlayerRNError.setupFailed(message: message).asNSError
                reject("ERROR_SETUPPLAYER", message, error)
                return
            }
            
            self.kalturaPlayerRN?.connectView(playerViewManager.kalturaPlayerRNView.kalturaPlayerView)
            
            self.kalturaPlayerRN?.updateViewContentMode(initOptions.aspectRatioResizeMode)
            
            resolve("Sucess")
        }
    }
    
    @objc func load(_ assetId: String?, mediaAsset: String?,
                    resolver resolve: @escaping RCTPromiseResolveBlock,
                    rejecter reject: @escaping RCTPromiseRejectBlock) {
        
        guard let kalturaPlayerRN = kalturaPlayerRN else {
            let message = "The KalturaPlayerRN is nil."
            let error = KalturaPlayerRNError.loadMediaFailed(message: message).asNSError
            reject("ERROR_LOADMEDIA", message, error)
            return
        }
        
        guard let assetId = assetId, !assetId.isEmpty else {
            let message = "The assetId is empty."
            let error = KalturaPlayerRNError.loadMediaFailed(message: message).asNSError
            reject("ERROR_LOADMEDIA", message, error)
            return
        }
        
        let playbackURL = URL(string: assetId)
        let isValidURL = UIApplication.shared.canOpenURL(playbackURL!)
        
        // If FE is passing an assetId which is playback URL actually then try to play it using basic player way
        if isValidURL && playerType != .basic {
            DispatchQueue.main.async {
                kalturaPlayerRN.playerMediaUsingBasicPlayer(assetId: assetId, mediaAsset: mediaAsset ?? "{}") { error in
                    if let kpRNError = error {
                        let message = kpRNError.userInfo[KalturaPlayerRNError.errorMessageKey] as? String
                        let nsError = kpRNError.asNSError
                        reject("ERROR_LOADMEDIA", message, nsError)
                    } else {
                        resolve("Sucess")
                    }
                }
            }
            return
        }
        
        DispatchQueue.main.async {
            kalturaPlayerRN.load(assetId: assetId, mediaAsset: mediaAsset ?? "{}") { error in
                if let kpRNError = error {
                    let message = kpRNError.userInfo[KalturaPlayerRNError.errorMessageKey] as? String
                    let nsError = kpRNError.asNSError
                    reject("ERROR_LOADMEDIA", message, nsError)
                } else {
                    resolve("Sucess")
                }
            }
        }
    }
}

extension KalturaPlayerModule {
    
    enum PlayerType: String {
        case basic
        case ovp
        case ott
    }
    
    private func getPlayerType(from type: String) -> PlayerType {
        
        if let playerType = PlayerType(rawValue: type.lowercased()) {
            return playerType
        }
        
        return .basic
    }
}

extension KalturaPlayerModule {
    
    @objc func addKalturaPlayerListeners() {
        kalturaPlayerRN?.observeAllEvents()
    }
    
    @objc func removeKalturaPlayerListeners() {
        kalturaPlayerRN?.removeObservationForAllEvents()
    }
}

extension KalturaPlayerModule {
    
    @objc func onApplicationPaused() {
        // TODO: onApplicationPaused
    }
    
    @objc func onApplicationResumed() {
        // TODO: onApplicationResumed
    }
}

extension KalturaPlayerModule {
    
    @objc func updatePluginConfigs(_ configs: String?) {
        guard let pluginConfigs = configs, !pluginConfigs.isEmpty else { return }
        
        let data = Data(pluginConfigs.utf8)
        if let plugins = try? JSONDecoder().decode(Plugins.self, from: data) {
            kalturaPlayerRN?.updatePlugins(plugins)
        }
    }
}

extension KalturaPlayerModule {
    
    @objc func play() {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.play()
        }
    }
    
    @objc func pause() {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.pause()
        }
    }
    
    @objc func replay() {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.replay()
        }
    }
    
    @objc func seekTo(_ position: TimeInterval) {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.seek(to: position)
        }
    }
    
    @objc func seekToLiveDefaultPosition() {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.seekToLiveEdge()
        }
    }
    
    @objc func stop() {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.stop()
        }
    }
    
    @objc func destroy() {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.destroy()
        }
    }
    
    @objc func changeTrack(_ trackId: String?) {
        guard let trackId = trackId, !trackId.isEmpty else { return }
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.selectTrack(trackId: trackId)
        }
    }
    
    @objc func changePlaybackRate(_ playbackRate: Float) {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.rate = playbackRate
        }
    }
    
    @objc func setVolume(_ volume: Float) {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.kalturaPlayer?.volume = volume
        }
    }
}

extension KalturaPlayerModule {
    
    @objc func updateResizeMode(_ mode: String?) {
        DispatchQueue.main.async { [weak self] in
            self?.kalturaPlayerRN?.updateViewContentMode(mode)
        }
    }
    
    @objc func updateAbrSettings(_ abrSettings: String?) {
        guard let settings = abrSettings, !settings.isEmpty else { return }
        
        let data = Data(settings.utf8)
        let abrSettings = try? JSONDecoder().decode(ABRSettings.self, from: data)
        if let maxVideoBitrate = abrSettings?.maxVideoBitrate {
            kalturaPlayerRN?.kalturaPlayer?.settings.network.preferredPeakBitRate = maxVideoBitrate
        }
    }
    
    @objc func resetAbrSettings() {
        // The default value in PlayKit for preferredPeakBitRate is 0.
        kalturaPlayerRN?.kalturaPlayer?.settings.network.preferredPeakBitRate = 0
    }
    
    @objc func updateLowLatencyConfig(_ lowLatencyConfig: String?) {
        guard let config = lowLatencyConfig, !config.isEmpty else { return }
        
        let data = Data(config.utf8)
        let pkLowLatencyConfig = try? JSONDecoder().decode(PKLowLatencyConfig.self, from: data)
        if let targetOffsetMs = pkLowLatencyConfig?.targetOffsetMs {
            kalturaPlayerRN?.kalturaPlayer?.settings.lowLatency.targetOffsetMs = targetOffsetMs
        }
    }
    
    @objc func resetLowLatencyConfig() {
        // The default value in PlayKit for targetOffsetMs is 0.
        kalturaPlayerRN?.kalturaPlayer?.settings.lowLatency.targetOffsetMs = 0
    }
}

extension KalturaPlayerModule {
    
    @objc func getCurrentPosition(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async { [weak self] in
            if let currentTime = self?.kalturaPlayerRN?.kalturaPlayer?.currentTime {
                resolve(currentTime)
            } else {
                let error = KalturaPlayerRNError.retrieveCurrentPositionFailed.asNSError
                let message = error.localizedDescription
                reject("ERROR_GETCURRENTPOSITION", message, error)
            }
        }
    }
    
    @objc func isPlaying(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async { [weak self] in
            if let isPlaying = self?.kalturaPlayerRN?.kalturaPlayer?.isPlaying {
                resolve(isPlaying)
            } else {
                let error = KalturaPlayerRNError.retrieveIsPlayingFailed.asNSError
                let message = error.localizedDescription
                reject("ERROR_ISPLAYING", message, error)
            }
        }
    }
    
    @objc func isLive(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async { [weak self] in
            if let isLive = self?.kalturaPlayerRN?.kalturaPlayer?.isLive() {
                resolve(isLive)
            } else {
                let error = KalturaPlayerRNError.retrieveIsLiveFailed.asNSError
                let message = error.localizedDescription
                reject("ERROR_ISPLAYING", message, error)
            }
        }
    }
}

extension KalturaPlayerModule {
    @objc func setLogLevel(logLevel: String?) {
        guard let logLevel = logLevel, !logLevel.isEmpty else {
            return
        }
        
        // TODO:
        
        
    }
}
