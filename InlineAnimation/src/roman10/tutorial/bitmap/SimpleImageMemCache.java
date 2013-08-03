package roman10.tutorial.bitmap;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class SimpleImageMemCache {
	private LruCache<String, Bitmap> mMemoryCache;
	private int mWidth, mHeight;
	
	public SimpleImageMemCache(float pRatio, int pWidth, int pHeight) {
		setCacheRatio(pRatio);
		mWidth = pWidth;
		mHeight = pHeight;
	}
	
	private void setCacheRatio(float pRatio) {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    // Use pRatio percentage of the available memory for this memory cache.
	    final int cacheSize = (int) (maxMemory*pRatio);
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	        	final int bitmapSize = BitmapUtils.getBitmapSize(bitmap) / 1024;
//	        	Logger.d(this, "cache size increment " + bitmapSize);
                return bitmapSize == 0 ? 1 : bitmapSize;
	        }
	    };
	}
	
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (null != key && null != bitmap && getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	private Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	public Bitmap loadBitmap(String pPath) {
	    Bitmap bitmap = getBitmapFromMemCache(pPath);
	    if (bitmap != null) {
//	    	Logger.d(this, "memory cache hit");
	        return bitmap;
	    } else {
	    	bitmap = BitmapUtils.decodeSampledBitmapFromFile(pPath, mWidth, mHeight);
	        addBitmapToMemoryCache(pPath, bitmap);
	        return bitmap;
	    }
	}
	
	public Bitmap loadBitmap(Resources res, int pRes) {
		Bitmap bitmap = getBitmapFromMemCache(String.valueOf(pRes));
	    if (bitmap != null) {
	        return bitmap;
	    } else {
	    	bitmap = BitmapUtils.decodeSampledBitmapFromResource(res, pRes, mWidth, mHeight);
	        addBitmapToMemoryCache(String.valueOf(pRes), bitmap);
	        return bitmap;
	    }
	}
	
	public Bitmap loadBitmap(Context pContext, String pAssetPath) {
		AssetManager manager = pContext.getAssets();
		InputStream desp;
		try {
			desp = manager.open(pAssetPath);
			Bitmap bitmap = getBitmapFromMemCache(pAssetPath);
			if (bitmap != null) {
		        return bitmap;
		    } else {
		    	bitmap = BitmapUtils.decodeSampledBitmapFromInputStream(desp, mWidth, mHeight);
		        addBitmapToMemoryCache(pAssetPath, bitmap);
		        return bitmap;
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void clearCache() {
		if (mMemoryCache != null) {
            mMemoryCache.evictAll();
		}
	}
}
