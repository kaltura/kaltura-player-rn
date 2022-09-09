//
//  OTTKalturaPlayerRN.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/06/2022.
//

import KalturaPlayer
import PlayKit

class OTTKalturaPlayerRN: KalturaPlayerRN {
    
    override var kalturaPlayer: KalturaPlayer? {
        return kalturaOTTPlayer
    }
    
    private var kalturaOTTPlayer: KalturaOTTPlayer! // Created upon init
    private let defaultServerURL = "https://rest-us.ott.kaltura.com/v4_5/api_v3/"
    
    init(withOptions initOptions: RNKPInitOptions, partnerId: Int) {
        super.init(withOptions: initOptions)
        
        var serverURL: String = initOptions.serverUrl ?? ""
        if serverURL.isEmpty == true {
            PKLog.debug("The serverUrl is missing or empty in the initOptions.")
            serverURL = defaultServerURL
        }
        
        KalturaOTTPlayer.setup(partnerId: Int64(partnerId), serverURL: serverURL, referrer: initOptions.referrer)
        kalturaOTTPlayer = KalturaOTTPlayer(options: playerOptions)
        updateSettings()
    }
    
    override func load(assetId: String, mediaAsset: String, callback: @escaping (_ error: KalturaPlayerRNError?) -> Void) {
        guard let ottMediaAsset = parseOTTMediaAsset(mediaAsset) else {
            let message = "Parsing the OTTMediaAsset failed."
            let error = KalturaPlayerRNError.loadMediaFailed(message: message)
            callback(error)
            return
        }
        
        let mediaOptions = ottMediaAsset.getMediaOptions()
        mediaOptions.assetId = assetId
        
        kalturaOTTPlayer.loadMedia(options: mediaOptions) { [weak self] error in
            if let error = error {
                let message = error.localizedDescription
                let kpRNError = KalturaPlayerRNError.loadMediaFailed(message: message)
                PKLog.debug("Load media failed with error: \(message)")
                callback(kpRNError)
            } else {
                if self?.initOptions.autoplay == false && self?.initOptions.preload == false {
                    self?.kalturaOTTPlayer.prepare()
                }
                callback(nil)
            }
        }
    }
}

extension OTTKalturaPlayerRN {
    
    private func parseOTTMediaAsset(_ mediaAsset: String) -> OTTMediaAsset? {
        let data = Data(mediaAsset.utf8)
        let mediaAsset: OTTMediaAsset?
        do {
            mediaAsset = try JSONDecoder().decode(OTTMediaAsset.self, from: data)
        } catch let error as NSError {
            PKLog.debug("Couldn't parse OTT MediaAsset, error: \(error)")
            return nil
        }
        return mediaAsset
    }
}
