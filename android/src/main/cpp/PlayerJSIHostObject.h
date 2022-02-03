#pragma once

#include <jsi/jsi.h>
#include <jni.h>

using namespace facebook;

extern JavaVM *cachedJVM;

namespace kplayer_jsi {

    class JSI_EXPORT PlayerJSIHostObject: public jsi::HostObject {
    private:
        jclass playerWrapperBridgeClass = nullptr;
        jmethodID setupMethodID = 0;
        jmethodID loadMethodID = 0;
        jmethodID prepareMethodID = 0;
        jmethodID playMethodID = 0;
        jmethodID pauseMethodID = 0;
        jmethodID replayMethodID = 0;
        jmethodID destroyMethodID = 0;
        jmethodID stopMethodID = 0;
        jmethodID seekToMethodID = 0;
        jmethodID changeTrackMethodID = 0;
        jmethodID changePlaybackRateMethodID = 0;
        jmethodID setAutoplayMethodID = 0;
        jmethodID setKSMethodID = 0;
        jmethodID setZIndexMethodID = 0;
        jmethodID setFrameMethodID = 0;
        jmethodID setVolumeMethodID = 0;
        jmethodID setLogLevelMethodID = 0;
        jmethodID setPlayerVisibilityMethodID = 0;
        jmethodID requestThumbnailInfoMethodID = 0;
        std::shared_ptr<jsi::Function> eventCallback = nullptr;
        void loadIDs();
        void logError(jsi::Runtime& rt, const char* message);
    public:
        PlayerJSIHostObject();
        void SendEvent(jsi::Runtime *runtime, const std::string& name, const std::string& payload);
        jsi::Value get(jsi::Runtime&, const jsi::PropNameID& name) override;
        std::vector<jsi::PropNameID> getPropertyNames(jsi::Runtime& rt) override;
    };

}