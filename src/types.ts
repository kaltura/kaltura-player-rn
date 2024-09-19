import type { ViewStyle } from 'react-native';

export interface KalturaPlayerProps {
  style: ViewStyle;
}

type DrmData = {
  licenseUrl: string;
  scheme: string;
  certificate?: string;
};

type MetadataConfig = {
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

type MediaEntrySource = {
  id: string;
  url: string;
  mimetype: string;
  bandwidth?: number;
  width?: number;
  height?: number;
  label?: string;
  drmData?: Array<DrmData>;
};

export type MediaEntry = {
  id: number;
  sources: Array<MediaEntrySource>;
  metadata: MetadataConfig;
  mediaType: string;
};

export type LoadParams = {
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

export type SetupParams = {
  options: {
    preload: boolean;
    autoplay: boolean;
    serverUrl: string;
    ks?: string;
  };
};
