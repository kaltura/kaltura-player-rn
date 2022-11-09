package com.reactnativekalturaplayer.model

import androidx.annotation.Nullable
import com.google.gson.JsonObject
import com.kaltura.playkit.PKEvent
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

class Constants {
    companion object {
        const val BROADPEAK_EVENT_ERROR = "BROADPEAK_ERROR"
        const val YOUBORA_REPORT_SENT = "REPORT_SENT"
    }
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

enum class ReflectiveEvents(val eventClass: String) {
    youbora("com.kaltura.playkit.plugins.youbora.YouboraEvent\$Type"),
    broadPeak("com.kaltura.playkit.plugins.broadpeak.BroadpeakEvent\$Type")
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

fun getEventUsingReflection(): List<Enum<*>> {
    val events = mutableListOf<Enum<*>>()
    var eventClassName: String? = null
    try {
        for (className in ReflectiveEvents.values()) {
            eventClassName = className.eventClass
            val pluginClass = Class.forName(eventClassName)
            for (obj: Any in pluginClass.enumConstants) {
                events.add(obj as Enum<*>)
            }
        }
    } catch (classNotFoundException: ClassNotFoundException) {
        pkLog.v("getEventUsingReflection classNotFoundException $eventClassName not found ")
    } catch (noSuchFieldException: NoSuchFieldException) {
        pkLog.v("getEventUsingReflection noSuchFieldException $eventClassName not found ")
    } catch (illegalAccessException: IllegalAccessException) {
        pkLog.v("getEventUsingReflection illegalAccessException $eventClassName not found ")
    } catch (classCastException: ClassCastException) {
        pkLog.v("getEventUsingReflection classCastException $eventClassName not found ")
    } catch (runTimeException: RuntimeException) {
        pkLog.v("getEventUsingReflection runTimeException $eventClassName not found ")
    }
    return events
}

fun getEventPayloadMap(event: PKEvent): Map<String, String> {
    val eventPayloadMap = mutableMapOf<String, String>()
    try {
        val fieldsList: Array<Field>? = (event.javaClass).declaredFields
        fieldsList?.let {
            if (it.isNotEmpty()) {
                for (field in it) {
                    val fieldValue = (event.javaClass).getDeclaredField(field.name).get(event)
                    fieldValue?.let { value ->
                        eventPayloadMap[field.name] = value.toString()
                    }
                }
            }
        }
    } catch (classNotFoundException: ClassNotFoundException) {
        pkLog.v("getPluginFactory classNotFoundException $event not found ")
    } catch (noSuchFieldException: NoSuchFieldException) {
        pkLog.v("getPluginFactory noSuchFieldException $event not found ")
    } catch (illegalAccessException: IllegalAccessException) {
        pkLog.v("getPluginFactory illegalAccessException $event not found ")
    } catch (illegalArgumentException: IllegalArgumentException) {
        pkLog.v("getPluginFactory illegalArgumentException $event not found ")
    } catch (classCastException: ClassCastException) {
        pkLog.v("getPluginFactory classCastException $event not found ")
    } catch (runTimeException: RuntimeException) {
        pkLog.v("getPluginFactory runTimeException $event not found ")
    }
    return eventPayloadMap
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
