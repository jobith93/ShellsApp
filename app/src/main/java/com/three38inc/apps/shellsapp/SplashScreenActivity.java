package com.three38inc.apps.shellsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

import me.wangyuwei.particleview.ParticleView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ParticleView mParticleView = (ParticleView) findViewById(R.id.pv);

        final ImageView imageView = (ImageView) findViewById(R.id.kjcLogo);
        Glide.with(this).load(getString(R.string.KJCLogo)).fitCenter().into(imageView);



        mParticleView.startAnim();

        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }, 1000);
            }
        });
    }
}
