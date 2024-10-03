package com.example.automation.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

public class TapAccessibilityService extends AccessibilityService {

    private Handler handler;
    private int x;
    private int y;
    private int min;
    private int max;
    private int counter;
    private int clickCounter;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("tap-handler");
        handlerThread.start();
        handler= new Handler(handlerThread.getLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String action = intent.getStringExtra("action");
            if (action.equals("run")){
                x = intent.getIntExtra("x", 0);
                y = intent.getIntExtra("y", 0);
                min = intent.getIntExtra("min", 0);
                max = intent.getIntExtra("max", 0);
                counter = intent.getIntExtra("counter", 0);

                clickCounter = 0;

                if(myRunnable == null){
                    myRunnable = new myRunnable();
                }
                handler.post(myRunnable);
            }else{
                handler.removeCallbacksAndMessages(null);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onInterrupt() {

    }

    private void sendClickCounterBroadcast() {
        Intent intent = new Intent("com.example.automation.CLICK_COUNTER");
        intent.putExtra("clickCounter", clickCounter);
        sendBroadcast(intent);
    }

    private void tap(int x, int y){
        Path swipePath = new Path();
        swipePath.moveTo(x,y);
        swipePath.lineTo(x,y);
        GestureDescription.Builder gBuilder = new GestureDescription.Builder();
        gBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath,0,1));
        dispatchGesture(gBuilder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);

                double randomTimer = min + (max - min) * Math.random();

                clickCounter ++;
                sendClickCounterBroadcast();
                if(clickCounter < counter){
                    handler.postDelayed(myRunnable, (int) randomTimer * 1000L);
                }
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
    }
    private myRunnable myRunnable;
    private class myRunnable implements Runnable{

        @Override
        public void run() {
            tap(x,y);
        }
    }
}