#include "PlayerJSIHostObject.h"
#include <android/log.h>

namespace kplayer_jsi {

    #define LOG_TAG "PlayerJSIHostObject"

    static JNIEnv *GetEnv()
    {
        static JNIEnv *pEnv;
        cachedJVM->GetEnv(reinterpret_cast<void **>(&pEnv), JNI_VERSION_1_6);
        return pEnv;
    }

    PlayerJSIHostObject::PlayerJSIHostObject() {
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG,
                            "Creating Player instance...");

        loadIDs();
    }

    void PlayerJSIHostObject::loadIDs() {
        jclass tmpBridgeClass = GetEnv()->FindClass("com/kaltura/react_native_kplayer/PKPlayerWrapper");
        playerWrapperBridgeClass = reinterpret_cast<jclass>(GetEnv()->NewGlobalRef(tmpBridgeClass));

        if (!playerWrapperBridgeClass) {
            return;
        }

        setupMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setup", "(ILjava/lang/String;)V");
        loadMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "load", "(Ljava/lang/String;Ljava/lang/String;)V");
        prepareMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "prepare", "()V");
        playMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "play", "()V");
        pauseMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "pause", "()V");
        replayMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "replay", "()V");
        stopMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "stop", "()V");
        destroyMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "destroy", "()V");
        seekToMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "seekTo", "(D)V");
        changeTrackMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "changeTrack", "(Ljava/lang/String;)V");
        changePlaybackRateMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "changePlaybackRate", "(D)V");
        setAutoplayMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setAutoplay", "(Z)V");
        setKSMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setKS", "(Ljava/lang/String;)V");
        setZIndexMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setZIndex", "(D)V");
        setFrameMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setFrame", "(IIII)V");
        setVolumeMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setVolume", "(D)V");
        setLogLevelMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setLogLevel", "(Ljava/lang/String;)V");
        setPlayerVisibilityMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "setPlayerVisibility", "(Z)V");
        requestThumbnailInfoMethodID = GetEnv()->GetStaticMethodID(playerWrapperBridgeClass, "requestThumbnailInfo", "(D)V");
    }

    std::vector<jsi::PropNameID> PlayerJSIHostObject::getPropertyNames(jsi::Runtime &rt) {
        std::vector<jsi::PropNameID> result;
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setup")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("load")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("prepare")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("play")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("pause")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("replay")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("destroy")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("stop")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("seekTo")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("changeTrack")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("changePlaybackRate")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setAutoplay")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setKS")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setZIndex")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setFrame")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setVolume")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setLogLevel")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("setPlayerVisibility")));
        result.push_back(jsi::PropNameID::forUtf8(rt, std::string("requestThumbnailInfo")));
        return result;
    }

    jsi::Value PlayerJSIHostObject::get(jsi::Runtime &runtime, const jsi::PropNameID &propNameId) {
        auto propName = propNameId.utf8(runtime);
        auto funcName = "KPlayer." + propName;

        if (propName == "setup") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         2, // partnerId, options
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count > 3) logError(runtime, "KPlayer::setup: expects three arguments ('partnerId, options, eventCallback')!");
                                                             if (count < 2) logError(runtime, "KPlayer::setup: expects two arguments ('partnerId, options')!");
                                                             if (!arguments[0].isNumber()) logError(runtime, "KPlayer::setup: argument ('partnerId') has to be of type number!");
                                                             if (!arguments[1].isObject()) logError(runtime, "KPlayer::setup: argument ('options') has to be of type object!");

                                                             if (count == 3 && arguments[2].isObject())
                                                                eventCallback = std::make_shared<jsi::Function>(arguments[2].getObject(runtime).getFunction(runtime));

                                                             auto stringifyJson = runtime.global()
                                                                     .getPropertyAsObject(runtime, "JSON")
                                                                     .getPropertyAsFunction(runtime, "stringify");

                                                             auto env = GetEnv();
                                                             auto partnerId = int(arguments[0].getNumber());
                                                             auto options = arguments[1].getObject(runtime);
                                                             auto opts = stringifyJson.call(runtime, options);
                                                             auto jsonOptionsStr = opts.getString(runtime).utf8(runtime);
                                                             auto optionsStr = env->NewStringUTF(jsonOptionsStr.c_str());
                                                             env->CallStaticVoidMethod(playerWrapperBridgeClass, setupMethodID, partnerId, optionsStr);
                                                             env->DeleteLocalRef(optionsStr);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "load") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         2, // assetId, options
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 2) logError(runtime, "KPlayer::load: expects two arguments ('assetId, options')!");
                                                             if (!arguments[0].isString()) logError(runtime, "KPlayer::load: argument ('assetId') has to be of type string!");
                                                             if (!arguments[1].isObject()) logError(runtime, "KPlayer::load: argument ('options') has to be of type object!");

                                                             auto stringifyJson = runtime.global()
                                                                     .getPropertyAsObject(runtime, "JSON")
                                                                     .getPropertyAsFunction(runtime, "stringify");

                                                             auto env = GetEnv();
                                                             auto assetId = arguments[0].getString(runtime).utf8(runtime);
                                                             auto options = arguments[1].getObject(runtime);
                                                             auto opts = stringifyJson.call(runtime, options);
                                                             auto jsonOptionsStr = opts.getString(runtime).utf8(runtime);
                                                             auto optionsStr = env->NewStringUTF(jsonOptionsStr.c_str());
                                                             auto jAssetId = env->NewStringUTF(assetId.c_str());
                                                             env->CallStaticVoidMethod(playerWrapperBridgeClass, loadMethodID, jAssetId, optionsStr);
                                                             env->DeleteLocalRef(jAssetId);
                                                             env->DeleteLocalRef(optionsStr);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "prepare") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         0,
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {
                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, prepareMethodID);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "play") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         0,
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {
                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, playMethodID);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "pause") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         0,
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {
                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, pauseMethodID);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "replay") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         0,
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {
                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, replayMethodID);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "stop") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         0,
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {
                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, stopMethodID);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "destroy") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         0,
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {
                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, destroyMethodID);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "seekTo") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // position
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::seekTo: expects one argument ('position')!");
                                                             if (!arguments[0].isNumber()) logError(runtime, "KPlayer::seekTo: argument ('position') has to be of type number!");

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, seekToMethodID, arguments[0].getNumber());
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "changeTrack") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // uniqueId
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::changeTrack: expects one argument ('uniqueId')!");
                                                             if (!arguments[0].isString()) logError(runtime, "KPlayer::changeTrack: argument ('uniqueId') has to be of type string!");

                                                             auto env = GetEnv();
                                                             auto uniqueId = arguments[0].getString(
                                                                     runtime).utf8(runtime);
                                                             auto jUniqueId = env->NewStringUTF(uniqueId.c_str());
                                                             env->CallStaticVoidMethod(playerWrapperBridgeClass, changeTrackMethodID, jUniqueId);
                                                             env->DeleteLocalRef(jUniqueId);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "changePlaybackRate") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // playbackRate
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::changePlaybackRate: expects one argument ('playbackRate')!");
                                                             if (!arguments[0].isNumber()) logError(runtime, "KPlayer::changePlaybackRate: argument ('playbackRate') has to be of type number!");

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, changePlaybackRateMethodID, arguments[0].getNumber());
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "setAutoplay") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // autoplay
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::setAutoplay: expects one argument ('autoplay')!");
                                                             if (!arguments[0].isBool()) logError(runtime, "KPlayer::setAutoplay: argument ('autoplay') has to be of type boolean!");

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, setAutoplayMethodID, arguments[0].getBool());
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "setKS") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // ks
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::setKS: expects one argument ('ks')!");
                                                             if (!arguments[0].isString()) logError(runtime, "KPlayer::setKS: argument ('ks') has to be of type string!");

                                                             auto env = GetEnv();
                                                             auto ks = arguments[0].getString(
                                                                     runtime).utf8(runtime);
                                                             auto jks = env->NewStringUTF(ks.c_str());
                                                             env->CallStaticVoidMethod(playerWrapperBridgeClass, setKSMethodID, jks);
                                                             env->DeleteLocalRef(jks);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "setZIndex") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // index
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::setZIndex: expects one argument ('index')!");
                                                             if (!arguments[0].isNumber()) logError(runtime, "KPlayer::setZIndex: argument ('index') has to be of type number!");

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, setZIndexMethodID, arguments[0].getNumber());
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "setFrame") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         4, // playerViewWidth, playerViewHeight, playerViewPosX, playerViewPosY
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 4) logError(runtime, "KPlayer::setFrame: expects 4 arguments ('playerViewWidth, playerViewHeight, playerViewPosX, playerViewPosY')!");
                                                             if (!arguments[0].isNumber()) logError(runtime, "KPlayer::setFrame: argument ('playerViewWidth') has to be of type number!");
                                                             if (!arguments[1].isNumber()) logError(runtime, "KPlayer::setFrame: argument ('playerViewHeight') has to be of type number!");
                                                             if (!arguments[2].isNumber()) logError(runtime, "KPlayer::setFrame: argument ('playerViewPosX') has to be of type number!");
                                                             if (!arguments[3].isNumber()) logError(runtime, "KPlayer::setFrame: argument ('playerViewPosY') has to be of type number!");

                                                             auto env = GetEnv();
                                                             env->CallStaticVoidMethod(playerWrapperBridgeClass, setFrameMethodID, int(arguments[0].getNumber()),
                                                                                       int(arguments[1].getNumber()), int(arguments[2].getNumber()),
                                                                                       int(arguments[3].getNumber()));
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "setVolume") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // volume
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::setVolume: expects one argument ('volume')!");
                                                             if (!arguments[0].isNumber()) logError(runtime, "setVolume::setZIndex: argument ('volume') has to be of type number!");

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, setVolumeMethodID, arguments[0].getNumber());
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "setLogLevel") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // logLevel
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::setLogLevel: expects one argument ('logLevel')!");
                                                             if (!arguments[0].isString()) logError(runtime, "KPlayer::setLogLevel: argument ('logLevel') has to be of type string!");

                                                             auto env = GetEnv();
                                                             auto logLevel = arguments[0].getString(
                                                                     runtime).utf8(runtime);
                                                             auto jlogLevel = env->NewStringUTF(logLevel.c_str());
                                                             env->CallStaticVoidMethod(playerWrapperBridgeClass, setLogLevelMethodID, jlogLevel);
                                                             env->DeleteLocalRef(jlogLevel);
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "setPlayerVisibility") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // isVisible
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::setPlayerVisibility: expects one argument ('isVisible')!");
                                                             if (!arguments[0].isBool()) logError(runtime, "KPlayer::setPlayerVisibility: argument ('isVisible') has to be of type boolean!");

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, setPlayerVisibilityMethodID, arguments[0].getBool());
                                                             return jsi::Value::undefined();
                                                         });
        }

        if (propName == "requestThumbnailInfo") {
            return jsi::Function::createFromHostFunction(runtime,
                                                         jsi::PropNameID::forAscii(runtime, funcName),
                                                         1, // positionMs
                                                         [this](jsi::Runtime& runtime,
                                                                const jsi::Value& thisValue,
                                                                const jsi::Value* arguments,
                                                                size_t count) -> jsi::Value {

                                                             if (!playerWrapperBridgeClass) {
                                                                 return jsi::Value::undefined();
                                                             }

                                                             if (count != 1) logError(runtime, "KPlayer::requestThumbnailInfo: expects one argument ('positionMs')!");
                                                             if (!arguments[0].isNumber()) logError(runtime, "requestThumbnailInfo::setZIndex: argument ('positionMs') has to be of type number!");

                                                             GetEnv()->CallStaticVoidMethod(playerWrapperBridgeClass, requestThumbnailInfoMethodID, arguments[0].getNumber());
                                                             return jsi::Value::undefined();
                                                         });
        }

        return jsi::Value::undefined();
    }

    void PlayerJSIHostObject::SendEvent(jsi::Runtime *runtime, const std::string &name,
                                        const std::string &payload) {
        if (eventCallback == nullptr) return;

        eventCallback->call(*runtime, name, payload);
    }

    void PlayerJSIHostObject::logError(jsi::Runtime &rt, const char *message) {
        auto consoleError = rt
                .global()
                .getPropertyAsObject(rt, "console")
                .getPropertyAsFunction(rt, "error");
        consoleError.call(rt, jsi::String::createFromUtf8(rt, message));
        // throw jsi::JSError(rt, message);
    }

}
