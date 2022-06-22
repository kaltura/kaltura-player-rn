//
//  KalturaPlayerRNView.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/05/2022.
//

import KalturaPlayer
import PlayKitProviders
import PlayKit
import Foundation

class KalturaPlayerRNView : UIView {
    
    enum PlayerType: String {
        case basic
        case ovp
        case ott
    }
    
    var kalturaPlayer: KalturaPlayer!
//    var initOptions: RNKPInitOptions?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
    }
    
    // MARK: - Props
    @objc var style: NSDictionary?
    @objc var playerType: String = "" {
        didSet{
            print(playerType)
        }
    }
//    @objc var partnerId: NSNumber = 0
//    @objc var playerInitOptions: String = "" {
//        didSet {
//            if playerInitOptions.isEmpty {
//                PKLog.debug("playerInitOptions was set but is empty.")
//                return
//            }
//            guard let playerOptions = playerInitOptions.toDictionary() else { return }
//            initOptions = RNKPInitOptions.parse(initOptions: playerOptions)
//            if initOptions == nil { return }
//
//            switch PlayerType(rawValue: playerType) {
//            case .basic:
//                break
//            case .ovp:
//                break
//            case .ott:
//                setupOTT(partnerId: Int64(truncating: partnerId))
//            case .none:
//                PKLog.debug("playerType was not set.")
//            }
//        }
//    }
//
//    @objc var assetId: String = ""
//    @objc var mediaAsset: String = ""
//    @objc var load: Bool = false {
//        didSet {
//            if mediaAsset.isEmpty {
//                PKLog.debug("mediaAsset was set but is empty.")
//                return
//            }
//            guard let mediaOptions = mediaAsset.toDictionary() else { return }
//            if load {
//                switch PlayerType(rawValue: playerType) {
//                case .basic:
//                    break
//                case .ovp:
//                    break
//                case .ott:
//                    loadOTT(assetId: assetId, options: mediaOptions)
//                case .none:
//                    PKLog.debug("playerType was not set.")
//                }
//
//            }
//        }
//    }
    
    // MARK: -
    

    

    
//    // TODO: Fix all the '!'
//    func setupOTT(partnerId: Int64) {
//        guard let options = initOptions else { return }
//        guard let serverUrl = options.serverUrl else { return }
//
//        KalturaOTTPlayer.setup(partnerId: partnerId, serverURL: serverUrl)
//
//        let playerOptions = PlayerOptions()
//        if let preload = options.preload {
//            playerOptions.preload = preload
//        }
//        if let autoplay = options.autoplay {
//            playerOptions.autoPlay = autoplay
//        }
////        playerOptions.ks = options["ks"] as? String
//        kalturaPlayer = KalturaOTTPlayer(options: playerOptions)
//
//        let playerView = KalturaPlayerView()
//        playerView.contentMode = .scaleAspectFit
//        playerView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
//        self.addSubview(playerView)
//        print(self.frame)
//        print(playerView.frame)
//        kalturaPlayer.view = playerView
//    }
//
//    func loadOTT(assetId: String, options: [String: Any]) {
//        let mediaOptions = OTTMediaOptions()
//        mediaOptions.assetId = assetId
//        mediaOptions.ks = options["ks"] as? String
//        mediaOptions.assetType = getAssetType(str: options["assetType"] as! String)
//        mediaOptions.playbackContextType = getPlaybackContextType(str: options["playbackContextType"] as! String)
//        //mediaOptions.adapterData = options["adapterData"] as? [String : String]
//
//        if ((options["assetReferenceType"]) != nil) {
//            mediaOptions.assetReferenceType = getAssetReferenceType(str: options["assetReferenceType"] as! String)
//        }
//
//        if ((options["protocol"]) != nil) {
//            mediaOptions.networkProtocol = options["protocol"] as! String
//        }
//
//        if ((options["format"]) != nil) {
//            mediaOptions.formats = [options["format"] as! String]
//        }
//
//        if ((options["urlType"]) != nil) {
//            mediaOptions.urlType = options["urlType"] as? String
//        }
//
//        if ((options["fileId"]) != nil) {
//            mediaOptions.fileIds = [options["urlType"] as! String]
//        }
//
//        if ((options["streamerType"]) != nil) {
//            mediaOptions.streamerType = (options["streamerType"] as! String).lowercased()
//        }
//
//        if ((options["startPosition"]) != nil) {
//            mediaOptions.startTime = ((options["startPosition"] as? TimeInterval)!)
//        }
//
//        (kalturaPlayer as? KalturaOTTPlayer)?.loadMedia(options: mediaOptions) { error in
//            if (error != nil) {
//                print("Error in loadMedia: %@", error!)
//            }
//        }
//    }
}

// MARK: - Helpers

extension KalturaPlayerRNView {
    
    func getAssetType(str: String) -> AssetType {
        if (str.caseInsensitiveCompare("media") == ComparisonResult.orderedSame) { return AssetType.media }
        if (str.caseInsensitiveCompare("recording") == ComparisonResult.orderedSame) { return AssetType.recording }
        if (str.caseInsensitiveCompare("epg") == ComparisonResult.orderedSame) { return AssetType.epg }
        
        return AssetType.unset
    }
    
    func getAssetReferenceType(str: String) -> AssetReferenceType {
        if (str.caseInsensitiveCompare("media") == ComparisonResult.orderedSame) { return AssetReferenceType.media }
        if (str.caseInsensitiveCompare("epgInternal") == ComparisonResult.orderedSame) { return AssetReferenceType.epgInternal }
        if (str.caseInsensitiveCompare("epgExternal") == ComparisonResult.orderedSame) { return AssetReferenceType.epgExternal }
        if (str.caseInsensitiveCompare("npvr") == ComparisonResult.orderedSame) { return AssetReferenceType.npvr }
        
        return AssetReferenceType.unset
    }
    
    func getPlaybackContextType(str: String) -> PlaybackContextType {
        if (str.caseInsensitiveCompare("playback") == ComparisonResult.orderedSame) { return PlaybackContextType.playback }
        if (str.caseInsensitiveCompare("catchup") == ComparisonResult.orderedSame) { return PlaybackContextType.catchup }
        if (str.caseInsensitiveCompare("trailer") == ComparisonResult.orderedSame) { return PlaybackContextType.trailer }
        if (str.caseInsensitiveCompare("startOver") == ComparisonResult.orderedSame) { return PlaybackContextType.startOver }
        
        return PlaybackContextType.unset
    }
}
