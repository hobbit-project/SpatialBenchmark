/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark;

import com.vividsolutions.jts.geom.Coordinate;


/**
 *
 * @author jsaveta
 */
public class AreaPoint {

    private double longitude;
    private double latitude;

    public AreaPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Coordinate getTracePoint() {
        return new Coordinate(this.longitude, this.latitude);
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(double l) {
        this.longitude = l;
    }

    public void setLatitude(double l) {
        this.latitude = l;
    }
}
