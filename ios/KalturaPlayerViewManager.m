#import "React/RCTViewManager.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(KalturaPlayerEvents, RCTEventEmitter)

@end

@interface RCT_EXTERN_MODULE(KalturaPlayerViewManager, RCTViewManager)

RCT_EXTERN_METHOD(setup:(nonnull int)partnerId options:(nonnull NSDictionary *)option)
RCT_EXTERN_METHOD(load:(nonnull NSString *)assetId options:(nonnull NSDictionary *)option)
RCT_EXTERN_METHOD(play)
RCT_EXTERN_METHOD(pause)
RCT_EXTERN_METHOD(replay)
RCT_EXTERN_METHOD(stop)
RCT_EXTERN_METHOD(setVolume:(nonnull float)volume)
RCT_EXTERN_METHOD(seekTo:(nonnull double)position)
RCT_EXTERN_METHOD(setPlayerVisibility:(nonnull BOOL)isVisible)
RCT_EXTERN_METHOD(setAutoplay:(nonnull BOOL)value)
RCT_EXTERN_METHOD(changeTrack:(nonnull NSString *)trackId)
RCT_EXTERN_METHOD(prepare)
RCT_EXTERN_METHOD(destroy)

@end
