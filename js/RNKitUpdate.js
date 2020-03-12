import {NativeModules, Alert} from "react-native";

const RNKitModule = NativeModules.RNKit;

export default {

    hasApkUpdate: (appVersionCode) => {
        return appVersionCode > RNKitModule.appVersionCode;
    },

    hasPatchUpdate: (bundleVersionCode) => {
        return bundleVersionCode > RNKitModule.bundleVersionCode;
    },

    startApkUpdate: (appVersionCode,downloadUrl, releaseNotes,showAlert=true) => {
        if(showAlert){
            Alert.alert("发现新版本",
                releaseNotes,
                [
                    {text: "下次吧", onPress: null},
                    {
                        text: "立即更新", onPress: () => {
                            RNKitModule.startApkDownload(Number(appVersionCode),downloadUrl);
                        }
                    }
                ]
            )
        }else{
            RNKitModule.startApkDownload(Number(appVersionCode),downloadUrl);
        }
    },

    startPatchUpdate: (bundleVersionCode,downloadUrl, installType = 1, releaseNotes) => {
        RNKitModule.startPatchDownload(Number(bundleVersionCode),downloadUrl, Number(installType));
    },


}
