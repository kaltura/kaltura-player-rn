package com.reactnativekalturaplayer.model

import androidx.annotation.Nullable
import com.google.gson.JsonObject
import com.kaltura.playkit.PKLog
import com.kaltura.playkit.PKPlugin
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig
import com.kaltura.playkit.plugins.kava.KavaAnalyticsPlugin
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsPlugin
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

enum class PlayerPluginClass(val className: String) {
    ima("com.kaltura.playkit.plugins.ima.IMAPlugin"),
    imadai("com.kaltura.playkit.plugins.imadai.IMADAIPlugin"),
    youbora("com.kaltura.playkit.plugins.youbora.YouboraPlugin"),
    kava("com.kaltura.playkit.plugins.kava.KavaAnalyticsPlugin"),
    ottAnalytics("com.kaltura.playkit.plugins.ott.PhoenixAnalyticsPlugin"),
    broadpeak("com.kaltura.playkit.plugins.broadpeak.BroadpeakPlugin")
}

enum class PlayerPluginConfigs(val className: String) {
    ima("com.kaltura.playkit.plugins.ima.IMAConfig"),
    imadai("com.kaltura.playkit.plugins.imadai.IMADAIConfig"),
    youbora("com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig"),
    kava("com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig"),
    ottAnalytics("com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig"),
    broadpeak("com.kaltura.playkit.plugins.broadpeak.BroadpeakConfig")
}

val pkLog: PKLog = PKLog.get("PlayerPluginUtils")

@Nullable
fun getPluginFactory(pluginName: PlayerPluginClass) : PKPlugin.Factory? {
    when (pluginName) {
        PlayerPluginClass.kava -> {
            return KavaAnalyticsPlugin.factory
        }
        PlayerPluginClass.ottAnalytics -> {
            return PhoenixAnalyticsPlugin.factory
        }
        else -> {
            val factoryField = getPluginFactory(pluginName.className)
            return if (factoryField != null && PKPlugin.Factory::class.java.isAssignableFrom(factoryField.type)) {
                PKPlugin.Factory::class.java.cast(factoryField.get(null))
            } else {
                null
            }
        }
    }
}

@Nullable
fun getPluginConfig(pluginName: PlayerPluginClass): Class<*>? {
    val pluginConfig = PlayerPluginConfigs.valueOf(pluginName.name)
    if (pluginConfig == null) {
        return null
    }

    return when (pluginConfig) {
        PlayerPluginConfigs.kava -> {
            KavaAnalyticsConfig::class.java
        }
        PlayerPluginConfigs.ottAnalytics -> {
            PhoenixAnalyticsConfig::class.java
        }
        else -> {
            getPluginConfig(pluginConfig.className)
        }
    }
}

@Nullable
private fun getPluginFactory(className: String): Field? {
    try {
        val pluginClass = Class.forName(className)
        val factoryField = pluginClass.getField("factory")
        if (!Modifier.isStatic(factoryField.modifiers)) {
            return null
        }
        return factoryField
    } catch (classNotFoundException: ClassNotFoundException) {
        pkLog.v("getPluginFactory classNotFoundException $className not found ")
    } catch (noSuchFieldException: NoSuchFieldException) {
        pkLog.v("getPluginFactory noSuchFieldException $className not found ")
    } catch (illegalAccessException: IllegalAccessException) {
        pkLog.v("getPluginFactory illegalAccessException $className not found ")
    } catch (classCastException: ClassCastException) {
        pkLog.v("getPluginFactory classCastException $className not found ")
    } catch (runTimeException: RuntimeException) {
        pkLog.v("getPluginFactory runTimeException $className not found ")
    }
    return null
}

@Nullable
private fun getPluginConfig(className: String): Class<*>? {
    try {
        return Class.forName(className)
    } catch (classNotFoundException: ClassNotFoundException) {
        pkLog.v("getPluginConfig classNotFoundException $className not found ")
    } catch (noSuchFieldException: NoSuchFieldException) {
        pkLog.v("getPluginConfig noSuchFieldException $className not found ")
    } catch (illegalAccessException: IllegalAccessException) {
        pkLog.v("getPluginConfig illegalAccessException $className not found ")
    } catch (classCastException: ClassCastException) {
        pkLog.v("getPluginConfig classCastException $className not found ")
    } catch (runTimeException: RuntimeException) {
        pkLog.v("getPluginConfig runTimeException $className not found ")
    }
    return null
}