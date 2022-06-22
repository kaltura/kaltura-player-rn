import Toast from 'react-native-root-toast';

let toast: Toast | null = null;

export function showToast(message: string) {
  toast = Toast.show(message, {
    duration: Toast.durations.SHORT,
    position: Toast.positions.BOTTOM,
    shadow: true,
    animation: true,
    hideOnPress: true,
    delay: 0,
  });
}

export function hideToast() {
  if (toast != null) {
    Toast.hide(toast);
  }
}
