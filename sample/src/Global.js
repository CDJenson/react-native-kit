import {RNKitExceptionHandler} from "react-native-kit";

RNKitExceptionHandler.init(true,(errMsg)=>{
    console.log('zjd ------ errMsg',errMsg)
});