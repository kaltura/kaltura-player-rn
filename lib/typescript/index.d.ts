import { ViewStyle, NativeEventEmitter } from 'react-native';
import React from 'react';
export declare const KalturaPlayerModule: any;
export declare const KalturaPlayerEmitter: NativeEventEmitter;
interface KalturaPlayerProps {
    style: ViewStyle;
}
export declare class KalturaPlayer extends React.Component<KalturaPlayerProps> {
    nativeComponentRef: any;
    eventListeners: any[];
    componentDidMount(): void;
    componentWillUnmount(): void;
    static propTypes: {
        style: object;
    };
    render(): JSX.Element;
}
export declare class KalturaPlayerAPI {
    static setup: (partnerId: number, options: {
        preload: boolean;
        autoplay: boolean;
        serverUrl: string;
        ks?: string;
    }) => any;
    static load: (assetId: string, options: {
        autoplay: boolean;
        assetType: "media" | "recording" | "epg";
        protocol: "http" | "https";
        playbackContextType?: "playback" | "catchup" | "trailer" | "startOver";
        assetReferenceType?: "media" | "epgInternal" | "epgExternal" | "npvr";
        urlType?: string;
        format?: string[];
        fileId?: string[];
        streamerType?: string;
        startPosition?: number;
    }) => any;
    static destroy: () => any;
    static setVolume: (volume: number) => any;
    static seekTo: (position: number) => any;
    static setPlayerVisibility: (isVisible: boolean) => any;
    static play: () => any;
    static replay: () => any;
    static pause: () => any;
    static stop: () => any;
    static setAutoplay: (value: boolean) => any;
}
export {};
