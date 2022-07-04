



@objc(KalturaPlayerViewManager)
class KalturaPlayerViewManager: RCTViewManager {
    var kalturaPlayerRNView: KalturaPlayerRNView!

    override func view() -> (KalturaPlayerRNView) {
        kalturaPlayerRNView = KalturaPlayerRNView()
        return kalturaPlayerRNView
    }
    
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    override class func moduleName() -> String! {
        return "KalturaPlayerViewManager"
    }
    


//    @objc func setVolume(_ volume: Float) {
//        DispatchQueue.main.async {
//            // setVolume not found
//        }
//    }
//

//
//    @objc func setPlayerVisibility(_ isVisible: Bool) {
//        DispatchQueue.main.async {
//            self.kalturaPlayer!.view?.isHidden = !isVisible;
//        }
//    }
//
//    @objc func setAutoplay(_ value: Bool) {
//        DispatchQueue.main.async {
//            let options = PlayerOptions()
//            options.autoPlay = value
//            self.kalturaPlayer.updatePlayerOptions(options)
//        }
//    }
//

}


