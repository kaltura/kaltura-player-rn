import Foundation
import PlayKit
import KalturaPlayer
import SwiftyJSON


struct Source {
    var id: String
    var mimetype: String
    var url: String
    
    // Initializer to create a Source from an NSDictionary
    init?(dictionary: NSDictionary) {
        guard let id = dictionary["id"] as? String,
              let mimetype = dictionary["mimetype"] as? String,
              let url = dictionary["url"] as? String else {
            return nil
        }
        self.id = id
        self.mimetype = mimetype
        self.url = url
    }
}

class MediaEntryManager{
    private static var pkMediaEntry: PKMediaEntry? = nil;
    
    public static func setMediaEntry(jsonMediaEntry: String){
        print("ttt setMediaEntry 1 jsonMediaEntry:", jsonMediaEntry)

        guard let data = jsonMediaEntry.data(using: .utf8) else { return }
        do {
            let jsonObject = try JSONSerialization.jsonObject(with: data, options: [])
            let json = JSON(jsonObject)
            print("ttt setMediaEntry 2 json:", json)
            pkMediaEntry = PKMediaEntry(json: json)
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
