//
//  KalturaPlayerModule.m
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 15/06/2022.
//

#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(KalturaPlayerEvents, RCTEventEmitter)

@end


@interface RCT_EXTERN_MODULE(KalturaPlayerModule, NSObject)

RCT_EXTERN_METHOD(setUpPlayer:(nonnull NSString *)type
                  partnerId:(nonnull int)partnerId
                  initOptions:(NSString *)initOptions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(load:(nonnull NSString *)assetId
                  mediaAsset:(NSString *)mediaAsset
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(addKalturaPlayerListeners)
RCT_EXTERN_METHOD(removeKalturaPlayerListeners)

RCT_EXTERN_METHOD(onApplicationPaused)
RCT_EXTERN_METHOD(onApplicationResumed)

RCT_EXTERN_METHOD(updatePluginConfigs:(NSString *)configs)

RCT_EXTERN_METHOD(play)
RCT_EXTERN_METHOD(pause)
RCT_EXTERN_METHOD(replay)
RCT_EXTERN_METHOD(seekTo:(nonnull double)position)
RCT_EXTERN_METHOD(seekToLiveDefaultPosition)
RCT_EXTERN_METHOD(stop)
RCT_EXTERN_METHOD(destroy)
RCT_EXTERN_METHOD(changeTrack:(NSString *)trackId)
RCT_EXTERN_METHOD(changePlaybackRate:(nonnull float)playbackRate)
RCT_EXTERN_METHOD(setVolume:(nonnull float)volume)

RCT_EXTERN_METHOD(updateResizeMode:(NSString *)mode)
RCT_EXTERN_METHOD(updateAbrSettings:(NSString *)abrSettings)
RCT_EXTERN_METHOD(resetAbrSettings)
RCT_EXTERN_METHOD(updateLowLatencyConfig:(NSString *)lowLatencyConfig)
RCT_EXTERN_METHOD(resetLowLatencyConfig)

RCT_EXTERN_METHOD(getCurrentPosition:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(isPlaying:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(isLive:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

@end
