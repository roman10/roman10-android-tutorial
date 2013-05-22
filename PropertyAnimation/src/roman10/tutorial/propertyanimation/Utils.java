package roman10.tutorial.propertyanimation;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
	public static float convertDpToPixel(Context contex, float pInDp) {
		Resources r = contex.getResources();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pInDp, r.getDisplayMetrics()));
	}
}
