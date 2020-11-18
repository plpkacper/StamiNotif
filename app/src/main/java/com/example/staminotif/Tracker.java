package com.example.staminotif;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class Tracker implements Parcelable {

    private int currSta;
    private int maxSta;
    private int recharge;
    private int modulo;
    private String name;
    public Timer timer;
    public boolean atMax;
    public String imageResource;
    public boolean favourite;
    public boolean maxSent;

    Tracker(String name, int currSta, int maxSta, int recharge, String imageResource) {
        this.name = name;
        this.currSta = currSta;
        this.maxSta = maxSta;
        this.recharge = recharge;
        this.modulo = 0;
        this.timer = new Timer();
        this.imageResource = imageResource;
        if (this.currSta == this.maxSta) {
            this.atMax = true;
        }
        else {
            this.atMax = false;
        }
        this.favourite = false;
    }

    Tracker(String name, int currSta, int maxSta, int recharge) {
        this.name = name;
        this.currSta = currSta;
        this.maxSta = maxSta;
        this.recharge = recharge;
        this.modulo = 0;
        this.timer = new Timer();
        this.imageResource = "";
        if (this.currSta == this.maxSta) {
            this.atMax = true;
        }
        else {
            this.atMax = false;
        }
        this.favourite = false;
    }

    Tracker(String name, int currSta, int maxSta, int recharge, String imageResource, boolean favourite) {
        this.name = name;
        this.currSta = currSta;
        this.maxSta = maxSta;
        this.recharge = recharge;
        this.modulo = 0;
        this.timer = new Timer();
        this.imageResource = imageResource;
        if (this.currSta == this.maxSta) {
            this.atMax = true;
        }
        else {
            this.atMax = false;
        }
        this.favourite = favourite;
    }

    Tracker(String name, int currSta, int maxSta, int recharge, boolean favourite) {
        this.name = name;
        this.currSta = currSta;
        this.maxSta = maxSta;
        this.recharge = recharge;
        this.modulo = 0;
        this.timer = new Timer();
        this.imageResource = "";
        if (this.currSta == this.maxSta) {
            this.atMax = true;
        }
        else {
            this.atMax = false;
        }
        this.favourite = favourite;
    }

    public void decrementSta1() {
        if (this.currSta - 1 < 0) {
            this.currSta = 0;
        }
        else {
            this.currSta--;
        }
        if (this.atMax) {
            this.timer.updateDate();
        }
        this.atMax = false;
    }
    public void decrementSta5() {
        if (this.currSta - 5 < 0) {
            this.currSta = 0;
        }
        else {
            this.currSta -= 5;
        }
        if (this.atMax) {
            this.timer.updateDate();
        }
        this.atMax = false;
    }
    public void decrementSta10() {
        if (this.currSta - 10 < 0) {
            this.currSta = 0;
        }
        else {
            this.currSta -= 10;
        }
        if (this.atMax) {
            this.timer.updateDate();
        }
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
    public String getImageResource() { return imageResource; }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setImageResource(String imageResource) { this.imageResource = imageResource; }
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
                ", atMax=" + atMax +
                ", imageResource='" + imageResource + '\'' +
                ", favourite=" + favourite +
                '}';
    }

    public void updateCounter() {
        if (!this.atMax) {
            int diff = timer.getDifference() + this.modulo;
            int toAdd = (int)Math.floor(diff/recharge);
            if (toAdd >= 1) {
                if (this.currSta + toAdd >= this.maxSta) {
                    this.currSta = this.maxSta;
                    this.atMax = true;
                    //Send notification if full
                }
                else {
                    this.currSta += (int)Math.floor(diff/this.recharge);
                    this.modulo = diff % recharge;
                    this.timer.updateDate();
                    //Log.d("stamina", "Stamina updated to: " + this.currSta);
                }
            }
        }
        else {
            timer.updateDate();
        }
    }

    protected Tracker(Parcel in) {
        currSta = in.readInt();
        maxSta = in.readInt();
        recharge = in.readInt();
        modulo = in.readInt();
        name = in.readString();
        timer = (Timer) in.readValue(Timer.class.getClassLoader());
        atMax = in.readByte() != 0x00;
        imageResource = in.readString();
        favourite = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(currSta);
        dest.writeInt(maxSta);
        dest.writeInt(recharge);
        dest.writeInt(modulo);
        dest.writeString(name);
        dest.writeValue(timer);
        dest.writeByte((byte) (atMax ? 0x01 : 0x00));
        dest.writeString(imageResource);
        dest.writeByte((byte) (favourite ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tracker> CREATOR = new Parcelable.Creator<Tracker>() {
        @Override
        public Tracker createFromParcel(Parcel in) {
            return new Tracker(in);
        }

        @Override
        public Tracker[] newArray(int size) {
            return new Tracker[size];
        }
    };
}