/**
* @providesModule P2PKit
* @flow
*/
'use strict';

import {
  NativeModules,
  NativeEventEmitter
} from 'react-native';

const { PPKReactBridge } = NativeModules;

export default class P2PKit {
  static LOW_POWER = 'LOW_POWER';
  static HIGH_PERFORMANCE = 'HIGH_PERFORMANCE';
  static p2pkitCallbackEmitter = new NativeEventEmitter(PPKReactBridge);

  static addListener(name, callback) {
    P2PKit.p2pkitCallbackEmitter.addListener(name, callback);
  }

  static removeListener(name, callback) {
    P2PKit.p2pkitCallbackEmitter.removeListener(name, callback);
  }

  static enable(appkey) {
    PPKReactBridge.enable(appkey);
  }

  static disable() {
    PPKReactBridge.disable();
  }

  static getMyPeerId(myPeerId) {
    PPKReactBridge.getMyPeerId();
  }

  static startDiscovery(discoveryInfo, discoveryPowerMode) {
    PPKReactBridge.startDiscovery(discoveryInfo, discoveryPowerMode);
  }

  static stopDiscovery() {
    PPKReactBridge.stopDiscovery();
  }

  static enableProximityRanging() {
    PPKReactBridge.enableProximityRanging();
  }

  static pushNewDiscoveryInfo(discoveryInfo) {
    PPKReactBridge.pushNewDiscoveryInfo(discoveryInfo);
  }

  static getDiscoveryPowerMode() {
    PPKReactBridge.getDiscoveryPowerMode();
  }

  static setDiscoveryPowerMode(discoveryPowerMode) {
    PPKReactBridge.setDiscoveryPowerMode(discoveryPowerMode);
  }
}
