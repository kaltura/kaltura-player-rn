import Foundation
import PlayKit
import KalturaPlayer


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
    
    public static func setMediaEntry(mediaEntry: NSDictionary){
        var id: String = ""
        var mediaType: MediaType = .vod
        var metadata: [String: String]?
        var sources: [PKMediaSource] = []
        
        if let entryId = mediaEntry["id"] {
            id = String(describing: entryId)
        }
        
        if let entryMetadata = mediaEntry["metadata"] as? [String: String] {
            metadata = entryMetadata
        }
        
        if let sourcesArray = mediaEntry["sources"] as? [NSDictionary] {
            for item in sourcesArray {
                if let source = Source(dictionary: item) {
                    let mediaSource = PKMediaSource(id: source.id)
                    mediaSource.contentUrl = URL(string: source.url)
                    mediaSource.mimeType = source.mimetype
                    sources.append(mediaSource)
                }
            }
        }
        
        pkMediaEntry = PKMediaEntry(id, sources: sources)
        pkMediaEntry?.metadata = metadata
        pkMediaEntry?.mediaType = mediaType
                
    }
    
    public static func play(player: KalturaOTTPlayer, startPosition: Double){
        if let mediaEntry = pkMediaEntry {
            let mediaOptions = OTTMediaOptions()
            mediaOptions.startTime = startPosition as TimeInterval
            player.setMedia(mediaEntry, options: mediaOptions)
        }
    }
}
