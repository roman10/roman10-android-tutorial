package roman10.tutorial.frameanimation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.content.res.AssetManager;

public class AnimationAssetsSet {
	private String[] mFileNames;
	private AssetManager mAssetManager;
	private String mAssetFolder;
	public AnimationAssetsSet(Context pContext, String pAssetFolder) {
		mAssetManager = pContext.getAssets();
		mAssetFolder = pAssetFolder;
		try {
			mFileNames = mAssetManager.list(pAssetFolder);
			Arrays.sort(mFileNames);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getGifFramePath(int pIdx) {
		if (null == mFileNames) {
			return null;
		} else {
			return mAssetFolder + File.separator + mFileNames[pIdx];
		}
	}
	
	public int getNumOfFrames() {
		if (null == mFileNames) {
			return 0;
		} else {
			return mFileNames.length; 
		}
	}
}
