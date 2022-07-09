/**
 * PreprocessAndComputeFeatures Project
 * Created by
 * Phan Doan Cuong
 * Android Dev
 * VMGames Company
 * cuongphank58@gmail.com
 * 0989906612
 * on 01/03/2017.
 */
package com.example.myapplication;

public class SimpleAccelData {
    private double timestamp;
    private double x;
    private double y;
    private double z;

    public SimpleAccelData(String timestamp, String x, String y, String z) {
        this.timestamp = Double.parseDouble(timestamp);

        // change "," or "." by "" for x, y, z

        x = x.replace(",", "");
        this.x = Double.parseDouble(x);
        y = y.replace(",","");
        this.y = Double.parseDouble(y);
        z = z.replace(",", "");
        this.z = Double.parseDouble(z);

    }
    public SimpleAccelData(float timestamp, Double x, Double y, Double z) {
        this.timestamp = timestamp;

        // change "," by "." for x, y, z


        this.x = x;
        this.y = y;
        this.z = z;

    }


    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
