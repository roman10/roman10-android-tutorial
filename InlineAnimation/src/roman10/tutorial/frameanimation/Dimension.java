package roman10.tutorial.frameanimation;

import android.os.Parcel;
import android.os.Parcelable;

public class Dimension implements Parcelable {
	public int width;
	public int height;
	
	public Dimension(int pWidth, int pHeight) {
		width = pWidth;
		height = pHeight;
	}
	
	public Dimension(Parcel in) {
		readFromParcel(in);
	}
	
	public static Dimension scaleDimension(Dimension pSource) {
		//candidate video resolutions
		//320:240 = 1.33:1
		//360:240 = 1.5:1
		//320:180 = 1.77:1
		float w2h = (float)(pSource.width)/pSource.height;
		float h2w = (float)(pSource.height)/pSource.width;
		if (w2h >= 1.77f) {
			pSource.width = 320;
			pSource.height = 180;
		} else if (w2h >= 1.5f) {
			pSource.width = 360;
			pSource.height = 240;
		} else if (w2h >= 1.33f) {
			pSource.width = 320;
			pSource.height = 240;
		} else if (h2w >= 1.77f) {
			pSource.width = 180;
			pSource.height = 320;
		} else if (h2w >= 1.5f) {
			pSource.width = 240;
			pSource.height = 360;
		} else if (h2w >= 1.33f) {
			pSource.width = 240;
			pSource.height = 320;
		} else {
			pSource.width = (int)(w2h*320);
			pSource.height = 320;
		}
		return pSource;
	}
	
	@Override
	public String toString() {
		String dimStr = this.width + "x" + this.height;
		return dimStr;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(width);
		dest.writeInt(height);
	}
	
	private void readFromParcel(Parcel in) {
		width = in.readInt();
		height = in.readInt();
	}
	
	 public static final Parcelable.Creator CREATOR =
    	new Parcelable.Creator() {
            public Dimension createFromParcel(Parcel in) {
                return new Dimension(in);
            }
 
            public Dimension[] newArray(int size) {
                return new Dimension[size];
            }
        };
}
