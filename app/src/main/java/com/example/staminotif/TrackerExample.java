package com.example.staminotif;

public class TrackerExample {

    private String name;
    private int currSta;
    private int recharge;
    private int maxSta;
    private String imageName;

    public TrackerExample(int recharge, String imageName, String name) {
        this.name = name;
        this.currSta = 0;
        this.recharge = recharge;
        this.maxSta = 0;
        this.imageName = imageName;
    }

    public int getRecharge() {
        return recharge;
    }

    public int getMaxSta() {
        return maxSta;
    }

    public String getName() {
        return name;
    }

    public String getImageName() {
        return imageName;
    }
}
