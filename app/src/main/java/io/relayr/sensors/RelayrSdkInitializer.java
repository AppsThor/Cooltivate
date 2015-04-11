package io.relayr.sensors;

import android.content.Context;

import io.relayr.RelayrSdk;

/**
 * Created by fditrani on 11/04/15.
 */
public class RelayrSdkInitializer {

    public static void initSdk(Context context) {
        new RelayrSdk.Builder(context).inMockMode(false).build();    }

}
