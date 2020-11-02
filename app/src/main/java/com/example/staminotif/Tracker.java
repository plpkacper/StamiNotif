package com.example.staminotif;

import java.util.concurrent.atomic.AtomicInteger;

public class Tracker {

    private AtomicInteger currSta;
    private AtomicInteger maxSta;
    private AtomicInteger recharge;
    private AtomicInteger modulo;
    public Timer timer;

    private Tracker(AtomicInteger currSta, AtomicInteger maxSta, AtomicInteger recharge) {
        this.currSta = currSta;
        this.maxSta = maxSta;
        this.recharge = recharge;
        this.modulo = new AtomicInteger(0);
        this.timer = new Timer();
    }

    public void incrementSta() {
        this.currSta.incrementAndGet();
    }
    public void decrementSta1() {
        this.currSta.decrementAndGet();
    }
    public void decrementSta5() {
        this.currSta.addAndGet(-5);
    }
    public void decrementSta10() {
        this.currSta.addAndGet(-10);
    }

    public AtomicInteger getCurrSta() {
        return currSta;
    }
    public AtomicInteger getMaxSta() {
        return maxSta;
    }
    public AtomicInteger getRecharge() {
        return recharge;
    }
    public AtomicInteger getModulo() {
        return modulo;
    }
    public Timer getTimer() {return timer;}

    public void setCurrSta(AtomicInteger currSta) {
        this.currSta = currSta;
    }
    public void setMaxSta(AtomicInteger maxSta) {
        this.maxSta = maxSta;
    }
    public void setRecharge(AtomicInteger recharge) {
        this.recharge = recharge;
    }
    public void setModulo(AtomicInteger modulo) {
        this.modulo = modulo;
    }
    public void updateRecharge(AtomicInteger recharge) {
        this.recharge = recharge;
    }
    public void updateMaxSta(AtomicInteger maxSta) {
        this.maxSta = maxSta;
    }
}
