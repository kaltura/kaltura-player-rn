package com.reactnativekalturaplayer.events

class KalturaPlayerAdEvents {

    companion object {
        const val AD_REQUESTED = "adRequested"
        const val AD_FIRST_PLAY = "adFirstPlay"
        const val STARTED = "started"
        const val AD_DISPLAYED_AFTER_CONTENT_PAUSE = "adDisplayedAfterContentPause" // Not in USE in SDK
        const val PAUSED = "paused"
        const val RESUMED = "resumed"
        const val COMPLETED = "completed"
        const val FIRST_QUARTILE = "firstQuartile"
        const val MIDPOINT = "midpoint"
        const val THIRD_QUARTILE = "thirdQuartile"
        const val SKIPPED = "skipped"
        const val SKIPPABLE_STATE_CHANGED = "skippableStateChanged"
        const val CLICKED = "adClickedEvent"
        const val TAPPED = "tapped"
        const val ICON_FALLBACK_IMAGE_CLOSED = "iconFallbackImageClosed"
        const val ICON_TAPPED = "iconTapped"
        const val AD_BREAK_READY = "adBreakReady"
        const val AD_PROGRESS = "adProgress"
        const val AD_BREAK_STARTED = "adBreakStarted"
        const val AD_BREAK_ENDED = "adBreakEnded"
        const val AD_BREAK_FETCH_ERROR = "adBreakFetchError"
        const val AD_BREAK_IGNORED = "adBreakIgnored"
        const val CUEPOINTS_CHANGED = "cuepointsChanged"
        const val PLAY_HEAD_CHANGED = "playHeadChanged"
        const val LOADED = "loaded"
        const val CONTENT_PAUSE_REQUESTED = "contentPauseRequested"
        const val CONTENT_RESUME_REQUESTED = "contentResumeRequested"
        const val ALL_ADS_COMPLETED = "allAdsCompleted"
        const val AD_LOAD_TIMEOUT_TIMER_STARTED = "adLoadTimeoutTimerStarted" // Not in USE in SDK
        const val AD_BUFFER_START = "adBufferStart"
        const val AD_BUFFER_END = "adBufferEnd"
        const val AD_PLAYBACK_INFO_UPDATED = "adPlaybackInfoUpdated"
        const val ERROR = "error"
        const val DAI_SOURCE_SELECTED = "daiSourceSelected"
        const val AD_WATERFALLING = "adWaterFalling" // Not required until we has AdLayout feature in RN
        const val AD_WATERFALLING_FAILED = "adWaterFallingFailed" // Not required until we has AdLayout feature in RN
    }
}
