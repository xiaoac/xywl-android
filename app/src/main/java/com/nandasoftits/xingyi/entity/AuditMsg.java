package com.nandasoftits.xingyi.entity;

import android.graphics.Bitmap;

public class AuditMsg {

    private Location shipLocation;

    private Integer tonnage;

    private Long time;

    private Bitmap emptyShipImg;

    private Bitmap referenceImg;

    public Location getShipLocation() {
        return shipLocation;
    }

    public void setShipLocation(Location shipLocation) {
        this.shipLocation = shipLocation;
    }

    public int getTonnage() {
        return tonnage;
    }

    public void setTonnage(int tonnage) {
        this.tonnage = tonnage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Bitmap getEmptyShipImg() {
        return emptyShipImg;
    }

    public void setEmptyShipImg(Bitmap emptyShipImg) {
        this.emptyShipImg = emptyShipImg;
    }

    public Bitmap getReferenceImg() {
        return referenceImg;
    }

    public void setReferenceImg(Bitmap referenceImg) {
        this.referenceImg = referenceImg;
    }
}
