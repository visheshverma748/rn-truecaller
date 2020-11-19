import {NativeModules,NativeEventEmitter,Platform} from 'react-native';
//const EventEmitter = new NativeEventEmitter(Platform.OS === "ios" ? NativeModules.Truecaller : NativeModules.TruecallerAuthModule);
//const Truecaller =Platform.OS === "ios" ? NativeModules.Truecaller : NativeModules.TruecallerAuthModule;

const EventEmitter = new NativeEventEmitter(NativeModules.TruecallerAuthModule);
const Truecaller = NativeModules.TruecallerAuthModule;

const RNTruecaller = {};
export const RNTruecallerEvent =
    {
        TrueProfileResponse: 'didReceiveTrueProfileResponse',
        TrueProfileResponseError: 'didReceiveTrueProfileResponseError',
    };

RNTruecaller.isTruecallerInstalled = () => {
    return Truecaller.isTruecallerInstalled();
};

RNTruecaller.initializeClient = () =>
{
    return Truecaller.initializeClient();
};

RNTruecaller.initializeClientIOS = (appKey,appLink) =>
{
    return Truecaller.initializeClientIOS(appKey,appLink);
};

RNTruecaller.requestTrueProfile = () =>
{
    return Truecaller.requestTrueProfile();
};

RNTruecaller.on = (event, callback) =>
{
    if (!Object.values(RNTruecallerEvent).includes(event)) {
        throw new Error(`Invalid RNTruecallerEvent event subscription, use import {RNTruecallerEvent} from 'rn-truecaller' to avoid typo`);
    }
    return EventEmitter.addListener(event, callback);
};

export default RNTruecaller;
