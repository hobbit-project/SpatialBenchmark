/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.util;

/**
 *
 * @author jsaveta
 */
public class CoordinatesUtil {

    private double latM;
    private double longM;

    public CoordinatesUtil() {
    }

    /**
     * Calculates the distance in km between two lat/long points using the
     * haversine formula
     * @param latA
     * @param longA
     * @param latB
     * @param longB
     * @return 
     */
    public double calculateDistanceInMeters(double latA, double longA, double latB, double longB) {
        //http://andrew.hedges.name/experiments/haversine/
        int r = 6371; // average radius of the earth in km

        double dLat = Math.toRadians(latB - latA);
        double dLon = Math.toRadians(longB - longA);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        System.out.println("d " + d * 1000);
        return d * 1000; //Distance in meters
    }

    public void createMiddlePoint(double la1, double lo1, double la2, double lo2) {
        double dLon = Math.toRadians(lo2 - lo1);
        //convert to radians
        double lat1 = Math.toRadians(la1);
        double lat2 = Math.toRadians(la2);
        double lon1 = Math.toRadians(lo1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        latM = Math.toDegrees(Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By)));
        longM = Math.toDegrees(lon1 + Math.atan2(By, Math.cos(lat1) + Bx));
        
    }

    public String getLatM() {
        return String.valueOf(this.latM);
    }

    public String getLongM() {
        return String.valueOf(this.longM);
    }
}
