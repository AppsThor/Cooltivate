package com.droidcon.it.hackaton.cooltivate;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
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
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

//    @ViewById
//    WebView webView;
    @ViewById
    TextView temperature;
    @ViewById
    TextView humidity;
    @ViewById
    TextView lux;
    private TransmitterDevice mDevice;
    private Subscription mUserInfoSubscription;
    private Subscription mTemperatureDeviceSubscription;

    @AfterViews
    public void loadWebView() {
//        temperature.setText("+25° C");
//        humidity.setText("70%");
//        lux.setText("100 nit");
//
//        webView.loadUrl("http://192.168.43.1:8080/browserfs.html");
//        webView.setInitialScale(100);
//        webView.setBackgroundColor(Color.BLACK);
//        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        webView.setVerticalScrollbarOverlay(false);
    }

    @AfterInject
    void onCreate() {
        RelayrSdkInitializer.initSdk(this);
        if (RelayrSdk.isUserLoggedIn()) {
            updateUiForALoggedInUser();
        } else {
//            updateUiForANonLoggedInUser();
            logIn();
        }
    }

    private void updateUiForALoggedInUser() {
        loadUserInfo();
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
                        loadDevices(user);
                    }
                });
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
                        Toast.makeText(MainActivity.this,
                                R.string.unsuccessfully_logged_in, Toast.LENGTH_SHORT).show();
                                loadDevices(user);

                    }
                });
    }

    private void loadDevices(User user) {
        RelayrSdk.getRelayrApi()
                .getTransmitters(user.id)
                .flatMap(new Func1<List<Transmitter>, Observable<List<TransmitterDevice>>>() {
                    @Override
                    public Observable<List<TransmitterDevice>> call(List<Transmitter> transmitters) {
                        // This is a naive implementation. Users may own many WunderBars or other
                        // kinds of transmitter.
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
                            } else
                            if (device.model.equals(DeviceModel.LIGHT_PROX_COLOR.getId())){
                                subscribeForLightUpdates(device);
                            }
                        }
                    }
                });
    }

    private void subscribeForTemperatureUpdates(TransmitterDevice device) {
        mDevice = device;
        RelayrSdk.getWebSocketClient().subscribe(mDevice)
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
                    public void onNext(Reading reading) {
                        if (reading.meaning.equals("temperature")) {
                            temperature.setText(reading.value + "˚ C");
                        } else if (reading.meaning.equals("humidity")) {
                            humidity.setText(reading.value + "%");
                        }
                    }
                });
    }





    private void subscribeForLightUpdates(TransmitterDevice device) {
        mDevice = device;
        RelayrSdk.getWebSocketClient().subscribe(mDevice)
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
                    public void onNext(Reading reading) {
                        if (reading.meaning.equals("luminosity")) {
                            lux.setText(reading.value + "cd");
                        }
                    }
                });
    }

}
