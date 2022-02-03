#include <jni.h>
#include "PlayerJSIHostObject.h"

using namespace facebook;
using namespace kplayer_jsi;

JavaVM *cachedJVM;
jsi::Runtime *cachedRuntime = nullptr;
std::shared_ptr<PlayerJSIHostObject> hostObject = nullptr;

std::string getPropertyAsStringOrEmptyFromObject(jsi::Object& object, const std::string& propertyName, jsi::Runtime& runtime) {
    jsi::Value value = object.getProperty(runtime, propertyName.c_str());
    return value.isString() ? value.asString(runtime).utf8(runtime) : "";
}

void install(jsi::Runtime& jsiRuntime) {
    auto kPlayerCreateNewInstance = jsi::Function::createFromHostFunction(jsiRuntime,
                                                                       jsi::PropNameID::forAscii(jsiRuntime, "KPlayerCreateNewInstance"),
                                                                       1,
                                                                       [](jsi::Runtime& runtime,
                                                                          const jsi::Value& thisValue,
                                                                          const jsi::Value* arguments,
                                                                          size_t count) -> jsi::Value {
                                                                           hostObject = std::make_shared<PlayerJSIHostObject>();
                                                                           return jsi::Object::createFromHostObject(runtime, hostObject);
                                                                       });

    jsiRuntime.global().setProperty(jsiRuntime, "KPlayerCreateNewInstance", std::move(kPlayerCreateNewInstance));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_kaltura_react_1native_1kplayer_PlayerModule_nativeInstall(JNIEnv *env,
                                                                   jclass clazz,
                                                                   jlong jsiPtr) {
    auto runtime = reinterpret_cast<jsi::Runtime *>(jsiPtr);
    if (runtime) {
        cachedRuntime = runtime;
        install(*runtime);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_kaltura_react_1native_1kplayer_PKPlayerWrapper_nativeSendEvent(JNIEnv *pEnv,
                                                                        jclass clazz,
                                                                        jstring name, jstring payload) {
    if (hostObject == nullptr || cachedRuntime == nullptr) return;
    auto jname = pEnv->GetStringUTFChars(name, nullptr);
    auto jpayload = pEnv->GetStringUTFChars(payload, nullptr);
    auto std_name = std::string(jname);
    auto std_payload = std::string(jpayload);
    pEnv->ReleaseStringUTFChars(name, jname);
    pEnv->ReleaseStringUTFChars(payload, jpayload);
    hostObject->SendEvent(cachedRuntime, std_name, std_payload);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    cachedJVM = jvm;
    return JNI_VERSION_1_6;
}