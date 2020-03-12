import {NativeModules,Alert,BackHandler} from "react-native";
import RNKitDeviceInfo from './RNKitDeviceInfo';
import RNKitLog from './RNKitLog';

const RNKitModule = NativeModules.RNKit;

const defaultReporter = (errMsg) => {
    // fatal error will be saved to sdCard
    RNKitLog.f(errMsg);
    customReporter && customReporter(errMsg)
};

const defaultJsErrorHandler = (error, isFatal) => {
    if (isFatal) {
        defaultReporter(`${error.name} ${error.message}`);

        Alert.alert(
            '','很抱歉，程序出现了异常，我们的攻城狮会尽快修复.',
            [{
                text: '退出应用',
                onPress: () => {
                    BackHandler.exitApp();
                }
            }, {
                text: '继续使用',
                onPress: () => {}
            }],
            {cancelable: false}
        );
    }
};


export default {
    init:(allowedInDevMode = false,reporter)=>{
        if (typeof allowedInDevMode !== "boolean" ){
            console.log("init is called with wrong argument types.. the argument  is optional should be a boolean");
            console.log("Not setting the JS handler .. please fix setJSExceptionHandler call");
            return;
        }
        const allowed = allowedInDevMode ? true : !__DEV__;
        if (allowed) {
            customReporter = reporter;

            global.ErrorUtils.setGlobalHandler(defaultJsErrorHandler);
            console.error = (message, error) => global.ErrorUtils.reportError(error); // sending console.error so that it can be caught

            //handle the native exception
            RNKitModule.setHandlerforNativeException((errMsg: string) => {
                defaultReporter(errMsg);
            })
        } else {
            console.log("Skipping init: Reason: In DEV mode and allowedInDevMode = false");
        }
    }
}




