//
//  KalturaPlayerRN.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/06/2022.
//

import Foundation
import KalturaPlayer

protocol KalturaPlayerRNProtocol {
    func connectView(_ view: KalturaPlayerView)
    func load(assetId: String, mediaAsset: String)
}

/** Do not create an instance of this class.
    Create one of it's subclasses: BasicKalturaPlayerRN / OTTKalturaPlayerRN / OVPKalturaPlayerRN
 **/
class KalturaPlayerRN: NSObject, KalturaPlayerRNProtocol {
    
    private(set) var initOptions: RNKPInitOptions
    private(set) var playerOptions: PlayerOptions
    
    /** Do not create an instance of this class.
        Create one of it's subclasses: BasicKalturaPlayerRN / OTTKalturaPlayerRN / OVPKalturaPlayerRN
     **/
    init(withOptions initOptions: RNKPInitOptions) {
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
        
//TODO:        initOptions.pluginConfig // Need to add
        
        if let ks = initOptions.ks {
            playerOptions.ks = ks
        }
        
        return playerOptions
    }
    
    
    
    
    func connectView(_ view: KalturaPlayerView) {
        // Shouldn't get here! Should be implemented in sub class.
    }

    func load(assetId: String, mediaAsset: String) {
        // Shouldn't get here! Should be implemented in sub class.
    }
}
