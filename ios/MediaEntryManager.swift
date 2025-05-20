import Foundation
import PlayKit
import KalturaPlayer
import SwiftyJSON

class MediaEntryManager{
    private static var pkMediaEntry: PKMediaEntry? = nil;
    
    public static func setMediaEntry(jsonMediaEntry: String){

        guard let data = jsonMediaEntry.data(using: .utf8) else { return }
        do {
            let jsonObject = try JSONSerialization.jsonObject(with: data, options: [])
            let json = JSON(jsonObject)
            
            pkMediaEntry = PKMediaEntry(json: json)
            
            if let entryMetadata = json["metadata"].object as? [String: String] {
                pkMediaEntry?.metadata = entryMetadata
            }
            
        } catch {
            print("ttt Failed to parse media entry JSON: \(error)")
        }
    }
    
    public static func play(player: KalturaOTTPlayer, startPosition: Double){
        if let mediaEntry = pkMediaEntry {
            let mediaOptions = OTTMediaOptions()
            mediaOptions.startTime = startPosition as TimeInterval
            player.setMedia(mediaEntry, options: mediaOptions)
        }
    }
}
