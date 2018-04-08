package com.zippr.testapplication.models;

import java.io.Serializable;

/**
 * Created by aritrapal on 22/03/18.
 */

public class SelLocDO implements Serializable {
    public String locId = "";
    public String name = "";
    public int parcelCount = 0;
    public double locLat = 0;
    public double locLng = 0;

    public SelLocDO(String locId, String name, int parcelCount, double locLat, double locLng) {
        this.locId = locId;
        this.name = name;
        this.parcelCount = parcelCount;
        this.locLat = locLat;
        this.locLng = locLng;
    }
}
