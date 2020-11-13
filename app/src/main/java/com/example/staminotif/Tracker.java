package com.example.staminotif;

import android.util.Log;

import androidx.annotation.LongDef;

public class Tracker {

    private int currSta;
    private int maxSta;
    private int recharge;
    private int modulo;
    private String name;
    public Timer timer;
    public boolean atMax;

    Tracker(String name, int currSta, int maxSta, int recharge) {
        this.name = name;
        this.currSta = currSta;
        this.maxSta = maxSta;
        this.recharge = recharge;
        this.modulo = 0;
        this.timer = new Timer();
        if (this.currSta == this.maxSta) {
            this.atMax = true;
        }
        else {
            this.atMax = false;
        }
    }

    public void decrementSta1() {
        this.currSta--;
        this.atMax = false;
    }
    public void decrementSta5() {
        this.currSta -= 5;
        this.atMax = false;
    }
    public void decrementSta10() {
        this.currSta -= 10;
        this.atMax = false;
    }

    public int getCurrSta() {
        return currSta;
    }
    public int getMaxSta() {
        return maxSta;
    }
    public int getRecharge() {
        return recharge;
    }
    public int getModulo() {
        return modulo;
    }
    public Timer getTimer() {return timer;}
    public String getName() { return this.name; }

    public void setCurrSta(int currSta) {
        this.currSta = currSta;
    }
    public void setMaxSta(int maxSta) {
        this.maxSta = maxSta;
    }
    public void setRecharge(int recharge) {
        this.recharge = recharge;
    }
    public void setModulo(int modulo) {
        this.modulo = modulo;
    }
    public void updateRecharge(int recharge) {
        this.recharge = recharge;
    }
    public void updateMaxSta(int maxSta) {
        this.maxSta = maxSta;
    }

    @Override
    public String toString() {
        return "Tracker{" +
                "currSta=" + currSta +
                ", maxSta=" + maxSta +
                ", recharge=" + recharge +
                ", modulo=" + modulo +
                ", name='" + name + '\'' +
                ", timer=" + timer +
                '}';
    }

    public void updateCounter() {
        if (!this.atMax) {
            int diff = timer.getDifference() + this.modulo;
            this.modulo = diff % recharge;
            int toAdd = (int)Math.floor(diff/recharge);
            if (this.currSta + toAdd >= this.maxSta) {
                this.currSta = this.maxSta;
                this.atMax = true;
                //Stamina is at max send notification
            }
            else {
                this.currSta += (int)Math.floor(diff/this.recharge);
                this.timer.updateDate();
                Log.d("stamina", "Stamina updated to: " + this.currSta);
            }
        }
    }
}
















