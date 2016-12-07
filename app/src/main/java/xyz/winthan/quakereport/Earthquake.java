package xyz.winthan.quakereport;

/**
 * Created by Win Than on 11/29/2016.
 */
public class Earthquake {

    private double mMagnitude;

    private String mLocation;

    private long mDate;

    private String mURL;

    public Earthquake(double mMagnitude, String mLocation, long mDate, String mURL) {
        this.mMagnitude = mMagnitude;
        this.mLocation = mLocation;
        this.mDate = mDate;
        this.mURL = mURL;
    }

    public Earthquake(double mMagnitude, String mLocation, long mDate) {
        this.mMagnitude = mMagnitude;
        this.mLocation = mLocation;
        this.mDate = mDate;
    }

    public String getmURL() {
        return mURL;
    }

    public double getmMagnitude() {
        return mMagnitude;
    }

    public String getmLocation() {
        return mLocation;
    }

    public Long getmDate() {
        return mDate;
    }
}
