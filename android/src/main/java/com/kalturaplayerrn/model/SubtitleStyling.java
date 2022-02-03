package com.kaltura.react_native_kplayer.model;

import android.graphics.Color;
import android.text.Layout;

import com.kaltura.playkit.player.SubtitleStyleSettings;

public class SubtitleStyling {
    public String subtitleStyleName;
    public Config config;

    public class Config {
        public String subtitleTextColor;
        public String subtitleBackgroundColor;
        public String subtitleWindowColor;
        public String subtitleEdgeColor;
        public String subtitleTextSizeFraction;
        public String subtitleStyleTypeface;
        public String subtitleEdgeType;
        public Boolean overrideInlineCueConfig;
        public Integer verticalPositionPercentage;
        public Integer horizontalPositionPercentage;
        public String horizontalAlignment;

        public Integer getColorDefaultWhite(String color) {
            if (color != null) {
                return Color.parseColor(color);
            } else {
                return Color.WHITE;
            }
        }

        public Integer getColorDefaultBlack(String color) {
            if (color != null) {
                return Color.parseColor(color);
            } else {
                return Color.BLACK;
            }
        }

        public SubtitleStyleSettings.SubtitleTextSizeFraction getSubtitleTextSizeFraction() {
            if (SubtitleStyleSettings.SubtitleTextSizeFraction.valueOf(subtitleTextSizeFraction) != null) {
                return SubtitleStyleSettings.SubtitleTextSizeFraction.valueOf(subtitleTextSizeFraction);
            }
            return SubtitleStyleSettings.SubtitleTextSizeFraction.SUBTITLE_FRACTION_100;
        }

        public SubtitleStyleSettings.SubtitleStyleTypeface getSubtitleStyleTypeface() {
            if (SubtitleStyleSettings.SubtitleStyleTypeface.valueOf(subtitleStyleTypeface) != null) {
                return SubtitleStyleSettings.SubtitleStyleTypeface.valueOf(subtitleStyleTypeface);
            }
            return SubtitleStyleSettings.SubtitleStyleTypeface.DEFAULT;
        }

        public SubtitleStyleSettings.SubtitleStyleEdgeType getSubtitleEdgeType() {
            if (SubtitleStyleSettings.SubtitleStyleEdgeType.valueOf(subtitleEdgeType) != null) {
                return SubtitleStyleSettings.SubtitleStyleEdgeType.valueOf(subtitleEdgeType);
            }
            return SubtitleStyleSettings.SubtitleStyleEdgeType.EDGE_TYPE_NONE;
        }

        public Layout.Alignment getHorizontalAlignment() {
            if (Layout.Alignment.valueOf(horizontalAlignment) != null) {
                return Layout.Alignment.valueOf(horizontalAlignment);
            }
            return Layout.Alignment.ALIGN_CENTER;
        }
    }
}
