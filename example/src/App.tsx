import * as React from 'react';

import { StyleSheet, View } from 'react-native';
import { KalturaPlayer, KalturaPlayerAPI } from 'react-native-kaltura-player';

export default function App() {
  // setup(3009, {
  //   preload: true,
  //   autoplay: true,
  //   serverUrl: "https://rest-us.ott.kaltura.com/v4_5/api_v3/"
  // })
  //console.log(KalturaPlayerView);
  KalturaPlayerAPI.setup(3009, {
    preload: true,
    autoplay: true,
    serverUrl: "https://rest-us.ott.kaltura.com/v4_5/api_v3/"
  })
  KalturaPlayerAPI.loadMedia("548576", {
    autoplay: true,
    assetType: "media",
    playbackContextType: "playback",
    protocol: "http"
  })
  setTimeout(() => {
    KalturaPlayerAPI.pause()
  }, 5000)
  return (
    <View style={styles.container}>
      <KalturaPlayer style={styles.box} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: '100%',
    height: '100%',
    marginVertical: 20,
  },
});
