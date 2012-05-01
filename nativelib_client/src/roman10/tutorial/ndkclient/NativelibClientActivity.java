package roman10.tutorial.ndkclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NativelibClientActivity extends Activity {
    private TextView displayText;
    private static final String libPath = "/data/data/roman10.tutorial.nativelib/lib/libhello-jni.so";
    public native String  stringFromJNI();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        displayText = (TextView) findViewById(R.id.display);
        System.load(libPath);
        displayText.setText( stringFromJNI() );
    }
}