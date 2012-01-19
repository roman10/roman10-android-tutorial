package roman10.statusbarnotification.evil;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

public class StatusBarNotificationActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new Thread(new Runnable() {
				    public void run() {
				    	int mCount = 0;
				    	mRun = true;
				    	while (mRun) {
				    		++mCount;
				    		SystemClock.sleep(1000);
					    	CharSequence title = "Freq noti is evil: " + mCount;
					        CharSequence content = "Freq notification update takes too much CPU";
					        if (CUSTOM_NOTI) {
					        	noti.contentView.setTextViewText(R.id.status_text, title);
					        	noti.contentView.setProgressBar(R.id.status_progress, 100, mCount%100, false);
					        } else {
						        Intent notiIntent = new Intent(context, StatusBarNotificationActivity.class);
						        PendingIntent pi = PendingIntent.getService(context, 0, notiIntent, 0);
						    	noti.setLatestEventInfo(context, title, content, pi);
					        }
					        //nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					        //nm = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
					    	nm.notify(STATUS_BAR_NOTIFICATION, noti);
				    	}
				    	
				    }
				  }).start();
			}
		});
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mRun = false;
			}
		});
        
        nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        CharSequence tickerText = "hello";
        long when = System.currentTimeMillis();
        noti = new Notification(R.drawable.ic_launcher, tickerText, when);
        context = this.getApplicationContext();
        
        Intent notiIntent = new Intent(context, StatusBarNotificationActivity.class);
        PendingIntent pi = PendingIntent.getService(context, 0, notiIntent, 0);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        
        CharSequence title = "Frequent notification is evil";
        CharSequence content = "Frequent notification update takes too much CPU";
        if (CUSTOM_NOTI) {
	        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.noti);
			contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_launcher);
			contentView.setTextViewText(R.id.status_text, title);
			contentView.setProgressBar(R.id.status_progress, 100, 0, false);
			noti.contentView = contentView;
			noti.contentIntent = pi;
        } else {
        	noti.setLatestEventInfo(context, title, content, pi);
        }
        
        nm.notify(STATUS_BAR_NOTIFICATION, noti);
    }
    private boolean CUSTOM_NOTI = true;
    private Context context;
    private NotificationManager nm;
    private Notification noti;
    private final int STATUS_BAR_NOTIFICATION = 1;
    private boolean mRun;
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mRun = false;
    }
}