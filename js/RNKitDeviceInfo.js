import {NativeModules} from "react-native";

const RNKitModule = NativeModules.RNKit;

export default {

    getPackageName: () => {
        return RNKitModule.packageName;
    },

    getAppVersionName: () => {
        return RNKitModule.appVersionName;
    },

    getAppVersionCode: () => {
        return RNKitModule.appVersionCode;
    },

    getBundleVersionCode: () => {
        return RNKitModule.bundleVersionCode;
    },

    getSerialNumber:()=>{
        return RNKitModule.serialNumber;
    },

    getSystemName:()=>{
        return RNKitModule.systemName;
    },

    getSystemVersion:()=>{
        return RNKitModule.systemVersion;
    },

    getModel:()=>{
        return RNKitModule.model;
    },

    getBrand:()=>{
        return RNKitModule.brand;
    },

    getDeviceId:()=>{
        return RNKitModule.deviceId;
    },

    getApiLevel:()=>{
        return RNKitModule.apiLevel;
    },
}