package com.reactnativekalturaplayer.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.kaltura.playkit.providers.api.phoenix.APIDefines;
import com.kaltura.playkit.providers.ott.OTTMediaAsset;
import com.kaltura.playkit.providers.ott.PhoenixMediaProvider;
import com.kaltura.playkit.providers.ovp.OVPMediaAsset;
import com.kaltura.tvplayer.OTTMediaOptions;
import com.kaltura.tvplayer.OVPMediaOptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Media asset for OVP and OTT
 */
public class MediaAsset {
    private String ks;        // ovp or ott

    // OTT Params
    private List<String> format;    // ott
    private List<String> fileId;    // ott //TODO: FIX it with List of fileIds
    private String assetType; // ott
    private String playbackContextType; // ott
    private String assetReferenceType;  // ott
    @SerializedName(value = "protocol", alternate = {"networkProtocol"})
    private String protocol;            // ott
    private String urlType; // ott
    private String streamerType; // ott
    private Map<String,String> adapterData; // ott
    private String referrer;

    // OVP Params
    private Boolean redirectFromEntryId = true;
    private Boolean useApiCaptions = false;

    private long startPosition;
    private Float initialVolume = 1.0f;

    private String getKs() {
        return ks;
    }

    private List<String> getFormat() {
        return format;
    }

    private List<String> getFileId() {
        return fileId;
    }

    private Boolean getUseApiCaptions() {
        return useApiCaptions;
    }

    public String getReferrer() {
        return referrer;
    }

    public Map<String,String> getAdapterData() {
        return adapterData;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public Float getInitialVolume() {
        return initialVolume;
    }

    private APIDefines.KalturaAssetType getAssetType() {
        if (assetType == null) {
            return null;
        }

        if (APIDefines.KalturaAssetType.Media.value.equals(assetType.toLowerCase())) {
            return APIDefines.KalturaAssetType.Media;
        } else if (APIDefines.KalturaAssetType.Epg.value.equals(assetType.toLowerCase())) {
            return APIDefines.KalturaAssetType.Epg;
        } else if (APIDefines.KalturaAssetType.Recording.value.equals(assetType.toLowerCase())) {
            return APIDefines.KalturaAssetType.Recording;
        }
        return null;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    private APIDefines.PlaybackContextType getPlaybackContextType() {
        if (playbackContextType == null) {
            return null;
        }

        if (APIDefines.PlaybackContextType.Playback.value.toLowerCase().equals(playbackContextType.toLowerCase())) {
            return APIDefines.PlaybackContextType.Playback;
        } else if (APIDefines.PlaybackContextType.StartOver.value.toLowerCase().equals(playbackContextType.toLowerCase())) {
            return APIDefines.PlaybackContextType.StartOver;
        } else if (APIDefines.PlaybackContextType.Trailer.value.toLowerCase().equals(playbackContextType.toLowerCase())) {
            return APIDefines.PlaybackContextType.Trailer;
        } else if (APIDefines.PlaybackContextType.Catchup.value.toLowerCase().equals(playbackContextType.toLowerCase())) {
            return APIDefines.PlaybackContextType.Catchup;
        }
        return null;
    }


    public void setPlaybackContextType(String playbackContextType) {
        this.playbackContextType = playbackContextType;
    }

    private APIDefines.AssetReferenceType getAssetReferenceType() {
        if (assetReferenceType == null) {
            return null;
        }
        if (APIDefines.AssetReferenceType.Media.value.toLowerCase().equals(assetReferenceType.toLowerCase())) {
            return APIDefines.AssetReferenceType.Media;
        } else if (APIDefines.AssetReferenceType.ExternalEpg.value.toLowerCase().equals(assetReferenceType.toLowerCase())) {
            return APIDefines.AssetReferenceType.ExternalEpg;
        } else if (APIDefines.AssetReferenceType.InternalEpg.value.toLowerCase().equals(assetReferenceType.toLowerCase())) {
            return APIDefines.AssetReferenceType.InternalEpg;
        } else if (APIDefines.AssetReferenceType.Npvr.value.toLowerCase().equals(assetReferenceType.toLowerCase())) {
            return APIDefines.AssetReferenceType.Npvr;
        }
        return null;
    }

    public void setAssetReferenceType(String assetReferenceType) {
        this.assetReferenceType = assetReferenceType;
    }


    private String getProtocol() {
        if (protocol == null) {
            return null;
        }
        if (PhoenixMediaProvider.HttpProtocol.All.toLowerCase().equals(protocol.toLowerCase())) {
            return PhoenixMediaProvider.HttpProtocol.All;
        } else if (PhoenixMediaProvider.HttpProtocol.Http.toLowerCase().equals(protocol.toLowerCase())) {
            return PhoenixMediaProvider.HttpProtocol.Http;
        } else if (PhoenixMediaProvider.HttpProtocol.Https.toLowerCase().equals(protocol.toLowerCase())) {
            return PhoenixMediaProvider.HttpProtocol.Https;
        }
        return null;
    }

    private APIDefines.KalturaUrlType getUrlType() {
        if (urlType == null) {
            return null;
        }
        if (APIDefines.KalturaUrlType.Direct.value.toLowerCase().equals(urlType.toLowerCase())) {
            return APIDefines.KalturaUrlType.Direct;
        } else if (APIDefines.KalturaUrlType.PlayManifest.value.toLowerCase().equals(urlType.toLowerCase())) {
            return APIDefines.KalturaUrlType.PlayManifest;
        }
        return null;
    }

    public Boolean getRedirectFromEntryId() {
        return redirectFromEntryId;
    }

    private APIDefines.KalturaStreamerType getStreamerType() {
        if (streamerType == null) {
            return null;
        }
        if (APIDefines.KalturaStreamerType.Mpegdash.value.toLowerCase().equals(streamerType.toLowerCase())) {
            return APIDefines.KalturaStreamerType.Mpegdash;
        } else if (APIDefines.KalturaStreamerType.Applehttp.value.toLowerCase().equals(streamerType.toLowerCase())) {
            return APIDefines.KalturaStreamerType.Applehttp;
        } else if (APIDefines.KalturaStreamerType.Url.value.toLowerCase().equals(streamerType.toLowerCase())) {
            return APIDefines.KalturaStreamerType.Url;
        } else if (APIDefines.KalturaStreamerType.Smothstreaming.value.toLowerCase().equals(streamerType.toLowerCase())) {
            return APIDefines.KalturaStreamerType.Smothstreaming;
        } else if (APIDefines.KalturaStreamerType.Multicast.value.toLowerCase().equals(streamerType.toLowerCase())) {
            return APIDefines.KalturaStreamerType.Multicast;
        } else if (APIDefines.KalturaStreamerType.None.value.toLowerCase().equals(streamerType.toLowerCase())) {
            return APIDefines.KalturaStreamerType.None;
        }
        return null;
    }

    public OTTMediaOptions buildOttMediaOptions(String assetId, String playerKS) {

        OTTMediaAsset ottMediaAsset = new OTTMediaAsset();
        ottMediaAsset.setAssetId(assetId);
        ottMediaAsset.setAssetType(getAssetType());
        ottMediaAsset.setContextType(getPlaybackContextType());
        ottMediaAsset.setAssetReferenceType(getAssetReferenceType());
        ottMediaAsset.setProtocol(getProtocol());
        ottMediaAsset.setKs(ks != null ? ks : playerKS);
        ottMediaAsset.setReferrer(referrer);
        ottMediaAsset.setUrlType(getUrlType());
        ottMediaAsset.setStreamerType(getStreamerType());
        ottMediaAsset.setAdapterData(getAdapterData());
        if (format != null && !format.isEmpty()) {
            ottMediaAsset.setFormats(format);
        }
        if (fileId != null && !fileId.isEmpty()) {
            ottMediaAsset.setMediaFileIds(fileId);
        }
        OTTMediaOptions ottMediaOptions = new OTTMediaOptions(ottMediaAsset);
        ottMediaOptions.startPosition = startPosition;
        return ottMediaOptions;
    }

    public OVPMediaOptions buildOvpMediaOptions(String entryId, String referenceId, String playerKS) {

        OVPMediaAsset ovpMediaAsset = new OVPMediaAsset();

        if (!TextUtils.isEmpty(entryId)) {
            ovpMediaAsset.setEntryId(entryId);
        }

        ovpMediaAsset.setKs(ks != null ? ks : playerKS);
        ovpMediaAsset.setReferrer(referrer);

        if (!TextUtils.isEmpty(referenceId)) {
            ovpMediaAsset.setReferenceId(referenceId);
        }

        if (redirectFromEntryId != null) {
            ovpMediaAsset.setRedirectFromEntryId(redirectFromEntryId);
        }

        OVPMediaOptions ovpMediaOptions = new OVPMediaOptions(ovpMediaAsset);

        if (useApiCaptions != null) {
            ovpMediaOptions.setUseApiCaptions(useApiCaptions);
        }

        ovpMediaOptions.startPosition = startPosition;

        return ovpMediaOptions;
    }
}
