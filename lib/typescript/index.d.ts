import { NativeEventEmitter } from 'react-native';
import React from 'react';
import type { KalturaPlayerProps, LoadParams, MediaEntry, SetupParams } from './types';
export declare const KalturaPlayerModule: any;
export declare const KalturaPlayerEmitter: NativeEventEmitter;
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
    static setup: (partnerId: number, options: SetupParams) => any;
    static load: (assetId: string, options: LoadParams) => any;
    static setMedia: (mediaEntry: MediaEntry) => any;
    static playNewMedia: (options: Record<string, any>) => any;
    static destroy: () => any;
    static setVolume: (volume: number) => any;
    static seekTo: (position: number) => any;
    static setPlayerVisibility: (isVisible: boolean) => any;
    static setKeepAwake: (value: boolean) => any;
    static play: () => any;
    static replay: () => any;
    static pause: () => any;
    static stop: () => any;
    static changeTrack: (trackId: string) => any;
    static setMaxBitrate: (bitrate: number) => any;
    static updateLoadControlBuffers: () => () => void;
    static prepare: () => any;
    static setAutoplay: (value: boolean) => any;
    static setSubtitleStyle: (value: string) => any;
}
