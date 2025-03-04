import UIKit

struct SubtitleStyleSettings {
    enum SubtitleTextSizeFraction: Int {
        case subtitleFraction100 = 4
        case subtitleFraction150 = 7
        case subtitleFraction200 = 10
    }

    var name: String
    var textSizeFraction: SubtitleTextSizeFraction
    var backgroundColor: UIColor
    var textColor: UIColor

    init(name: String, textSizeFraction: SubtitleTextSizeFraction = .subtitleFraction100, backgroundColor: UIColor = .clear, textColor: UIColor = .white) {
        self.name = name
        self.textSizeFraction = textSizeFraction
        self.backgroundColor = backgroundColor
        self.textColor = textColor
    }

    var textSizeFractionValue: Int {
        return textSizeFraction.rawValue
    }

    func withTextSizeFraction(_ fraction: SubtitleTextSizeFraction) -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: name, textSizeFraction: fraction, backgroundColor: backgroundColor, textColor: textColor)
    }

    func withBackgroundColor(_ color: UIColor) -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: name, textSizeFraction: textSizeFraction, backgroundColor: color, textColor: textColor)
    }

    func withTextColor(_ color: UIColor) -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: name, textSizeFraction: textSizeFraction, backgroundColor: backgroundColor, textColor: color)
    }
}

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
            .withTextSizeFraction(.subtitleFraction100)
            .withBackgroundColor(.clear)
    }

    private static func setBlackBackgroundWhiteTextSubtitleStyleSettings() -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: "BlackBackgroundWhiteText")
            .withTextSizeFraction(.subtitleFraction150)
            .withBackgroundColor(.black)
            .withTextColor(.white)
    }

    private static func setYellowTextSubtitleStyleSettings() -> SubtitleStyleSettings {
        return SubtitleStyleSettings(name: "YellowText")
            .withTextSizeFraction(.subtitleFraction200)
            .withBackgroundColor(.clear)
            .withTextColor(.yellow)
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
