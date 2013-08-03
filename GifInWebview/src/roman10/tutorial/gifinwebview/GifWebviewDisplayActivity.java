package roman10.tutorial.gifinwebview;

import android.os.Bundle;
import android.app.Activity;

public class GifWebviewDisplayActivity extends Activity {
	private GifWebView gifView;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gif_webview_display);
		gifView = (GifWebView) findViewById(R.id.gif_view);
		gifView.setGifAssetPath("file:///android_asset/1.gif");
	}
}
