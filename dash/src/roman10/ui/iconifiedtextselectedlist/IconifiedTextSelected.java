package roman10.ui.iconifiedtextselectedlist;

import java.lang.ref.WeakReference;

import android.graphics.drawable.Drawable;

public class IconifiedTextSelected implements Comparable<IconifiedTextSelected>{
    
	private String mText = "";
	//private SoftReference<Drawable> mIcon;
	private WeakReference<Drawable> mIcon;
	private Drawable mHardIcon;
	private boolean mSelected;
	private boolean mSelectable = true;
	private boolean mVisibility;
	private int mCid;
	private int mRid;
	private int mType;		//0: default; 1: picture; 2:video; 21: hard picture; 22: hard video
	private int mMediaType;	//0: photo; 1: video
	private int mFolderType;//0: photo; 1: video; 2: mixed, new type
	
	public IconifiedTextSelected(String text, Drawable bullet, boolean selected, boolean visibility, int _type, int _mediaType, int _folderType) {
		//mIcon = new SoftReference<Drawable>(bullet);
		if ((_type==0) || (_type == 21) || (_type == 22)) {
			mHardIcon = bullet;
		} else {
			mIcon = new WeakReference<Drawable>(bullet);
		}
		//mIcon = bullet;
		mText = text;
		mSelected = selected;
		mVisibility = visibility;
		mType = _type;
		mMediaType = _mediaType;
		mFolderType = _folderType;
	}
	
	public IconifiedTextSelected(String text, Drawable bullet, boolean selected, boolean visibility, int cid, int rid, int _type, int _mediaType, int _folderType) {
		//mIcon = new SoftReference<Drawable>(bullet);
		if ((_type==0) || (_type == 21) || (_type == 22)) {
			mHardIcon = bullet;
		} else {
			mIcon = new WeakReference<Drawable>(bullet);
		}
		//mIcon = bullet;
		mText = text;
		mSelected = selected;
		mVisibility = visibility;
		mCid = cid;
		mRid = rid;
		mType = _type;
		mMediaType = _mediaType;
		mFolderType = _folderType;
	}
	
	public int getMediaType() {
		return mMediaType;
	}
	
	public int getFolderType() {
		return mFolderType;
	}
	
	public int getCid() {
		return mCid;
	}
	
	public int getRid() {
		return mRid;
	}
	
	public boolean isSelectable() {
		return mSelectable;
	}
	
	public boolean getVisibility() {
		return mVisibility;
	}
	
	public void setVisibility(boolean visibility) {
		mVisibility = visibility;
	}
	
	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}
	
	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	public boolean getSelected() {
		return mSelected;
	}
	
	public void setSelected(boolean selected) {
		mSelected = selected;
	}
	
	public void setIcon(Drawable icon) {
		if (mIcon!=null) {
			mIcon = null;
		}
		if (mHardIcon!=null) {
			mHardIcon = null;
		}
		if ((mType==0) || (mType == 21) || (mType == 22)) {
			//mIcon = new SoftReference<Drawable>(icon);
			mHardIcon = icon;
		} else {
			mIcon = new WeakReference<Drawable>(icon);
		}
	}
	
	public Drawable getIcon() {
		if ((mType==0) || (mType == 21) || (mType == 22)) {
			return mHardIcon;	 
		} else {
			return mIcon.get();
		}
	}
	
	public int getType() {
		return mType;
	}

	//@Override
	public int compareTo(IconifiedTextSelected other) {
		if(this.mText != null)
			return this.mText.compareTo(other.getText()); 
		else 
			throw new IllegalArgumentException();
	}
}
