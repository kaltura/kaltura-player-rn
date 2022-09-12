//
//  KalturaPlayerRNView.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/05/2022.
//

import Foundation
import KalturaPlayer

class KalturaPlayerRNView : UIView {
    
    let kalturaPlayerView: KalturaPlayerView = KalturaPlayerView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        kalturaPlayerView.frame = frame
        kalturaPlayerView.contentMode = .scaleAspectFit
        kalturaPlayerView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        self.addSubview(kalturaPlayerView)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
    }
    
    // MARK: - Props
    
    @objc var style: NSDictionary?
}
