package com.droidcon.it.hackaton.cooltivate;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kratorius.circleprogress.CircleProgressView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.relayr.RelayrSdk;
import io.relayr.model.DeviceModel;
import io.relayr.model.Reading;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import io.relayr.sensors.RelayrSdkInitializer;
import io.snapback.sdk.gesture.sequence.adapter.BlowSensorAdapter;
import io.snapback.sdk.gesture.sequence.adapter.ProximitySensorAdapter;
import io.snapback.sdk.gesture.sequence.adapter.ShakeSensorAdapter;
import io.snapback.sdk.gesture.sequence.pulse.PulseGestureEvent;
import io.snapback.sdk.gesture.sequence.pulse.PulseGestureHandler;
import io.snapback.sdk.gesture.sequence.pulse.PulseGestureListener;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {


    @ViewById
    WebView webView;
    @ViewById
    CircleProgressView temperature;
    @ViewById
    CircleProgressView humidity;
    @ViewById
    CircleProgressView lux;

    TransmitterDevice mDevice;
    Subscription mTemperatureDeviceSubscription;
    Subscription mLightDeviceSubscription;

    private PulseGestureHandler pulseBlowHandler;
    private PulseGestureHandler pulseShakeHandler;
    private PulseGestureHandler pulseProximityHandler;
    public static final Integer MAX_LIGHT_VALUE = 10;
    private Integer lightValue=0;

    @AfterViews
    void onCreate() {
        String userInfoTrackingDetail = "samuele.veneruso@gmail.com";

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Resources res = getResources();

        initializeBlowHandler(res, userInfoTrackingDetail);
        initializeShakeHandler(res, userInfoTrackingDetail);
        initializeProximityHandler(res, userInfoTrackingDetail);
        RelayrSdkInitializer.initSdk(this);
        if (RelayrSdk.isUserLoggedIn()) {
            loadUserInfo();
        } else {
            logIn();
        }
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


    private void logIn() {
        RelayrSdk.logIn(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,
                                R.string.unsuccessfully_logged_in, Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onNext(User user) {
                        startListeningAndInteract(user);
                    }
                });
    }

    private void startListeningAndInteract(User user) {
        Toast.makeText(MainActivity.this,
                R.string.successfully_logged_in, Toast.LENGTH_SHORT).show();

        loadWebView();
        loadDevices(user);
    }

    private void loadUserInfo() {
        RelayrSdk.getRelayrApi()
                .getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(User user) {
                        startListeningAndInteract(user);
                    }
                });
    }



    public void loadWebView() {
        webView.loadUrl("http://192.168.43.1:8080/browserfs.html");
        webView.setInitialScale(100);
        webView.setBackgroundColor(Color.BLACK);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollbarOverlay(false);
    }


    private void loadDevices(User user) {
        RelayrSdk.getRelayrApi()
                .getTransmitters(user.id)
                .flatMap(new Func1<List<Transmitter>, Observable<List<TransmitterDevice>>>() {
                    @Override
                    public Observable<List<TransmitterDevice>> call(List<Transmitter> transmitters) {
                        if (transmitters.isEmpty())
                            return Observable.from(new ArrayList<List<TransmitterDevice>>());
                        return RelayrSdk.getRelayrApi().getTransmitterDevices(transmitters.get(0).id);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<TransmitterDevice>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, R.string.something_went_wrong,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<TransmitterDevice> devices) {
                        for (TransmitterDevice device : devices) {
                            if (device.model.equals(DeviceModel.TEMPERATURE_HUMIDITY.getId())) {
                                subscribeForTemperatureUpdates(device);
                            } else if (device.model.equals(DeviceModel.LIGHT_PROX_COLOR.getId())) {
                                subscribeForLightUpdates(device);
                            }
                        }
                    }
                });
    }

    private void subscribeForTemperatureUpdates(TransmitterDevice device) {
        mDevice = device;
        mTemperatureDeviceSubscription = RelayrSdk.getWebSocketClient().subscribe(mDevice)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Reading>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, R.string.something_went_wrong,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    @UiThread
                    public void onNext(Reading reading) {
                        if (reading.meaning.equals("temperature")) {
                            Float aFloat = Float.valueOf(reading.value.toString());
                            temperature.setText(String.format("%.1f", aFloat)+"°");
                            temperature.setValue(aFloat.intValue());
                        } else if (reading.meaning.equals("humidity")) {
                            Float aFloat = Float.valueOf(reading.value.toString());
                            humidity.setText(String.format("%.1f", aFloat) + "%");
                            humidity.setValue(aFloat.intValue());
                        }
                    }
                });
    }


    private void subscribeForLightUpdates(TransmitterDevice device) {
        mDevice = device;
        mLightDeviceSubscription = RelayrSdk.getWebSocketClient().subscribe(mDevice)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Reading>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, R.string.something_went_wrong,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    @UiThread
                    public void onNext(Reading reading) {
                        if (reading.meaning.equals("luminosity")) {
                            Float floatPercentLum = Float.parseFloat(reading.value.toString()) * 100 / 4096;
                            lux.setText(String.format("%.1f", floatPercentLum)+ "%");
                            lux.setValue(floatPercentLum.intValue());
                        }
                    }
                });
    }






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
//                Log.i("pulsestart", "pulsestart");
            }
        }
    }

    private class ShakeListener implements PulseGestureListener {

        @Override
        public void onEvent(PulseGestureEvent event) {
            if(event.getType() == PulseGestureEvent.PULSE_HOLD_EVENT_TYPE) {
//                Log.i("pulsestop", "pulstop");
            }
        }
    }

    private class ProximityListener implements PulseGestureListener {

        @Override
        public void onEvent(PulseGestureEvent event) {
            if(event.getType() == PulseGestureEvent.PULSE_HOLD_EVENT_TYPE) {
                lightValue++;
                if(lightValue > MAX_LIGHT_VALUE) {
                    lightValue = 0;
                }
//                Log.i("lightvalue", "lightValue: " + lightValue);
            }
        }
    }


}
