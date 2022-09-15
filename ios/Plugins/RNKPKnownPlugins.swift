//
//  RNKPKnownPlugins.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 08/08/2022.
//

import PlayKit

class RNKPKnownPlugins {
    
    enum PKPlugins: CaseIterable {
        case IMA
        case IMADAI
        case Youbora
    
        private func className() -> String {
            switch self {
            case .IMA:
                return "PlayKit_IMA.IMAPlugin"
            case .IMADAI:
                return "PlayKit_IMA.IMADAIPlugin"
            case .Youbora:
                return "PlayKitYoubora.YouboraPlugin"
            }
        }
        
        private func configName() -> String {
            switch self {
            case .IMA:
                return "PlayKit_IMA.IMAConfig"
            case .IMADAI:
                return "PlayKit_IMA.IMADAIConfig"
            case .Youbora:
                return "PlayKit.AnalyticsConfig"
            }
        }
        
        
        func getClass() -> BasePlugin.Type? {
            return NSClassFromString(self.className()) as? BasePlugin.Type
        }
        
        func getConfigClass() -> NSObject.Type? {
            return NSClassFromString(self.configName()) as? NSObject.Type
        }
    }
    
    static func configs(_ plugins: Plugins) -> PluginConfig? {
        var configs: [String: Any] = [:]
        for plugin in PKPlugins.allCases {
            var loopablePlugin: Loopable? = nil
            switch plugin {
            case .IMA:
                if let ima = plugins.ima {
                    loopablePlugin = ima
                }
            case .IMADAI:
                if let imaDAI = plugins.imadai {
                    loopablePlugin = imaDAI
                }
            case .Youbora:
                if let youbora = plugins.youbora {
                    loopablePlugin = youbora
                }
            }
            
            if let pluginStruct = loopablePlugin,
               let allProperties = try? pluginStruct.allProperties(),
               let configClass = plugin.getConfigClass(),
               let pluginName = plugin.getClass()?.pluginName {
                let pluginConfig = configClass.init()
                for property in allProperties {
                    if property.value != nil {
                        pluginConfig.setValue(property.value, forKey: property.key)
                    }
                }
                
                configs[pluginName] = pluginConfig
            }
        }
        
        if !configs.isEmpty {
            return PluginConfig(config: configs)
        }
        
        return nil
    }
}
