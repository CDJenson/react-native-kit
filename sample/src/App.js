import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, PermissionsAndroid, Image} from 'react-native';
import {RNKitUpdate,RNKitLog} from "react-native-kit";

const fetchService = (url, options = {}) => {
    return fetch(url, options)
        .then(response => {
            if (response.ok) {
                return response.json();
            }
        })
        .then(responseJson => {
            return responseJson;
        })
        .catch(error => {
        });
};

export default class App extends Component {

    async requestExternalStoragePermission() {
        try {
            const results = await PermissionsAndroid.requestMultiple(
                [PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE, PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE],
            );
            if (results[PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE] === 'granted' && results[PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE] === 'granted') {
                this.versionCheck()
            }
        } catch (e) {
        }
    }

    componentDidMount() {
        this.requestExternalStoragePermission();
    }

    versionCheck() {
        fetchService("http://172.30.80.34:8000/versionInfo.json",{}).then(response=>{
            RNKitLog.d(response);
            let data = response.data;
            if(RNKitUpdate.hasApkUpdate(data.appVersionCode)){
                RNKitUpdate.startApkUpdate(data.appVersionCode,data.apkUrl,data.releaseNotes);
            }else if(RNKitUpdate.hasPatchUpdate(data.bundleVersionCode)){
                RNKitUpdate.startPatchUpdate(data.bundleVersionCode,data.patchUrl,data.installType,data.releaseNotes);
            }
        }).catch(error=>{
            console.log('versionCheck', error)
        })
    }

    render() {
        return (
            <View style={styles.container}>
                <Text style={styles.welcome}>Welcome to React Native!</Text>
                <Text style={styles.instructions}>To get started, edit App.js</Text>
                <Text style={styles.instructions}>Update1 Version</Text>
                <Image source={require('./imgs/smile.png')}/>
                <Image source={require('./imgs/crying.png')}/>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});
