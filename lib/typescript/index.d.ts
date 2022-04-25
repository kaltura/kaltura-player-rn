import { ViewStyle, NativeEventEmitter } from 'react-native';
import React from 'react';
import { Requireable } from 'prop-types';
export declare const KalturaPlayerModule: any;
export declare const KalturaPlayerEmitter: NativeEventEmitter;
interface KalturaPlayerProps {
    style: ViewStyle;
    partnerId: number;
    assetId: string;
    playerInitOptions: string;
    mediaAsset: string;
}
export declare class KalturaPlayer extends React.Component<KalturaPlayerProps> {
    nativeComponentRef: any;
    eventListener: any;
    componentDidMount(): void;
    componentWillUnmount(): void;
    static propTypes: {
        style: object;
        partnerId: Requireable<number>;
        assetId: Requireable<string>;
        prepare: Requireable<boolean>;
        playerInitOptions: Requireable<string>;
        mediaAsset: Requireable<string>;
    };
    setAssetId: (id: string) => void;
    setPlayerInitOptions: (options: string) => void;
    setMediaAsset: (asset: string) => void;
    prepare: () => void;
    play: () => void;
    pause: () => void;
    stop: () => void;
    replay: () => void;
    seekTo: (position: number) => void;
    changeTrack: (trackId: string) => void;
    setPlaybackRate: (rate: number) => void;
    setVolume: (vol: number) => void;
    setAutoPlay: (isAutoPlay: boolean) => void;
    setKS: (KS: string) => void;
    setZIndex: (index: number) => void;
    updateMediaAsset: (updatedMediaAsset: string) => void;
    updateAssetId: (updatedAssetId: string) => void;
    render(): JSX.Element;
}
export {};
