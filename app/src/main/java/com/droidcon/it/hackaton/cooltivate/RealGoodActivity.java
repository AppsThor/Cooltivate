package com.droidcon.it.hackaton.cooltivate;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import io.snapback.sdk.gesture.sequence.adapter.BlowSensorAdapter;
import io.snapback.sdk.gesture.sequence.adapter.ProximitySensorAdapter;
import io.snapback.sdk.gesture.sequence.adapter.ShakeSensorAdapter;
import io.snapback.sdk.gesture.sequence.pulse.PulseGestureEvent;
import io.snapback.sdk.gesture.sequence.pulse.PulseGestureHandler;
import io.snapback.sdk.gesture.sequence.pulse.PulseGestureListener;

/**
 * Created by Samuele on 4/11/15.
 */
public class RealGoodActivity extends Activity{

    public static final String VENTILATORE_ACCESO = "VENTILATORE ACCESO? ";
    public static final String IRRIGAZIONE_APERTA = "IRRIGAZIONE APERTA? ";
    public static final String LIVELLO_LUCE = "LIVELLO LUCE ";
    private TextView fanTextView;
    private TextView waterTextView;
    private TextView lightTextView;

    private boolean fanStatus = false;
    private boolean waterStatus = false;
    private Integer lightValue = 0;


    //SnapBack START
    public static final Integer MAX_LIGHT_VALUE = 100;

    private PulseGestureHandler pulseBlowHandler;
    private PulseGestureHandler pulseShakeHandler;
    private PulseGestureHandler pulseProximityHandler;
    //SnapBack END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realgood);

        fanTextView = (TextView) findViewById(R.id.fanTextView);
        waterTextView = (TextView) findViewById(R.id.waterTextView);
        lightTextView = (TextView) findViewById(R.id.lightTextView);

        Resources res = getResources();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //SnapBack START
        String userInfoTrackingDetail = "samuele.veneruso@gmail.com";

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initializeBlowHandler(res, userInfoTrackingDetail);
        initializeShakeHandler(res, userInfoTrackingDetail);
        initializeProximityHandler(res, userInfoTrackingDetail);

        //SnapBack END

        changeText(fanTextView, VENTILATORE_ACCESO + fanStatus);
        changeText(waterTextView, IRRIGAZIONE_APERTA + fanStatus);
        changeText(lightTextView, LIVELLO_LUCE + lightValue);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pulseBlowHandler.start();
        pulseShakeHandler.start();
        pulseProximityHandler.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pulseBlowHandler.stop();
        pulseShakeHandler.stop();
        pulseProximityHandler.stop();
    }

    private void changeText(final TextView textView, final String txt) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                textView.setText(txt);
            }

        });
    }

    //SnapBack START

    private void initializeProximityHandler(Resources res, String userInfoTrackingDetail) {
        pulseProximityHandler = new PulseGestureHandler(this);
        pulseProximityHandler.setUsageTrackingDetails(userInfoTrackingDetail, res.getString(R.string.app_name));
        ProximitySensorAdapter proximitySensorAdapter = new ProximitySensorAdapter(pulseProximityHandler, this);
        pulseProximityHandler.setSensorAdapters(proximitySensorAdapter);
        pulseProximityHandler.register(new ProximityListener());
    }

    private void initializeShakeHandler(Resources res, String userInfoTrackingDetail) {
        pulseShakeHandler = new PulseGestureHandler(this);
        pulseShakeHandler.setUsageTrackingDetails(userInfoTrackingDetail, res.getString(R.string.app_name));
        ShakeSensorAdapter shakeSensorAdapter = new ShakeSensorAdapter(pulseShakeHandler, this);
        pulseShakeHandler.setSensorAdapters(shakeSensorAdapter);
        pulseShakeHandler.register(new ShakeListener());
    }

    private void initializeBlowHandler(Resources res, String userInfoTrackingDetail) {
        pulseBlowHandler = new PulseGestureHandler(this);
        pulseBlowHandler.setUsageTrackingDetails(userInfoTrackingDetail, res.getString(R.string.app_name));
        BlowSensorAdapter blowSensorAdapter = new BlowSensorAdapter(pulseBlowHandler, this);
        pulseBlowHandler.setSensorAdapters(blowSensorAdapter);
        pulseBlowHandler.register(new BlowListener());
    }


    private class BlowListener implements PulseGestureListener {

        @Override
        public void onEvent(PulseGestureEvent event) {
            if(event.getType() == PulseGestureEvent.PULSE_START_EVENT_TYPE) {
                fanStatus = !fanStatus;
                changeText(fanTextView, VENTILATORE_ACCESO + fanStatus);
            }
        }
    }

    private class ShakeListener implements PulseGestureListener {

        @Override
        public void onEvent(PulseGestureEvent event) {
            if(event.getType() == PulseGestureEvent.PULSE_HOLD_EVENT_TYPE) {
                waterStatus = !waterStatus;
                changeText(waterTextView, IRRIGAZIONE_APERTA + waterStatus);
            }
        }
    }

    private class ProximityListener implements PulseGestureListener {

        @Override
        public void onEvent(PulseGestureEvent event) {
            if(event.getType() == PulseGestureEvent.PULSE_HOLD_EVENT_TYPE) {
                lightValue++;
                changeText(lightTextView, LIVELLO_LUCE + lightValue);
            }
        }
    }


    //SnapBack END

}
