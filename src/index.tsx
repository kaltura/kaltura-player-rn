import { requireNativeComponent, ViewStyle } from 'react-native'

type KalturaPlayerRnProps = {
  color: string
  style: ViewStyle
}

export const KalturaPlayerRnViewManager =
  requireNativeComponent<KalturaPlayerRnProps>('KalturaPlayerRnView')

export default KalturaPlayerRnViewManager
