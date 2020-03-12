import {NativeModules} from "react-native";

const RNKitModule = NativeModules.RNKit;

const transformMessage = (message) => {
    if (typeof message === 'object') {
        message = JSON.stringify(message)
    } else {
        message = String(message);
    }
    return message;
};

export default {
    v: (message) => {
        RNKitModule.log('verbose', transformMessage(message));
    },
    d: (message) => {
        RNKitModule.log('debug', transformMessage(message));
    },
    i: (message) => {
        RNKitModule.log('i', transformMessage(message));
    },
    w: (message) => {
        RNKitModule.log('warn', transformMessage(message));
    },
    e: (message) => {
        RNKitModule.log('error', transformMessage(message));
    },
    f: (message) => {
        RNKitModule.log('fatal', transformMessage(message));
    },
}