//
//  KalturaPlayerRNAdEvents.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 23/09/2022.
//

enum KalturaPlayerRNAdEvents: String, CaseIterable {
    
    case adRequested // adsRequested in PlayKit AdEvent
    
    case started // adStarted
        
    case paused // adPaused
    
    case resumed // adResumed
    
    case completed // adComplete
    
    case firstQuartile // adFirstQuartile
    
    case midpoint // adMidpoint
    
    case thirdQuartile // adThirdQuartile
    
    case skipped // adSkipped
    
    case adClickedEvent // adClicked
    
    case tapped // adTapped
    
    case adBreakReady
    
    case adProgress // adDidProgressToTime
    
    case adBreakStarted // DAI
    
    case adBreakEnded // DAI
    
    case cuepointsChanged // adCuePointsUpdate
    
    case loaded // adLoaded
    
    case contentPauseRequested // adDidRequestContentPause
    
    case contentResumeRequested // adDidRequestContentResume
    
    case allAdsCompleted
    
    case adBufferStart // adStartedBuffering
    
    case error
    
    // MARK: Missing events from the iOS side
//    case streamLoaded // DAI
//    case streamStarted // DAI
//    case adPeriodStarted // DAI
//    case adPeriodEnded // DAI
//    case adLog
//    case webOpenerEvent
//    case adWebOpenerWillOpenExternalBrowser
//    case adWebOpenerWillOpenInAppBrowser
//    case adWebOpenerDidOpenInAppBrowser
//    case adWebOpenerWillCloseInAppBrowser
//    case adWebOpenerDidCloseInAppBrowser
//    case adPlaybackReady
//    case requestTimedOut
}

// MARK: - Ad Events that are in Android but not in iOS

enum KalturaPlayerUnsupportedRNAdEvents: String, CaseIterable {
   
    case adFirstPlay
    
    case adDisplayedAfterContentPause

    case skippableStateChanged
    
    case iconFallbackImageClosed
    
    case iconTapped
    
    case adBreakFetchError
    
    case adBreakIgnored
    
    case playHeadChanged
    
    case adLoadTimeoutTimerStarted // Is it requestTimedOut?
    
    case adBufferEnd // In iOS we have adPlaybackReady, it didn't finish buffering but it is likely to keep up, is it the same?
    
    case adPlaybackInfoUpdated
    
    case daiSourceSelected
    
    case adWaterFalling
    
    case adWaterFallingFailed
}
