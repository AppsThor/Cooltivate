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

import io.relayr.RelayrSdk;
import io.relayr.model.User;
import io.relayr.sensors.RelayrSdkInitializer;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @ViewById
    WebView webView;
    @ViewById
    TextView temperature;
    @ViewById
    TextView humidity;
    @ViewById
    TextView lux;

    @AfterViews
    public void loadWebView() {
        temperature.setText("+25° C");
        humidity.setText("70%");
        lux.setText("100 nit");

        webView.loadUrl("http://192.168.43.1:8080/browserfs.html");
        webView.setInitialScale(100);
        webView.setBackgroundColor(Color.BLACK);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollbarOverlay(false);
    }

    @AfterInject
     void onCreate() {
        RelayrSdkInitializer.initSdk(this);
        if (!RelayrSdk.isUserLoggedIn()) {
            logIn();
        }
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
                    }
                });
    }

}
