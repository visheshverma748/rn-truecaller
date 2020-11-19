package com.truecaller.module;

import android.app.Activity;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueException;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;
import com.truecaller.android.sdk.clients.VerificationCallback;
import com.truecaller.android.sdk.clients.VerificationDataBundle;
import static com.truecaller.android.sdk.clients.VerificationDataBundle.KEY_OTP;

public class TruecallerAuthModule extends ReactContextBaseJavaModule {

    private Promise promise = null;

    private final ITrueCallback sdkCallback = new ITrueCallback() {
        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {

            if (promise != null) {
                WritableMap map = Arguments.createMap();
                map.putBoolean("successful", true);
                map.putString("firstName", trueProfile.firstName);
                map.putString("lastName", trueProfile.lastName);
                map.putString("phoneNumber", trueProfile.phoneNumber);
                map.putString("gender", trueProfile.gender);
                map.putString("street", trueProfile.street);
                map.putString("city", trueProfile.city);
                map.putString("zipcode", trueProfile.zipcode);
                map.putString("countryCode", trueProfile.countryCode);
                map.putString("facebookId", trueProfile.facebookId);
                map.putString("twitterId", trueProfile.twitterId);
                map.putString("email", trueProfile.email);
                map.putString("url", trueProfile.url);
                map.putString("avatarUrl", trueProfile.avatarUrl);
                map.putBoolean("isVerified", trueProfile.isTrueName);
                map.putBoolean("isAmbassador", trueProfile.isAmbassador);
                map.putString("companyName", trueProfile.companyName);
                map.putString("jobTitle", trueProfile.jobTitle);
                map.putString("payload", trueProfile.payload);
                map.putString("signature", trueProfile.signature);
                map.putString("signatureAlgorithm", trueProfile.signatureAlgorithm);
                map.putString("requestNonce", trueProfile.requestNonce);
                promise.resolve(map);
            }
        }

        @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
            Log.d("TruecallerAuthModule", Integer.toString(trueError.getErrorType()));
            if (promise != null) {
                String errorReason = null;
                switch (trueError.getErrorType()) {
                    case TrueError.ERROR_TYPE_INTERNAL:
                        errorReason = "ERROR_TYPE_INTERNAL";
                        break;
                    case TrueError.ERROR_TYPE_NETWORK:
                        errorReason = "ERROR_TYPE_NETWORK";
                        break;
                    case TrueError.ERROR_TYPE_USER_DENIED:
                        errorReason = "ERROR_TYPE_USER_DENIED";
                        break;
                    case TrueError.ERROR_PROFILE_NOT_FOUND:
                        errorReason = "ERROR_TYPE_UNAUTHORIZED_PARTNER";
                        break;
                    case TrueError.ERROR_TYPE_UNAUTHORIZED_USER:
                        errorReason = "ERROR_TYPE_UNAUTHORIZED_USER";
                        break;
                    case TrueError.ERROR_TYPE_TRUECALLER_CLOSED_UNEXPECTEDLY:
                        errorReason = "ERROR_TYPE_TRUECALLER_CLOSED_UNEXPECTEDLY";
                        break;
                    case TrueError.ERROR_TYPE_TRUESDK_TOO_OLD:
                        errorReason = "ERROR_TYPE_TRUESDK_TOO_OLD";
                        break;
                    case TrueError.ERROR_TYPE_POSSIBLE_REQ_CODE_COLLISION:
                        errorReason = "ERROR_TYPE_POSSIBLE_REQ_CODE_COLLISION";
                        break;
                    case TrueError.ERROR_TYPE_RESPONSE_SIGNATURE_MISMATCH:
                        errorReason = "ERROR_TYPE_RESPONSE_SIGNATURE_MISSMATCH";
                        break;
                    case TrueError.ERROR_TYPE_REQUEST_NONCE_MISMATCH:
                        errorReason = "ERROR_TYPE_REQUEST_NONCE_MISSMATCH";
                        break;
                    case TrueError.ERROR_TYPE_INVALID_ACCOUNT_STATE:
                        errorReason = "ERROR_TYPE_INVALID_ACCOUNT_STATE";
                        break;
                    case TrueError.ERROR_TYPE_TC_NOT_INSTALLED:
                        errorReason = "ERROR_TYPE_TC_NOT_INSTALLED";
                        break;
                }
                WritableMap map = Arguments.createMap();
                map.putString("error", errorReason != null ? errorReason : "ERROR_TYPE_NULL");
                promise.resolve(map);
            }
        }

        @Override
        public void onVerificationRequired() {
        //The statement below can be ignored incase of One-tap flow integration
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TruecallerSDK.getInstance().requestVerification("IN", "PHONE-NUMBER-STRING", apiCallback, (FragmentActivity) getCurrentActivity());
                }
            });
        }
    };

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private void onConnected(String session) {
        // Create map for params
        WritableMap params = Arguments.createMap();
        // Put data to map
        params.putString("event", session);
        // Get EventEmitter from context and send event thanks to it
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onSessionConnect", params);
    }


//Callback below can be ignored incase of One-tap only integration

    final VerificationCallback apiCallback = new VerificationCallback() {
        @Override
        public void onRequestSuccess(int requestCode, @Nullable VerificationDataBundle extras) {
            if (requestCode == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
            }
            if (requestCode == VerificationCallback.TYPE_MISSED_CALL_RECEIVED) {
                TrueProfile profile = new TrueProfile.Builder("USER-FIRST-NAME", "USER-LAST-NAME").build();
                TruecallerSDK.getInstance().verifyMissedCall(profile, apiCallback);
            }
            if (requestCode == VerificationCallback.TYPE_OTP_INITIATED) {
            }
            if (requestCode == VerificationCallback.TYPE_OTP_RECEIVED) {
                TrueProfile profile = new TrueProfile.Builder("USER-FIRST-NAME", "USER-LAST-NAME").build();
                TruecallerSDK.getInstance().verifyOtp(profile, "OTP-ENTERED-BY-THE-USER", apiCallback);
            }
            if (requestCode == VerificationCallback.TYPE_VERIFICATION_COMPLETE) {
            }
            if (requestCode == VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE) {
            }
        }

        @Override
        public void onRequestFailure(final int requestCode, @NonNull final TrueException e) {
            //Write the Exception Part
            System.out.println("Error from exception");
        }
    };

    TruecallerAuthModule(ReactApplicationContext reactContext) {
        super(reactContext);
        TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(reactContext, sdkCallback)
                .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
                .buttonColor(Color.parseColor("COLOUR-STRING/HEX-CODE"))
                .buttonTextColor(Color.parseColor("COLOUR-STRING/HEX-CODE"))
                .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
                .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
                .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
                .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
                .privacyPolicyUrl("<YOUR-PRIVACY-POLICY-URL>")
                .termsOfServiceUrl("<YOUR-TERMS-OF-SERVICE-URL>")
                .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
                .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
                .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
                .build();
        TruecallerSDK.init(trueScope);
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "TruecallerAuthModule";
    }

    @ReactMethod
    public void showToast(Promise promise) {
        Toast.makeText(getReactApplicationContext(), "hello", Toast.LENGTH_LONG).show();
        promise.resolve(null);
    }

    @ReactMethod
    public void authenticate(Promise promise) {
        try {
            this.promise = promise;
            if (TruecallerSDK.getInstance() != null) {
                TruecallerSDK.getInstance().getUserProfile((FragmentActivity) getCurrentActivity());
            } else {
                WritableMap map = Arguments.createMap();
                map.putString("error", "ERROR_TYPE_NOT_SUPPORTED");
                this.promise.resolve(map);
            }
        } catch (Exception e) {
            this.promise.reject(e);
        }
    }

    @ReactMethod
    public void Verify(String PhoneNumber){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(PhoneNumber);
                TruecallerSDK.getInstance().requestVerification("IN", PhoneNumber, apiCallback, (FragmentActivity) getCurrentActivity() );
            }
        });
    }

    @ReactMethod
    public void verifyComplete(String firstName,String lastName){
        TrueProfile profile = new TrueProfile.Builder(firstName, lastName).build();
        TruecallerSDK.getInstance().verifyMissedCall(profile, apiCallback);
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(activity, requestCode, resultCode, intent);
            if (requestCode == 100) {
                TruecallerSDK.getInstance().onActivityResultObtained((FragmentActivity) activity, resultCode, intent);
            }
        }
    };

    @ReactMethod
    private void initiate(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Authenticate: ","Initiate is working fine ");
                TruecallerSDK.getInstance().getUserProfile((FragmentActivity) getCurrentActivity());
            }
        });
    }

}