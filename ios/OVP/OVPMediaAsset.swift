//
//  OVPMediaAsset.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 08/07/2022.
//

import Foundation
import KalturaPlayer

struct OVPMediaAsset: Codable {
    var ks: String?
    var startPosition: Double?
    
//    TODO: Missing the following, need to add
//    var referenceId: String?
//    var uiconfId: NSNumber?
    
    func getMediaOptions() -> OVPMediaOptions {
        let mediaOptions = OVPMediaOptions()
        
        if let ks = ks {
            mediaOptions.ks =  ks
        }
        if let startPosition = startPosition {
            mediaOptions.startTime = startPosition
        }
        
        return mediaOptions
    }
}
