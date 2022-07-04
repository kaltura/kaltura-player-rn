//
//  KalturaPlayerModule.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 15/06/2022.
//

import Foundation

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
        // If We don't return all supported Events in the lib it crashes!
        let currentlyUnsupportedEvents = ["volumeChanged", "loadedTimeRanges", "drmInitialized", "contentPauseRequested", "contentResumeRequested", "loaded", "adProgress", "cuepointsChanged", "adBufferStart", "adBufferEnd", "playbackRateChanged"]
        return playerEvents + currentlyUnsupportedEvents
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
                           resolver resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        
        guard let options = initOptions, !options.isEmpty else {
            let error = NSError(domain: "", code: 200, userInfo: nil)
            reject("ERROR_SETUPPLAYER", "The initOptions can not be empty.", error)
            return
        }
        
        let data = Data(options.utf8)
        let initOptions: RNKPInitOptions
        do {
            initOptions = try JSONDecoder().decode(RNKPInitOptions.self, from: data)
        } catch let error as NSError {
            reject("ERROR_SETUPPLAYER", "The initOptions could not be parsed.", error)
            return
        }
        
        //TODO: Remove print
        print(initOptions)
        
        self.playerType = getPlayerType(from: type)
        
        switch self.playerType {
        case .basic:
            self.kalturaPlayerRN = BasicKalturaPlayerRN(withOptions: initOptions)
        case .ott:
            self.kalturaPlayerRN = OTTKalturaPlayerRN(withOptions: initOptions)
        case .ovp:
            self.kalturaPlayerRN = OVPKalturaPlayerRN(withOptions: initOptions)
        }
        
        // Connect the player view
        guard self.bridge.moduleIsInitialized(KalturaPlayerViewManager.self),
                let playerViewManager = self.bridge.module(for: KalturaPlayerViewManager.self) as? KalturaPlayerViewManager else {
            let error = NSError(domain: "", code: 200, userInfo: nil)
            reject("ERROR_SETUPPLAYER", "The KalturaPlayerViewManager was not yet initialised.", error)
            return
        }
        
        DispatchQueue.main.async {
        self.kalturaPlayerRN?.connectView(playerViewManager.kalturaPlayerRNView.kalturaPlayerView)
        }
        
        resolve("Sucess")
    }
    
    @objc func load(_ assetId: String?, mediaAsset: String?) {
        guard let kalturaPlayerRN = kalturaPlayerRN else {
            return
        }
        
        guard let assetId = assetId, !assetId.isEmpty , let mediaAsset = mediaAsset, !mediaAsset.isEmpty else {
            return
        }
        
        DispatchQueue.main.async {
            kalturaPlayerRN.load(assetId: assetId, mediaAsset: mediaAsset)
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
    
    @objc func onApplicationResumed() {
        // TODO: onApplicationResumed
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
}
