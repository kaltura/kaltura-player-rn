package com.reactnativekalturaplayer.model

import com.google.gson.JsonObject
import com.kaltura.playkit.PKPlugin
import com.kaltura.playkit.plugins.ima.IMAConfig
import com.kaltura.playkit.plugins.ima.IMAPlugin
import com.kaltura.playkit.plugins.imadai.IMADAIConfig
import com.kaltura.playkit.plugins.imadai.IMADAIPlugin
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig
import com.kaltura.playkit.plugins.kava.KavaAnalyticsPlugin
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsPlugin
import com.kaltura.playkit.plugins.youbora.YouboraPlugin
import com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class RegisteredPlugins {
    var ima: JsonObject? = null
    var imadai: JsonObject? = null
    var youbora: JsonObject? = null
    var kava: JsonObject? = null
    var ottAnalytics: JsonObject? = null
    var broadpeak: JsonObject? = null
}

data class UpdatePluginConfigJson(val pluginName: String?, val pluginConfig: Any?)

enum class PlayerPlugins {
    ima,
    imadai,
    youbora,
    kava,
    ottAnalytics,
    broadpeak
}

fun getPluginFactory(pluginName: PlayerPlugins) : PKPlugin.Factory? {
    when (pluginName) {
        PlayerPlugins.ima -> {
            return IMAPlugin.factory
        }
        PlayerPlugins.imadai -> {
            return IMADAIPlugin.factory
        }
        PlayerPlugins.youbora -> {
            return YouboraPlugin.factory
        }
        PlayerPlugins.kava -> {
            return KavaAnalyticsPlugin.factory
        }
        PlayerPlugins.ottAnalytics -> {
            return PhoenixAnalyticsPlugin.factory
        }
        PlayerPlugins.broadpeak -> {
            val factoryField = getBroadpeakFactory();
            return if (factoryField != null && PKPlugin.Factory::class.java.isAssignableFrom(factoryField.type)) {
                PKPlugin.Factory::class.java.cast(factoryField.get(null))
            } else {
                null
            }
        }
    }
}

fun getPluginConfig(pluginName: PlayerPlugins): Class<*>? {
    when (pluginName) {
        PlayerPlugins.ima -> {
            return IMAConfig::class.java
        }
        PlayerPlugins.imadai -> {
            return IMADAIConfig::class.java
        }
        PlayerPlugins.youbora -> {
            return YouboraConfig::class.java
        }
        PlayerPlugins.kava -> {
            return KavaAnalyticsConfig::class.java
        }
        PlayerPlugins.ottAnalytics -> {
            return PhoenixAnalyticsConfig::class.java
        }
        PlayerPlugins.broadpeak -> {
            return getBroadpeakConfig()
        }
    }
}

fun getBroadpeakFactory(): Field? {
    try {
        val broadpeakPlugin = Class.forName("com.kaltura.playkit.plugins.broadpeak.BroadpeakPlugin")
        val factoryField = broadpeakPlugin.getField("factory")
        if (!Modifier.isStatic(factoryField.modifiers)) {
            return null
        }
        return factoryField
    } catch (classNotFoundException: ClassNotFoundException) {

    }
    return null
}

fun getBroadpeakConfig(): Class<*>? {
    try {
        val broadpeakConfig = Class.forName("com.kaltura.playkit.plugins.broadpeak.BroadpeakConfig")
        return broadpeakConfig
    } catch (classNotFoundException: ClassNotFoundException) {

    }
    return null
}