import UIKit
import PlayKit
import KalturaPlayer

class SubtitleStylingManager {
    static func applySubtitleStyle(kalturaPlayer: KalturaOTTPlayer, userSubtitleStyleSettings: String) {
        let subtitleStyle = SubtitleStyling(userSubtitleStyleSettings: userSubtitleStyleSettings).getSubtitleStyleSettings()
        let playerSubtitleSettings = kalturaPlayer.settings.textTrackStyling
        playerSubtitleSettings.setBackgroundColor(UIColor.black.withAlphaComponent(0.5))
        playerSubtitleSettings.setTextColor(subtitleStyle.textColor)
        playerSubtitleSettings.setTextSize(percentageOfVideoHeight: subtitleStyle.textSizeFractionValue)
        kalturaPlayer.updateTextTrackStyling()
        print("applySubtitleStyle setSubtitleStyle: \(subtitleStyle.backgroundColor)")
    }
}
