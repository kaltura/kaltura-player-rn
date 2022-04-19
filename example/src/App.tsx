import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import { KalturaPlayer, KalturaPlayerAPI } from 'react-native-kaltura-player';

export default function App() {
  // setup(3009, {
  //   preload: true,
  //   autoplay: true,
  //   serverUrl: "https://rest-us.ott.kaltura.com/v4_5/api_v3/"
  // })
  //console.log(KalturaPlayerView);
  // KalturaPlayerAPI.setup(3009, {
  //   preload: true,
  //   autoplay: true,
  //   serverUrl: "https://rest-us.ott.kaltura.com/v4_5/api_v3/"
  // })
  // KalturaPlayerAPI.loadMedia("548576", {
  //   autoplay: true,
  //   assetType: "media",
  //   playbackContextType: "playback",
  //   protocol: "http"
  // })
  // setTimeout(() => {
  //   KalturaPlayerAPI.pause()
  // }, 5000)
  return (
    <View>
      <Text style={styles.red}>just red</Text>
      <Text style={styles.bigBlue}>just bigBlue</Text>
      
      <KalturaPlayer style = {styles.center}
      assetId = {"548576"}
      partnerId = {3009}
      baseUrl = {'https://rest-us.ott.kaltura.com/v4_5/api_v3/'}
      prepare = {true} >
      </KalturaPlayer>

      <Text style={[styles.bigBlue, styles.red]}>bigBlue, then red</Text>
      <Text style={[styles.red, styles.bigBlue]}>red, then bigBlue</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginTop: 50,
  },
  bigBlue: {
    color: 'blue',
    fontWeight: 'bold',
    fontSize: 30,
  },
  red: {
    color: 'red',
  },
  center: {
    flex: 1,
    padding: 100,
    height: 300,
    alignItems: 'center'
  }
});

