package roman10.tutorial.customizedradiobuttonlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {
    private Button btnDefault;
    private Button btnCustomized;
    private Button btnMoreApps;
    private Button btnSingleSel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnDefault = (Button) findViewById(R.id.android_default);
        btnDefault.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent lIntent = new Intent(Main.this, roman10.tutorial.customizedradiobuttonlist.RadioGroup1.class);
				Main.this.startActivity(lIntent);
			}
		});
        btnSingleSel = (Button) findViewById(R.id.single_selection);
        btnSingleSel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent lIntent = new Intent(Main.this, roman10.tutorial.customizedradiobuttonlist.List10.class);
				Main.this.startActivity(lIntent);
			}
		});
        btnCustomized = (Button) findViewById(R.id.customized);
        btnCustomized.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				Intent lIntent = new Intent(Main.this, roman10.tutorial.customizedradiobuttonlist.CutomizedRadioButtonList.class);
				Main.this.startActivity(lIntent);
			}
		});
        btnMoreApps = (Button) findViewById(R.id.more_apps);
        btnMoreApps.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent lIntent = new Intent(Main.this, roman10.tutorial.customizedradiobuttonlist.AppsListDialog.class);
				Main.this.startActivity(lIntent);
			}
		});
    }
}