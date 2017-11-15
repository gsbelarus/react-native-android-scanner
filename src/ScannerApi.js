import { DeviceEventEmitter, NativeModules } from 'react-native'

export default class ScannerApi {

  static SCANNER_READ_FAIL = NativeModules.Scanner.SCANNER_READ_FAIL
  static SCANNER_CALLBACK_TASK = NativeModules.Scanner.SCANNER_CALLBACK_TASK

  static TRIGGER_MODE_ONESHOT = NativeModules.Scanner.TRIGGER_MODE_ONESHOT
  static TRIGGER_MODE_AUTO = NativeModules.Scanner.TRIGGER_MODE_AUTO
  static TRIGGER_MODE_CONTINUOUS = NativeModules.Scanner.TRIGGER_MODE_CONTINUOUS

  static async start (options) {
    return await NativeModules.Scanner.start(options || {})
  }

  static async stop () {
    return await NativeModules.Scanner.stop()
  }

  static async isRunning () {
    return await NativeModules.Scanner.isRunning()
  }

  static async isDeviceSupported () {
    return await NativeModules.Scanner.isDeviceSupported()
  }

  static addListener (func) {
    return DeviceEventEmitter.addListener(NativeModules.Scanner.ON_SCANNED, func)
  }

  static removeListener (func) {
    return DeviceEventEmitter.removeListener(NativeModules.Scanner.ON_SCANNED, func)
  }

  static addStatusChangeListener (func) {
    return DeviceEventEmitter.addListener(NativeModules.Scanner.ON_STATUS_SCANNED_CHANGED, func)
  }

  static removeStatusChangeListener (func) {
    return DeviceEventEmitter.removeListener(NativeModules.Scanner.ON_STATUS_SCANNED_CHANGED, func)
  }
}