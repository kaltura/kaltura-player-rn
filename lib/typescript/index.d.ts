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
    setNativeProps: (nativeProps: any) => void;
    addListeners: () => void;
    removeListeners: () => void;
    setup: (id: number, options: string) => void;
    load: (id: string, asset: string) => void;
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
    render(): JSX.Element;
}
export {};
