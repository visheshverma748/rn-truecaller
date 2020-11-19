import {NativeModules} from 'react-native';

const {RNPaytmCustomuiSdk} = NativeModules;

class TruecallerSDK {

    static isPaytmAppInstalled() {
        return RNPaytmCustomuiSdk.isPaytmAppInstalled();
    }


}

export default RNTruecaller;
