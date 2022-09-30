import { ViewStyle } from 'react-native';
import React from 'react';
import { PlayerEvents } from './events/PlayerEvents';
import { AdEvents } from './events/AdEvents';
import { AnalyticsEvents } from './events/AnalyticsEvents';
import { PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES, WAKEMODE, SUBTITLE_STYLE, SUBTITLE_PREFERENCE, VIDEO_CODEC, AUDIO_CODEC, VR_INTERACTION_MODE } from './consts';
export { PlayerEvents, AdEvents, AnalyticsEvents, PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES, WAKEMODE, SUBTITLE_STYLE, SUBTITLE_PREFERENCE, VIDEO_CODEC, AUDIO_CODEC, VR_INTERACTION_MODE, };
interface KalturaPlayerProps {
    style: ViewStyle;
}
export declare class KalturaPlayer extends React.Component<KalturaPlayerProps> {
    nativeComponentRef: any;
    static propTypes: {
        style: object;
    };
    componentDidMount(): void;
    componentWillUnmount(): void;
    render(): JSX.Element;
}
export declare class KalturaPlayerAPI {
    /**
     * This method creates a Player instance internally (Basic, OVP/OTT Player)
     * With this, it take the PlayerInitOptions which are having essential Player settings values
     *
     * @param playerType The Player Type, Basic/OVP/OTT.
     * @param options PlayerInitOptions JSON String.
     * @param id PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value
     * should be always greater than 0 and should be valid otherwise, we will not be able to featch the details
     * for the mediaId or the entryId)
     */
    static setup: (playerType: PLAYER_TYPE, options: string, id?: number) => Promise<any>;
    /**
     * Load the media with the given
     *
     * assetId OR mediaId OR entryID for OVP/OTT Kaltura Player
     *
     * playbackURL for Basic Kaltura Player
     *
     * @param id Playback URL for Kaltura Basic Player OR
     * MediaId for Kaltura OTT Player OR
     * EntryId for Kaltura OVP Player
     * @param asset Media Asset JSON String
     */
    static loadMedia: (id: string, asset: string) => Promise<any>;
    /**
     * Adds the Native Player View to the Player if not attached
     * Ideally this API should be called after calling {@link removePlayerView}
     */
    static addPlayerView: () => void;
    /**
     * Removes the Native Player View from the Player if it is attached
     * Ideally this API should be called after calling {@link addPlayerView}
     */
    static removePlayerView: () => void;
    /**
     * Add the listners for the Kaltura Player
     */
    static addListeners: () => void;
    /**
     * Add the listners for the Kaltura Player
     */
    static removeListeners: () => void;
    /**
     * Should be called when the application is in background
     */
    static onApplicationPaused: () => void;
    /**
     * Should be called when the application comes back to
     * foreground
     */
    static onApplicationResumed: () => void;
    /**
     * Update Plugin Configs
     *
     * @param configs Updated Plugin Configs (YouboraConfig JSON, IMAConfig JSON etc)
     */
    static updatePluginConfigs: (configs: object) => void;
    /**
     * Play the player if it is not playing
     */
    static play: () => void;
    /**
     * Pause the player if it is playing
     */
    static pause: () => void;
    /**
     * Stops the player to the initial state
     */
    static stop: () => void;
    /**
     * Destroy the Kaltura Player instance
     */
    static destroy: () => void;
    /**
     * Replays the media from the beginning
     */
    static replay: () => void;
    /**
     * Seek the player to the specified position
     * @param position in miliseconds (Ms)
     */
    static seekTo: (position: number) => void;
    /**
     * Change a specific track (Video, Audio or Text track)
     * @param trackId Unique track ID which was sent in `tracksAvailable` event
     */
    static changeTrack: (trackId: string) => void;
    /**
     * Change the playback rate (ff or slow motion). Default is 1.0f
     * @param rate Desired playback rate (Ex: 0.5f, 1.5f 2.0f etc)
     */
    static setPlaybackRate: (rate: number) => void;
    /**
     * Change the volume of the current audio track.
     * Accept values between 0.0 and 1.0. Where 0.0 is mute and 1.0 is maximum volume.
     * If the volume parameter is higher then 1.0, it will be converted to 1.0.
     * If the volume parameter is lower then 0.0, it be converted to 0.0.
     *
     * @param vol - volume to set.
     */
    static setVolume: (vol: number) => void;
    /**
     * Set the media to play automatically at the start (load)
     * if `false`, user will have to click on UI play button
     *
     * @param isAutoPlay media should be autoplayed at the start or not
     */
    static setAutoPlay: (isAutoPlay: boolean) => void;
    /**
     * Set the KS for the media (only for OVP/OTT users)
     * Call this before calling {@link loadMedia}
     * @param KS Kaltura Secret key
     */
    static setKS: (KS: string) => void;
    /**
     * NOOP
     * @param index
     */
    /**
     * Only for Live Media.
     * Seek player to Live Default Position.
     */
    static seekToLiveDefaultPosition: () => void;
    /**
     * Update the existing subtitle styling
     */
    static updateSubtitleStyle: (subtitleStyle: string) => void;
    /**
     * Update the Resize Mode
     */
    static updateResizeMode: (mode: PLAYER_RESIZE_MODES) => void;
    /**
     * Update the ABR Settings
     */
    static updateAbrSettings: (abrSettings: string) => void;
    /**
     * Reset the ABR Settings
     */
    static resetAbrSettings: () => void;
    /**
     * Update the Low Latency Config
     * Only for Live Media
     */
    static updateLowLatencyConfig: (lowLatencyConfig: string) => void;
    /**
     * Reset the Low Latency Config
     * Only for Live Media
     */
    static resetLowLatencyConfig: () => void;
    /**
     * Get the current playback position for Content and Ad
     * @returns number: Position of the player or {@link POSITION_UNSET}
     */
    static getCurrentPosition: () => Promise<any>;
    /**
     * Checks if Player is currently playing or not
     * @returns boolean
     */
    static isPlaying: () => Promise<any>;
    /**
     * Checks if the stream is Live or Not
     * @returns boolean
     */
    static isLive: () => Promise<any>;
    /**
     * Get the Information for a thumbnail image by position.
     *
     * @param positionMs - relevant image for given player position.
     * @returns ThumbnailInfo JSON object
     */
    static requestThumbnailInfo: (positionMs: number) => Promise<any>;
    /**
     * Enable the console logs for the JS bridge
     * By default it is disabled.
     * @param enabled enable the debug logs
     * @returns if `enabled` is `null` then don't do anything
     */
    static enableDebugLogs: (enabled: boolean) => void;
}
