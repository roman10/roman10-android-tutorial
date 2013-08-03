package roman10.tutorial.gifinwebview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

public class GifWebView extends WebView {
    
	public GifWebView(Context context) {
		super(context);
		getSettings().setJavaScriptEnabled(true);
	}
	
    public GifWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getSettings().setJavaScriptEnabled(true);
	}

	public void setGifPath(String pPath) {
		String baseUrl = pPath.substring(0, pPath.lastIndexOf("/") + 1);
		String fileName = pPath.substring(pPath.lastIndexOf("/")+1);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} </style>");
		strBuilder.append("</head><body>");
		strBuilder.append("<img src=\"" + fileName + "\" width=\"100%\" /></body></html>");
		String data = strBuilder.toString();
		Log.d(this.getClass().getName(), "data: " + data);
		Log.d(this.getClass().getName(), "base url: " + baseUrl);
		Log.d(this.getClass().getName(), "file name: " + fileName);
		loadDataWithBaseURL("file://" + baseUrl, data, "text/html", "utf-8", null);
    }
	
	public void setGifAssetPath(String pPath) {
		String baseUrl = pPath.substring(0, pPath.lastIndexOf("/") + 1);
		String fileName = pPath.substring(pPath.lastIndexOf("/")+1);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} </style>");
		strBuilder.append("</head><body>");
		strBuilder.append("<img src=\"" + fileName + "\" width=\"100%\" /></body></html>");
		String data = strBuilder.toString();
		Log.d(this.getClass().getName(), "data: " + data);
		Log.d(this.getClass().getName(), "base url: " + baseUrl);
		Log.d(this.getClass().getName(), "file name: " + fileName);
		loadDataWithBaseURL(baseUrl, data, "text/html", "utf-8", null);
    }
	
	private String jsOnload = "<script type=\"text/javascript\">function resize(image) {" + "\n"
		  +     "var differenceHeight = document.body.clientHeight - image.clientHeight;" + "\n"
		  +     "var differenceWidth  = document.body.clientWidth  - image.clientWidth;" + "\n"
		  +      "if (differenceHeight < 0) differenceHeight = differenceHeight * -1;" + "\n"
		  +      "if (differenceWidth  < 0) differenceWidth  = differenceWidth * -1;" + "\n"
		  +      "if (differenceHeight > differenceWidth)" + "\n"
		  +      "{" + "\n"
		  +       "   image.style['height'] = document.body.clientHeight + 'px';" + "\n"
		  +      "}" + "\n"
		  +      "else" + "\n"
		  +      "{" + "\n"
		  +       "   image.style['width'] = document.body.clientWidth + 'px' ;" + "\n"
		  +      "}" + "\n"
		  + 	 "console.info(document.body.clientWidth);" + "\n"
		  + 	 "console.info(document.body.clientHeight);" + "\n"
		  +      "image.style['margin'] = 0;" + "\n"
		  +      "document.body.style['margin'] = 0;" + "\n"
		  +  "}" + "\n"
		+ "</script>";
}