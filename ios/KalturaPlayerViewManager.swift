

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
}
