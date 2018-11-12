package com.mobi.mobilitapp.Capture;

/**
 * Created by rokslamek on 04/04/17.
 */

public class SensorSample {

    long timestamp;
    float x;
    float y;
    float z;

    public SensorSample(long timestamp, float x, float y, float z) {

        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
