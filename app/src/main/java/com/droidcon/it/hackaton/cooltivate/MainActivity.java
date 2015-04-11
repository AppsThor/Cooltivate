package com.droidcon.it.hackaton.cooltivate;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

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

}
