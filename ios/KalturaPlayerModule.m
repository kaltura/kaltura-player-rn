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
                  initOptions:(nonnull NSString *)initOptions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(load:(nonnull NSString *)assetId
                  mediaAsset:(nonnull NSString *)mediaAsset
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(addKalturaPlayerListeners)
RCT_EXTERN_METHOD(removeKalturaPlayerListeners)

RCT_EXTERN_METHOD(onApplicationPaused)
RCT_EXTERN_METHOD(onApplicationResumed)

RCT_EXTERN_METHOD(play)
RCT_EXTERN_METHOD(pause)
RCT_EXTERN_METHOD(replay)
RCT_EXTERN_METHOD(seekTo:(nonnull double)position)
RCT_EXTERN_METHOD(seekToLiveDefaultPosition)
RCT_EXTERN_METHOD(stop)
RCT_EXTERN_METHOD(destroy)
RCT_EXTERN_METHOD(changeTrack:(nonnull NSString *)trackId)
RCT_EXTERN_METHOD(changePlaybackRate:(nonnull float)playbackRate)
RCT_EXTERN_METHOD(setVolume:(nonnull float)volume)

RCT_EXTERN_METHOD(getCurrentPosition:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(isPlaying:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

@end
