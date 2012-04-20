package roman10.tutorial.ui.pagedview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PagedViewDemoMain extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        Button btn1 = (Button)findViewById(R.id.demo1);
        btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent lIntent = new Intent();
				lIntent.setClass(getApplicationContext(), PagedViewDemo.class);
				startActivity(lIntent);
			}
		});
        
        Button btn2 = (Button)findViewById(R.id.demo2);
        btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent lIntent = new Intent();
				lIntent.setClass(getApplicationContext(), PagedViewDemo2.class);
				startActivity(lIntent);
			}
		});
        
        Button btn3 = (Button)findViewById(R.id.demo3);
        btn3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent lIntent = new Intent();
				lIntent.setClass(getApplicationContext(), PagedViewDemo3.class);
				startActivity(lIntent);
			}
		});
	}
}
