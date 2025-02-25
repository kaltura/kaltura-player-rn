import UIKit

class SubtitleStyling {
    private var subtitleStyleSettings: SubtitleStyleSettings

    init(userSubtitleStyleSettings: String) {
        self.subtitleStyleSettings = SubtitleStyling.updateSubtitleStyle(userSubtitleStyleSettings: userSubtitleStyleSettings)
    }

    func getSubtitleStyleSettings() -> SubtitleStyleSettings {
        return subtitleStyleSettings
    }

    static func updateSubtitleStyle(userSubtitleStyleSettings: String) -> SubtitleStyleSettings {
        let subtitle = convertSubtitleStyleStringToEnum(subtitleStyle: userSubtitleStyleSettings)
        return updateSubtitleStyle(subtitle: subtitle)
    }

    static func updateSubtitleStyle(subtitle: Subtitles) -> SubtitleStyleSettings {
        switch subtitle {
        case .default:
            return setDefaultSubtitleStyleSettings()
        case .blackBackgroundWhiteText:
            return setBlackBackgroundWhiteTextSubtitleStyleSettings()
        case .yellowText:
            return setYellowTextSubtitleStyleSettings()
        }
    }

    private static func setDefaultSubtitleStyleSettings() -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: "Default")
            .setTextSizeFraction(.subtitleFraction100)
            .setBackgroundColor(.clear)
    }

    private static func setBlackBackgroundWhiteTextSubtitleStyleSettings() -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: "BlackBackgroundWhiteText")
            .setTextSizeFraction(.subtitleFraction125)
            .setBackgroundColor(.black)
            .setTextColor(.white)
    }

    private static func setYellowTextSubtitleStyleSettings() -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: "YellowText")
            .setTextSizeFraction(.subtitleFraction150)
            .setBackgroundColor(.clear)
            .setTextColor(.yellow)
    }

    private static func convertSubtitleStyleStringToEnum(subtitleStyle: String) -> Subtitles {
        return Subtitles(rawValue: subtitleStyle) ?? .default
    }

    enum Subtitles: String {
        case `default` = "default"
        case blackBackgroundWhiteText = "blackBackgroundWhiteText"
        case yellowText = "yellowText"
    }
}

class SubtitleStyleSettings {
    enum SubtitleTextSizeFraction {
        case subtitleFraction100
        case subtitleFraction125
        case subtitleFraction150
    }

    private var name: String
    private var textSizeFraction: SubtitleTextSizeFraction
    private var backgroundColor: UIColor
    private var textColor: UIColor

    init(name: String) {
        self.name = name
        self.textSizeFraction = .subtitleFraction100
        self.backgroundColor = .clear
        self.textColor = .white
    }

    func setTextSizeFraction(_ fraction: SubtitleTextSizeFraction) -> SubtitleStyleSettings {
        self.textSizeFraction = fraction
        return self
    }

    func setBackgroundColor(_ color: UIColor) -> SubtitleStyleSettings {
        self.backgroundColor = color
        return self
    }

    func setTextColor(_ color: UIColor) -> SubtitleStyleSettings {
        self.textColor = color
        return self
    }
}