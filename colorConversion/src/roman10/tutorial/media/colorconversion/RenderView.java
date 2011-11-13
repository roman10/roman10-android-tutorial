package roman10.tutorial.media.colorconversion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class RenderView extends View{
	private static final String TAG = "RenderView";
	private Bitmap prBitmap;
	private final Paint prFramePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	
	private static native void naGetConvertedFrame(Bitmap _bitmap, String _testFilePath, int _width, int _height);
	
	public RenderView(Context _context, String _testFilePath, int _width, int _height) {
		super(_context);
		/*initialize the bitmap according to test frame size*/
		prBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
		/*call the color conversion procedure to convert and fill bitmap with RGBA data*/
		naGetConvertedFrame(prBitmap, _testFilePath, _width, _height); 
		/*render the bitmap*/
		invalidate();
	}
	
	@Override protected void onDraw(Canvas _canvas) {
		if (prBitmap != null) {
			Log.i("drawbitmap", "---RENDER ST");
			_canvas.drawBitmap(prBitmap, 0, 0, prFramePaint);
			Log.i("drawbitmap", "---RENDER ED");
		}
	}
	
	@Override protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
