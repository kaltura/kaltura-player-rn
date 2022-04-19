import {
  requireNativeComponent,
  NativeModules,
  ViewStyle,
  NativeEventEmitter,
} from 'react-native';
import React from 'react';
import PropTypes, { Requireable } from 'prop-types';

export const KalturaPlayerModule = NativeModules.KalturaPlayerViewManager;
const KalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
export const KalturaPlayerEmitter = new NativeEventEmitter(KalturaPlayerEvents);

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');

console.log(KalturaPlayerEvents)
interface KalturaPlayerProps {
  style: ViewStyle,
  assetId: string,
  partnerId: number,
  baseUrl: String,
  prepare: boolean
}

export class KalturaPlayer extends React.Component<KalturaPlayerProps> {
  nativeComponentRef: any;
  eventListener: any;

  componentDidMount() {
    // this.eventListener = KalturaPlayerEmitter.addListener('playing', (event: any) => {
    //   console.log(event)
    // });
  }

  componentWillUnmount() {
    //this.eventListener.remove(); //Removes the listener
  }

  static propTypes: {
    style: object,
    assetId: Requireable<string>,
    partnerId: Requireable<number>,
    baseUrl: Requireable<string>,
    prepare: Requireable<boolean>;
  };

  render() {
    return <RNKalturaPlayer {...this.props} ref={(nativeRef) => this.nativeComponentRef = nativeRef} />;
  }
}

KalturaPlayer.propTypes = {
  style: PropTypes.object,
  assetId: PropTypes.string,
  partnerId: PropTypes.number,
  baseUrl: PropTypes.string,
  prepare: PropTypes.bool
};


