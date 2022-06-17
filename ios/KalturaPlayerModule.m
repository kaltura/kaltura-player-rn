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

RCT_EXTERN_METHOD(setUpPlayer:(nonnull int)partnerId initOptions:(nonnull NSString *) callback:(RCTResponseSenderBlock)callback)


@end
