package com.droidcon.it.hackaton.cooltivate;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;

import io.relayr.RelayrSdk;
import io.relayr.model.User;
import io.relayr.sensors.RelayrSdkInitializer;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

@EActivity(R.layout.activity_main)
public class LoginActivity extends Activity {


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
                        Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this,
                                R.string.unsuccessfully_logged_in, Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onNext(User user) {
                        Toast.makeText(LoginActivity.this,
                                R.string.unsuccessfully_logged_in, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
