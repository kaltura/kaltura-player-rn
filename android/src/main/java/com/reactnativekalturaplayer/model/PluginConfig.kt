package com.reactnativekalturaplayer.model

data class PluginConfig(var pluginName: String, var pluginConfig: Any)

enum class RegisteredPlugins {
    ima,
    imadai,
    youbora,
    kava,
    ottAnalytics,
    broadpeak
}