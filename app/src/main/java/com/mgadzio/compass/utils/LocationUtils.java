package com.mgadzio.compass.utils;

import android.location.Location;

public class LocationUtils {

    public static int calcuateAngleBetweenLocations(Location location1, Location location2) {

        double lat1 = location1.getLatitude(),
                long1 = location1.getLongitude(),
                lat2 = location2.getLatitude(),
                long2 = location2.getLongitude();

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return (int) brng;
    }
}
