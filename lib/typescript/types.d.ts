import type { ViewStyle } from 'react-native';
export interface KalturaPlayerProps {
    style: ViewStyle;
}
declare type DrmData = {
    licenseUrl: string;
    scheme: string;
    certificate?: string;
};
declare type MetadataConfig = {
    name: string;
    description?: string;
    mediaType?: string;
    contextType?: string;
    metas?: any;
    tags?: any;
    epgId?: string;
    recordingId?: string;
    updatedAt?: number;
    creatorId?: string;
    views?: number;
};
declare type MediaEntrySource = {
    id: string;
    url: string;
    mimetype: string;
    bandwidth?: number;
    width?: number;
    height?: number;
    label?: string;
    drmData?: Array<DrmData>;
};
export declare type MediaEntry = {
    id: number;
    sources: Array<MediaEntrySource>;
    metadata: MetadataConfig;
    mediaType: string;
};
export declare type LoadParams = {
    autoplay: boolean;
    assetType: 'media' | 'recording' | 'epg';
    protocol: 'http' | 'https';
    playbackContextType?: 'playback' | 'catchup' | 'trailer' | 'startOver';
    assetReferenceType?: 'media' | 'epgInternal' | 'epgExternal' | 'npvr';
    urlType?: string;
    format?: string[];
    fileId?: string[];
    streamerType?: string;
    startPosition?: number;
};
export declare type SetupParams = {
    options: {
        preload: boolean;
        autoplay: boolean;
        serverUrl: string;
        ks?: string;
    };
};
export declare type SubTitleStyleSettings = 'default' | 'blackBackgroundWhiteText' | 'yellowText';
export {};
