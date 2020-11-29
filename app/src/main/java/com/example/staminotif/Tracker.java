package com.example.staminotif;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//Sets up trackers table in trackers database
@Entity(tableName = "trackers")
public class Tracker implements Parcelable {

    //sets a primary key to an autogenerated field.
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int tID;

    //Initialising variables
    private int currSta;
    private int maxSta;
    private int recharge;
    private int modulo;
    private String name;
    public Timer timer;
    public boolean atMax;
    public String imageResource;
    public int imageId;
    public boolean favourite;
    public boolean maxSent;

    //Big generic instance creator.
    Tracker(int tID, String name, int currSta, int maxSta, int recharge, String imageResource, boolean favourite, int imageId) {
        this.tID = tID;
        this.name = name;
        this.currSta = currSta;
        this.maxSta = maxSta;
        this.recharge = recharge;
        this.modulo = 0;
        this.timer = new Timer();
        this.imageResource = imageResource;
        this.imageId = imageId;
        if (this.currSta == this.maxSta) {
            this.atMax = true;
        }
        else {
            this.atMax = false;
        }
        this.favourite = favourite;
    }

    //Decrementing stamina by certain values
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

    public void decrementStaValue(int value) {
        if (this.currSta - value < 0) {
            this.currSta = 0;
        }
        else {
            this.currSta -= value;
        }
        if (this.atMax) {
            this.timer.updateDate();
        }
        this.atMax = false;
    }

    //Getters and setters and a toString.
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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getTID() { return tID; }

    public void setTID(int tID) { this.tID = tID; }

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
                "tID=" + tID +
                ", currSta=" + currSta +
                ", maxSta=" + maxSta +
                ", recharge=" + recharge +
                ", modulo=" + modulo +
                ", name='" + name + '\'' +
                ", timer=" + timer +
                ", atMax=" + atMax +
                ", imageResource='" + imageResource + '\'' +
                ", imageId=" + imageId +
                ", favourite=" + favourite +
                ", maxSent=" + maxSent +
                '}';
    }

    //Updating the currsta and modulo variables
    public void updateCounter() {
        //If the tracker isn't at max
        if (!this.atMax) {
            //Get difference in time in minutes with the modulo added
            int diff = timer.getDifference() + this.modulo;
            //Gets an int amount of units that the stamina needs to be increased by.
            int toAdd = (int)Math.floor(diff/recharge);
            //If the amount to add isn't 0
            if (toAdd >= 1) {
                //if the addition will make the stamina full, set it to be full and set atMax to be true.
                if (this.currSta + toAdd >= this.maxSta) {
                    this.currSta = this.maxSta;
                    this.atMax = true;
                }
                //If the addition won't make the stamina bar full, add to the currsta variable.
                else {
                    this.currSta += (int)Math.floor(diff/this.recharge);
                    //Set the modulo to keep accurate track of the stamina and update the time.
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


    //Parcelable methods that aren't used anymore.
    protected Tracker(Parcel in) {
        currSta = in.readInt();
        maxSta = in.readInt();
        recharge = in.readInt();
        modulo = in.readInt();
        name = in.readString();
        timer = (Timer) in.readValue(Timer.class.getClassLoader());
        atMax = in.readByte() != 0x00;
        imageResource = in.readString();
        imageId = in.readInt();
        favourite = in.readByte() != 0x00;
        tID = in.readInt();
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
        dest.writeInt(imageId);
        dest.writeByte((byte) (favourite ? 0x01 : 0x00));
        dest.writeInt(tID);
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