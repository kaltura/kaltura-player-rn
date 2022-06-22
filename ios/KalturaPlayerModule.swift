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

    override func supportedEvents() -> [String]! {
        return [
            "canPlay", "durationChanged", "stopped", "ended", "loadedMetadata", "play", "pause", "playing", "seeking", "seeked", "replay",
            "tracksAvailable", "textTrackChanged", "audioTrackChanged", "videoTrackChanged", "playbackInfo", "stateChanged",
            "timedMetadata", "sourceSelected", "loadedTimeRanges", "playheadUpdate", "error", "errorLog", "playbackStalled", "playbackRate", "drmInitialized"
        ]
    }
}

@objc(KalturaPlayerModule)
class KalturaPlayerModule: NSObject, RCTBridgeModule {
    
    var bridge: RCTBridge!
    var initOptions: RNKPInitOptions?
    var kalturaPlayerViewManager: KalturaPlayerViewManager?
    
    static func moduleName() -> String! {
        return "KalturaPlayerModule"
    }
    
    // Special method in order to expose static data.
    // Will enable to see the object in the JS console.
    // You can return any kind of data, but keep in mind that this data is static, computed on build.
    // This means that if you change this data on runtime, you won’t get the updated values.
    @objc
    func constantsToExport() -> [AnyHashable : Any]! {
        return [:]
    }
    
    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    @objc
    func setUpPlayer(_ partnerId: Int = 0, initOptions: String?, resolver resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {
        
        guard let options = initOptions, !options.isEmpty else {
            let error = NSError(domain: "", code: 200, userInfo: nil)
            reject("ERROR_SETUPPLAYER", "The initOptions can not be empty.", error)
            return
        }
        
        let data = Data(options.utf8)
        do {
            self.initOptions = try JSONDecoder().decode(RNKPInitOptions.self, from: data)
        } catch let error as NSError {
            reject("ERROR_SETUPPLAYER", "The initOptions could not be parsed.", error)
            return
        }
        
        print(self.initOptions)
        
        guard self.bridge.moduleIsInitialized(KalturaPlayerViewManager.self),
                let playerViewManager = self.bridge.module(for: KalturaPlayerViewManager.self) as? KalturaPlayerViewManager else {
            let error = NSError(domain: "", code: 200, userInfo: nil)
            reject("ERROR_SETUPPLAYER", "The KalturaPlayerViewManager was not yet initialised.", error)
            return
        }
        
        self.kalturaPlayerViewManager = playerViewManager

        let playerType = playerViewManager.kalturaPlayerRNView.playerType
        
        switch KalturaPlayerRNView.PlayerType(rawValue: playerType) {
        case .basic:
            break
        case .ott:
            break
        case .ovp:
            break
        case .none:
            let error = NSError(domain: "", code: 200, userInfo: nil)
            reject("ERROR_SETUPPLAYER", "The KalturaPlayerRNView playerType isn't a valid option.", error)
            return
        }
    }
    
    @objc
    func onApplicationResumed() {
        
    }
    
}
