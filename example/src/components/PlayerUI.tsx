'use strict';

import React, { Component } from 'react';
import { KalturaPlayer, PLAYER_TYPE } from 'react-native-kaltura-player';
import { StyleSheet, Text, View, Image, TouchableOpacity } from 'react-native';
import PropTypes from 'prop-types';
import SeekBar from '../components/SeekBar';
import { showToast } from './ScreenMessage';
const iconPlay = require('../assets/images/ic_play.png');
const iconPause = require('../assets/images/ic_pause.png');
const iconReplay = require('../assets/images/ic_replay.png');
const iconFullScreen = require('../assets/images/ic_full_screen.png');
const iconMute = require('../assets/images/ic_mute.png');
const iconUnmute = require('../assets/images/ic_volume.png');
const iconSeekFrwd = require('../assets/images/ic_seekfrwd.png');
const iconSeekBack = require('../assets/images/ic_seekback.png');

const iconHeight = 40;
const iconWidth = 40;

// TODO: Add the default Props feature.
export class PlayerUI extends React.Component<any, any> {

  isPlayerMute = false;
  isSeekedForward = false;

  constructor(props: any) {
    super(props);
    this.state = {
      playerType: props.playerType,
      isAdPlaying: props.isAdPlaying,
      isContentPlaying: props.isContentPlaying,
      position: props.position,
      duration: props.duration,
      onSeekBarScrubbed: props.onSeekBarScrubbed,
      onSeekBarScrubbing: props.onSeekBarScrubbing,
      playPauseIconPressed: props.playPauseIconPressed,
      replayButtonPressed: props.replayButtonPressed,
      muteUnmuteButtonPressed: props.muteUnmuteButtonPressed,
      seekButtonPressed:props.seekButtonPressed,
    };

    console.log(`Props are: ${this.props.playerType}`);
  }

  componentDidUpdate(prevProps) {
    if (this.props.playerType !== prevProps.playerType) {
      this.setState(() => ({
        playerType: this.props.playerType,
      }));
    }

    if (this.props.isAdPlaying !== prevProps.isAdPlaying) {
      this.setState(() => ({
        isAdPlaying: this.props.isAdPlaying,
      }));
    }

    if (this.props.isContentPlaying !== prevProps.isContentPlaying) {
      this.setState(() => ({
        isContentPlaying: this.props.isContentPlaying,
      }));
    }

    if (this.props.position !== prevProps.position) {
      this.setState(() => ({
        position: this.props.position,
      }));
    }

    if (this.props.duration !== prevProps.duration) {
      this.setState(() => ({
        duration: this.props.duration,
      }));
    }

    if (this.props.onSeekBarScrubbed !== prevProps.onSeekBarScrubbed) {
      this.setState(() => ({
        onSeekBarScrubbed: this.props.onSeekBarScrubbed,
      }));
    }

    if (this.props.onSeekBarScrubbing !== prevProps.onSeekBarScrubbing) {
      this.setState(() => ({
        onSeekBarScrubbing: this.props.onSeekBarScrubbing,
      }));
    }
  }

  componentDidMount() {}

  componentWillUnmount() {}

  onSeekBarScrubbed = (seekedPosition: number) => {
    this.props.onSeekBarScrubbed(seekedPosition);
  };

  onSeekBarScrubbing = (isSeeking: boolean) => {
    this.props.onSeekBarScrubbing(isSeeking);
  };

  render() {
    return (
      <View style={styles.root}>
        <KalturaPlayer
          style={styles.playerView}
          playerType={this.state.playerType}
        ></KalturaPlayer>

        <SeekBar
          style={styles.seekbar}
          isAdPlaying={this.state.isAdPlaying}
          position={this.state.position}
          duration={this.state.duration}
          onSeekBarScrubbed={this.onSeekBarScrubbed}
          onSeekBarScrubbing={this.onSeekBarScrubbing}
        ></SeekBar>

        <View
          style={[
            styles.flex_container,
            {
              flexDirection: 'row',
            },
          ]}
        >
          <Text style={{ flex: 1 }}>
            {convertToHHMMSS(this.state.position)}
          </Text>

          <Text>{convertToHHMMSS(this.state.duration)}</Text>
        </View>

        <View
          style={{
            flex: 1,
            width: '100%',
            height: 60,
            padding: 10,
            alignItems: 'center',
            flexDirection: 'row',
            justifyContent: 'space-between',
          }}
        >
          <TouchableOpacity
            style={{ flex: 1 }}
            onPress={() => {
              if (this.state.isAdPlaying) { 
                showToast("Action not allowed when Ad is playing")
                return; 
              }
              this.state.playPauseIconPressed();
            }}
          >
            <Image
              source={this.state.isContentPlaying ? iconPause : iconPlay}
              resizeMode="contain"
              style={{ flex: 1, width: iconHeight, height: iconWidth }}
            />
          </TouchableOpacity>

          <TouchableOpacity
            style={{ flex: 1 }}
            onPress={() => {
              if (this.state.isAdPlaying) { 
                showToast("Action not allowed when Ad is playing")
                return; 
              }
              this.state.replayButtonPressed();
            }}
          >
            <Image
              source={iconReplay}
              resizeMode="contain"
              style={{ flex: 1, width: iconHeight, height: iconWidth }}
            />
          </TouchableOpacity>

          <TouchableOpacity
            style={{ flex: 1 }}
            onPress={() => {
              if (this.state.isAdPlaying) { 
                showToast("Action not allowed when Ad is playing")
                return; 
              }

              this.isSeekedForward = false;
              this.state.seekButtonPressed(this.isSeekedForward);
            }}
          >
            <Image
              source={iconSeekBack}
              resizeMode="contain"
              style={{ flex: 1, width: iconHeight, height: iconWidth }}
            />
          </TouchableOpacity>

          <TouchableOpacity
            style={{ flex: 1 }}
            onPress={() => {
              if (this.state.isAdPlaying) { 
                showToast("Action not allowed when Ad is playing")
                return; 
              }

              this.isSeekedForward = true;
              this.state.seekButtonPressed(this.isSeekedForward);
            }}
          >
            <Image
              source={iconSeekFrwd}
              resizeMode="contain"
              style={{ flex: 1, width: iconHeight, height: iconWidth }}
            />
          </TouchableOpacity>

          <TouchableOpacity
            style={{ flex: 1 }}
            onPress={() => {
              showToast("Not functional right now.")
            }}
          >
            <Image
              source={iconFullScreen}
              resizeMode="contain"
              style={{ flex: 1, width: iconHeight, height: iconWidth }}
            />
          </TouchableOpacity>

          <TouchableOpacity
            style={{ flex: 1 }}
            onPress={() => {
              if (this.state.isAdPlaying) { 
                showToast("Action not allowed when Ad is playing")
                return; 
              }

              if (this.isPlayerMute) {
                this.isPlayerMute = false;
              } else {
                this.isPlayerMute = true;
              }

              this.state.muteUnmuteButtonPressed(this.isPlayerMute);
            }}
          >
            <Image
              source={this.isPlayerMute ? iconUnmute : iconMute}
              resizeMode="contain"
              style={{ flex: 1, width: iconHeight, height: iconWidth }}
            />
          </TouchableOpacity>

        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: '#fff',
  },
  playerView: {
    padding: 100,
    height: 300,
    alignItems: 'center',
  },
  seekbar: {
    marginTop: -300,
  },
  flex_container: {
    flexWrap: 'wrap',
    marginStart: 5,
    marginEnd: 5,
  },
});

function convertToHHMMSS(value: number) {
  var sec_num = value; // don't forget the second param
  var hours = Math.floor(sec_num / 3600);
  var minutes = Math.floor((sec_num - hours * 3600) / 60);
  var seconds = Math.floor(sec_num - hours * 3600 - minutes * 60);

  if (hours < 10) {
    hours = '0' + hours;
  }
  if (minutes < 10) {
    minutes = '0' + minutes;
  }
  if (seconds < 10) {
    seconds = '0' + seconds;
  }

  if (hours > 0) {
    return hours + ':' + minutes + ':' + seconds;
  } else {
    return minutes + ':' + seconds;
  }
}
