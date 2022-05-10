import { ViewStyle } from 'react-native';
import React from 'react';
import PlayerEvents from './events/PlayerEvents';
interface KalturaPlayerProps {
    style: ViewStyle;
    playerType: PLAYER_TYPE;
}
export default PlayerEvents;
export declare enum PLAYER_TYPE {
    OVP = "ovp",
    OTT = "ott",
    BASIC = "basic"
}
export declare enum MEDIA_FORMAT {
    DASH = "dash",
    HLS = "hls",
    WVM = "wvm",
    MP4 = "mp4",
    MP3 = "mp3",
    UDP = "udp"
}
export declare enum MEDIA_ENTRY_TYPE {
    VOD = "Vod",
    LIVE = "Live",
    DVRLIVE = "DvrLive"
}
export declare enum DRM_SCHEME {
    WIDEVINE_CENC = "WidevineCENC",
    PLAYREADY_CENC = "PlayReadyCENC",
    WIDEVINE_CENC_CLASSIC = "WidevineClassic",
    PLAYREADY_CLASSIC = "PlayReadyClassic"
}
export declare class KalturaPlayer extends React.Component<KalturaPlayerProps> {
    nativeComponentRef: any;
    eventListener: any;
    playerType: PLAYER_TYPE | undefined;
    static propTypes: {
        style: object;
    };
    componentDidMount(): void;
    componentWillUnmount(): void;
    setNativeProps: (nativeProps: any) => void;
    /**
     * Add the listners for the Kaltura Player
     */
    addListeners: () => void;
    /**
     * Add the listners for the Kaltura Player
     */
    removeListeners: () => void;
    /**
     * This method creates a Player instance internally (Basic, OVP/OTT Player)
     * We this, it take the PlayerInitOptions which are having essential Player settings values
     * helpful for the playback
     *
     * @param options PlayerInitOptions JSON String
     * @param id PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value
     * should be always greater than 0 and should be valid otherwise, we will not be able to featch the details
     * for the mediaId or the entryId)
     */
    setup: (options: string, id?: number) => void;
    /**
     *
     * @param id Playback URL for Kaltura Basic Player OR
     * MediaId for Kaltura OTT Player OR
     * EntryId for Kaltura OVP Player
     * @param asset Media Asset JSON String
     */
    loadMedia: (id: string, asset: string) => void;
    /**
     * Play the player if it is not playing
     */
    play: () => void;
    /**
     * Pause the player if it is playing
     */
    pause: () => void;
    /**
     * Stops the player to the initial state
     */
    stop: () => void;
    /**
     * Replays the media from the beginning
     */
    replay: () => void;
    /**
     * Seek the player to the specified position
     * @param position in miliseconds (Ms)
     */
    seekTo: (position: number) => void;
    /**
     * Change a specific track (Video, Audio or Text track)
     * @param trackId Unique track ID which was sent in `tracksAvailable` event
     */
    changeTrack: (trackId: string) => void;
    /**
     * Change the playback rate (ff or slow motion). Default is 1.0f
     * @param rate Desired playback rate (Ex: 0.5f, 1.5f 2.0f etc)
     */
    setPlaybackRate: (rate: number) => void;
    /**
      * Change the volume of the current audio track.
      * Accept values between 0.0 and 1.0. Where 0.0 is mute and 1.0 is maximum volume.
      * If the volume parameter is higher then 1.0, it will be converted to 1.0.
      * If the volume parameter is lower then 0.0, it be converted to 0.0.
      *
      * @param vol - volume to set.
      */
    setVolume: (vol: number) => void;
    setAutoPlay: (isAutoPlay: boolean) => void;
    setKS: (KS: string) => void;
    setZIndex: (index: number) => void;
    render(): JSX.Element;
}
