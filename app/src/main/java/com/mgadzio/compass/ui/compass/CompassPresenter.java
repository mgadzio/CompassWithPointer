package com.mgadzio.compass.ui.compass;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.mgadzio.compass.ui.base.BasePresenter;
import com.mgadzio.compass.utils.LocationUtils;

import static android.content.Context.LOCATION_SERVICE;

public class CompassPresenter extends BasePresenter<CompassActivity> implements SensorEventListener {

    interface ViewCompass {
        void rotateCompass(float currentDegree, float targetDegree);

        void rotatePointer(float currentDegree, float targetDegree);

        void updateButtons(@Nullable String latitude, @Nullable String longitude);

        void showDialogLatitude();

        void showDialogLongitude();

        void showMsgAppClosing();
    }

    private static final int REQUEST_CODE_PERMISSION = 1;

    private Context context;
    private SensorManager sensorManager;
    private float currentDegreeCompass = 0f, currentDegreePointer = 0f;
    private double wantedLatitude, wantedLongitude;
    private boolean latitudeSet = false, longitudeSet = false;
    private Location lastKnownLocation, wantedLocation;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float targetDegree = Math.round(sensorEvent.values[0]);
        //avoid small sensor changes
        if (Math.abs(currentDegreeCompass - targetDegree) < 1) {
            return;
        }

        getView().rotateCompass(currentDegreeCompass, targetDegree);

        currentDegreeCompass = targetDegree;
        updatePointer();
    }

    protected void initLocation() {
        if (ActivityCompat.checkSelfPermission(getView(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            getView().showMsgAppClosing();
            getView().finish();
            return;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, mLocationListener);
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
//            lastKnownLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //not used
        }

        @Override
        public void onProviderEnabled(String s) {
            //not used
        }

        @Override
        public void onProviderDisabled(String s) {
            //not used
        }
    };


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not used
    }

    @Override
    public void bindView(CompassActivity view) {
        super.bindView(view);
        context = getView().getApplicationContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        checkLocationPermission();
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }


    protected void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getView(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getView(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getView(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
            }
        } else {
            initLocation();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initLocation();
        } else {
            getView().showMsgAppClosing();
            getView().finish();
        }
    }

    public void onButtonLatitudeClicked() {
        getView().showDialogLatitude();
    }

    public void onButtonLongitudeClicked() {
        getView().showDialogLongitude();
    }

    public void onLatitudePassed(double latitude) {
        this.wantedLatitude = latitude;
        latitudeSet = true;
        getView().updateButtons(String.valueOf(latitude), null);
        updateWantedLocation();
    }

    public void onLongitudePassed(double longitude) {
        this.wantedLongitude = longitude;
        longitudeSet = true;
        getView().updateButtons(null, String.valueOf(longitude));
        updateWantedLocation();
    }

    protected void updateWantedLocation() {
        if (latitudeSet && longitudeSet) {
            wantedLocation = new Location("");
            wantedLocation.setLatitude(wantedLatitude);
            wantedLocation.setLongitude(wantedLongitude);

            updatePointer();
        }
    }

    protected void updatePointer() {
        if (lastKnownLocation != null && wantedLocation != null) {
            int angle = LocationUtils.calcuateAngleBetweenLocations(lastKnownLocation, wantedLocation);
            float targetPointerAngle = currentDegreeCompass + angle;
            getView().rotatePointer(currentDegreePointer, targetPointerAngle);
            currentDegreePointer = targetPointerAngle;
        }

    }
}
