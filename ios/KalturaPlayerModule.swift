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
class KalturaPlayerModule: NSObject {
    
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
    
    func setUpPlayer(partnerId: Int = 0, initOptions: String?, callback: RCTResponseSenderBlock) {
        
    }
    
}
