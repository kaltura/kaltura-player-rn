import React, { useEffect } from "react";
import {
  View,
  Text,
  NativeModules,
  NativeEventEmitter,
  Button,
} from "react-native";

const { KalturaPlayerModule } = NativeModules;

const KalturaPlayer = () => {
  console.log(KalturaPlayerModule);
  const onPress = () => {
    const url =
      "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";
    KalturaPlayerModule.createKalturaPlayerEvent(url, "BBB");
  };

  useEffect(() => {
    const playerEventEmitter = new NativeEventEmitter(KalturaPlayerModule);
    const eventListener = playerEventEmitter.addListener(
      "EventReminder",
      (event) => {
        console.log(event.eventProperty);
      }
    );
    return () => {
      eventListener.remove();
    };
  }, []);

  return (
    <View>
      <Text>Player</Text>
      <Button onPress={onPress} title="Kaltura Player event" />
    </View>
  );
};

export default KalturaPlayer;
