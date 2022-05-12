package com.reactnativekalturaplayer.events

class KalturaPlayerAdEvents {

    companion object {
        @JvmField
        val AD_REQUESTED = "adRequested"
        @JvmField
        val AD_FIRST_PLAY = "adFirstPlay"
        @JvmField
        val STARTED = "started"
        @JvmField
        val AD_DISPLAYED_AFTER_CONTENT_PAUSE = "adDisplayedAfterContentPause"
        @JvmField
        val PAUSED = "paused"
        @JvmField
        val RESUMED = "resumed"
        @JvmField
        val COMPLETED = "completed"
        @JvmField
        val FIRST_QUARTILE = "firstQuartile"
        @JvmField
        val MIDPOINT = "midpoint"
        @JvmField
        val THIRD_QUARTILE = "thirdQuartile"
        @JvmField
        val SKIPPED = "skipped"
        @JvmField
        val SKIPPABLE_STATE_CHANGED = "skippableStateChanged"
        @JvmField
        val CLICKED = "adClickedEvent"
        @JvmField
        val TAPPED = "tapped"
        @JvmField
        val ICON_FALLBACK_IMAGE_CLOSED = "iconFallbackImageClosed"
        @JvmField
        val ICON_TAPPED = "iconTapped"
        @JvmField
        val AD_BREAK_READY = "adBreakReady"
        @JvmField
        val AD_PROGRESS = "adProgress"
        @JvmField
        val AD_BREAK_STARTED = "adBreakStarted"
        @JvmField
        val AD_BREAK_ENDED = "adBreakEnded"
        @JvmField
        val AD_BREAK_FETCH_ERROR = "adBreakFetchError"
        @JvmField
        val AD_BREAK_IGNORED = "adBreakIgnored"
        @JvmField
        val CUEPOINTS_CHANGED = "cuepointsChanged"
        @JvmField
        val PLAY_HEAD_CHANGED = "playHeadChanged"
        @JvmField
        val LOADED = "loaded"
        @JvmField
        val CONTENT_PAUSE_REQUESTED = "contentPauseRequested"
        @JvmField
        val CONTENT_RESUME_REQUESTED = "contentResumeRequested"
        @JvmField
        val ALL_ADS_COMPLETED = "allAdsCompleted"
        @JvmField
        val AD_LOAD_TIMEOUT_TIMER_STARTED = "adLoadTimeoutTimerStarted"
        @JvmField
        val AD_BUFFER_START = "adBufferStart"
        @JvmField
        val AD_BUFFER_END = "adBufferEnd"
        @JvmField
        val AD_PLAYBACK_INFO_UPDATED = "adPlaybackInfoUpdated"
        @JvmField
        val ERROR = "error"
        @JvmField
        val DAI_SOURCE_SELECTED = "daiSourceSelected"
        @JvmField
        val AD_WATERFALLING = "adWaterFalling"
        @JvmField
        val AD_WATERFALLING_FAILED = "adWaterFallingFailed"
    }
}
