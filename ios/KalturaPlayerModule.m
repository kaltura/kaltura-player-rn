//
//  KalturaPlayerModule.m
//  kalturaPlayerRN
//
//  Created by Gilad Nadav on 17/10/2021.
//

#import "KalturaPlayerModule.h"
#import <Foundation/Foundation.h>
#import <React/RCTLog.h>
@implementation KalturaPlayerModule



RCT_EXPORT_METHOD(createKalturaPlayerEvent:(NSString *)name location:(NSString *)location)
{
  RCTLogInfo(@"createKalturaPlayerEvent to create an event %@ at %@", name, location);
}

// To export a module named KalturaPayerModule
RCT_EXPORT_MODULE(KalturaPlayerModule);

@end
