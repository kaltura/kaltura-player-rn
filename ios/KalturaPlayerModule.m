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

RCT_EXTERN_METHOD(onApplicationResumed)

@end
