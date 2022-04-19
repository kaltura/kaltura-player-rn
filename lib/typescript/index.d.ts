import { ViewStyle, NativeEventEmitter } from 'react-native';
import React from 'react';
import { Requireable } from 'prop-types';
export declare const KalturaPlayerModule: any;
export declare const KalturaPlayerEmitter: NativeEventEmitter;
interface KalturaPlayerProps {
    style: ViewStyle;
    assetId: string;
    partnerId: number;
    baseUrl: String;
    prepare: boolean;
}
export declare class KalturaPlayer extends React.Component<KalturaPlayerProps> {
    nativeComponentRef: any;
    eventListener: any;
    componentDidMount(): void;
    componentWillUnmount(): void;
    static propTypes: {
        style: object;
        assetId: Requireable<string>;
        partnerId: Requireable<number>;
        baseUrl: Requireable<string>;
        prepare: Requireable<boolean>;
    };
    render(): JSX.Element;
}
export {};
