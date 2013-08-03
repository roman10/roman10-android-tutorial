package roman10.tutorial.frameanimation;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimationSettings implements Parcelable {
	public int mDelay;			// in milliseconds
	public int mPlaybackTimes;			// 0 for forever, 
	public Dimension mDim; 
	public int mColors;
	
	public static final int GIF_DELAY_DEFAULT = 200;
	
	public AnimationSettings() {
		mDelay = GIF_DELAY_DEFAULT;
		mPlaybackTimes = 0;
		mDim = new Dimension(320, 240);
		mColors = 256;
	}
	
	public static AnimationSettings newCopy(AnimationSettings pSettings) {
		AnimationSettings newSettings = new AnimationSettings();
		newSettings.mDelay = pSettings.mDelay;
		newSettings.mPlaybackTimes = pSettings.mPlaybackTimes;
		newSettings.mDim = new Dimension(pSettings.mDim.width, pSettings.mDim.height);
		newSettings.mColors = pSettings.mColors;
		return newSettings;
	}
	
	public AnimationSettings(Parcel parcel) {
		readFromParcel(parcel);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(mDelay);
		parcel.writeInt(mPlaybackTimes);
		parcel.writeParcelable(mDim, flags);
		parcel.writeInt(mColors);
	}
	
	private void readFromParcel(Parcel parcel) {
		mDelay = parcel.readInt();
		mPlaybackTimes = parcel.readInt();
		mDim = parcel.readParcelable(Dimension.class.getClassLoader());
		mColors = parcel.readInt();
	}
	
	public static final Parcelable.Creator CREATOR =
    	new Parcelable.Creator() {
            public AnimationSettings createFromParcel(Parcel in) {
                return new AnimationSettings(in);
            }
 
            public AnimationSettings[] newArray(int size) {
                return new AnimationSettings[size];
            }
        };
}
