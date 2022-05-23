#import "React/RCTViewManager.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(KalturaPlayerEvents, RCTEventEmitter)

@end

@interface RCT_EXTERN_MODULE(KalturaPlayerViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(style, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(playerType, NSString)
RCT_EXPORT_VIEW_PROPERTY(partnerId, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(playerInitOptions, NSString)
RCT_EXPORT_VIEW_PROPERTY(assetId, NSString)
RCT_EXPORT_VIEW_PROPERTY(mediaAsset, NSString)
RCT_EXPORT_VIEW_PROPERTY(load, BOOL)


// We are not using methods because Android

//RCT_EXTERN_METHOD(addListeners:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(removeListeners:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(partnerId:(nonnull int)partnerId)
//RCT_EXTERN_METHOD(playerInitOptions:(nonnull NSDictionary *)option)
//RCT_EXTERN_METHOD(updatePluginConfig:(nonnull NSDictionary *)config)
//RCT_EXTERN_METHOD(assetId:(nonnull int)assetId)
//RCT_EXTERN_METHOD(mediaAsset:(nonnull NSString *)mediaAsset)
//RCT_EXTERN_METHOD(load:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(play:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(pause:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(stop:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(replay:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(seek:(nonnull double)position)
//RCT_EXTERN_METHOD(changeTrack:(nonnull int)trackId)
//RCT_EXTERN_METHOD(playbackRate:(nonnull float)rate)
//RCT_EXTERN_METHOD(volume:(nonnull float)volume)
//RCT_EXTERN_METHOD(autoPlay:(nonnull BOOL)isAutoPlay)
//RCT_EXTERN_METHOD(ks:(nonnull NSString *)ks)
//RCT_EXTERN_METHOD(zIndex:(nonnull float)index)




//RCT_EXTERN_METHOD(setup:(nonnull int)partnerId options:(nonnull NSDictionary *)option)
//RCT_EXTERN_METHOD(load:(nonnull NSString *)assetId options:(nonnull NSDictionary *)option)
//RCT_EXTERN_METHOD(play)
//RCT_EXTERN_METHOD(pause)
//RCT_EXTERN_METHOD(replay)
//RCT_EXTERN_METHOD(stop)
//RCT_EXTERN_METHOD(setVolume:(nonnull float)volume)
//RCT_EXTERN_METHOD(seekTo:(nonnull float)position)
//RCT_EXTERN_METHOD(setPlayerVisibility:(nonnull BOOL)isVisible)
//RCT_EXTERN_METHOD(setAutoplay:(nonnull BOOL)value)
//RCT_EXTERN_METHOD(destroy)
//RCT_EXTERN_METHOD(observeAllEvents)

@end
