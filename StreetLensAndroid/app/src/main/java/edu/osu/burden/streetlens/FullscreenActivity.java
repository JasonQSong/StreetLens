package edu.osu.burden.streetlens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.hardware.Camera;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private FrameLayout mFrameLayoutCam;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private boolean mVisible;
    private double mAccelerator;

    private boolean FAKEDATA=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mFrameLayoutCam=(FrameLayout)findViewById(R.id.frame_layout_cam);



        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


//        mCamera=getCameraInstance();
//        mCameraPreview=new CameraPreview(this,mCamera);
//        mFrameLayoutCam.addView(mCameraPreview);
        InitOrientation();
        InitLocation();
        InitRetailMeNot();
        InitTimer();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //// CAMERA

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    ////ACCELERATOR
    private DeviceParameterModel mDeviceParameter=new DeviceParameterModel();

    private SensorManager sm;
    private Sensor accelerometerSensor;
    private Sensor magneticSensor;

    void InitOrientation(){
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor==accelerometerSensor){
                    System.arraycopy(event.values,0,mDeviceParameter.accelerator,0,event.values.length);
                    sm.getRotationMatrix(mDeviceParameter.R,null,mDeviceParameter.accelerator,mDeviceParameter.magnetor);
                    sm.getOrientation(mDeviceParameter.R,mDeviceParameter.orientation);
                    if(FAKEDATA){
                        mDeviceParameter.orientation[0]=0;
                        mDeviceParameter.orientation[1]=0;
                        mDeviceParameter.orientation[2]=(float)-1.57;
                    }
                    try {
                        Log.v("StreetLensAccelerator", mDeviceParameter.toJson().toString());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);

        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor==magneticSensor){
                    System.arraycopy(event.values, 0, mDeviceParameter.magnetor, 0, event.values.length);
                    sm.getRotationMatrix(mDeviceParameter.R, null, mDeviceParameter.accelerator, mDeviceParameter.magnetor);
                    sm.getOrientation(mDeviceParameter.R,mDeviceParameter.orientation);
                    if(FAKEDATA){
                        mDeviceParameter.orientation[0]=0;
                        mDeviceParameter.orientation[1]=0;
                        mDeviceParameter.orientation[2]=(float)-1.57;
                    }
                    try {
                        Log.v("StreetLensMagnetor", mDeviceParameter.toJson().toString());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    void InitLocation(){
        LocationManager lm=(LocationManager)this.getSystemService(LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        try {
                    LocationListener locationListener=new  LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(FAKEDATA){
                        location.setLatitude(40);
                        location.setLongitude(-83);
                    }
                    mDeviceParameter.location=location;
                    Log.w("StreetLensLocation",mDeviceParameter.location.getLatitude()+","+mDeviceParameter.location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }
    RetailMeNotInterface rmni;
    void InitRetailMeNot(){

        rmni=new RetailMeNotInterface(this);
        rmni.Fetch(mDeviceParameter.location,1);
    }
    private DeviceArgumentModel mDeviceArgumentModel=new DeviceArgumentModel();
    private Timer UITimer;
    void InitTimer(){
        //TODO
//        mDeviceArgumentModel.XPixelPerRad=100;
//        mDeviceArgumentModel.YPixelPerRad=100;
//        mDeviceArgumentModel.ScreenWidthPixel=480;
//        mDeviceArgumentModel.ScreenHeightPixel=320;

        UITimer=new Timer();
        UITimer.schedule(new TimerTask() {
            @Override
            public void run() {

                RefreshUI();
            }
        },1000,1000);
    }

    final double PI=3.1416;
    void RefreshUI(){
        rmni.Fetch(mDeviceParameter.location,2);
        if(rmni.stores==null)
            return;
        try {
            double[] ScreenXArray,ScreenYArray;
            String[] StoreNameArray,SubtitleArray;
            int len=rmni.stores.length();
            ScreenXArray=new double[len];
            ScreenYArray=new double[len];
            StoreNameArray=new String[len];
            SubtitleArray=new String[len];
            for (int i = 0; i < rmni.stores.length(); i++) {
                JSONObject store =(JSONObject) rmni.stores.get(i);
                Location storeLocation=new Location(LocationManager.GPS_PROVIDER);
                storeLocation.setLongitude(((JSONArray) store.get("loc")).getDouble(0));
                storeLocation.setLatitude(((JSONArray) store.get("loc")).getDouble(1));
                Log.d("DeviceLoc",mDeviceParameter.location.toString());
                Log.d("StoreLoc",storeLocation.toString());
                double storeAngle=mDeviceParameter.location.bearingTo(storeLocation);
                double screenXAngle=storeAngle-mDeviceParameter.orientation[0];
                double screenYAngle=-mDeviceParameter.orientation[2]-PI/2;
                double screenXPixel=screenXAngle*mDeviceArgumentModel.XPixelPerRad;
                double screenYPixel=screenYAngle*mDeviceArgumentModel.YPixelPerRad;
                String StoreName=store.get("name").toString();
                String Subtitle="";
                try {
                    Subtitle += ((JSONArray) store.get("offers")).length();
                }catch (JSONException e){
                    Log.d("StreetLensStore","No offers for "+StoreName);
                }
                Log.w("StreetLensStore" ,StoreName+"/"+storeAngle+"/"+Subtitle+"/"+screenXPixel+"/"+screenYPixel);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    ////THIS IS THE BEGIN OF DRAW FUNC
    void DrawTag(int screen_x, int screen_y,String StoreName, String Subtitle){

    }
    ////THIS IS THE END OF DRAW FUNC

}
