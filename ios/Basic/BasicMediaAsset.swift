//
//  BasicMediaAsset.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 24/06/2022.
//

import Foundation
import PlayKit

struct BasicMediaAsset: Codable {
    var id: String?
    var name: String?
    var duration: Double?
    var mediaEntryType: String?
    var mediaFormat: String?
//    var drmData: [DRMParams]?
    var startPosition: Double?
}
