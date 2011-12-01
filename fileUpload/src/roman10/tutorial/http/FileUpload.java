package roman10.tutorial.http;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FileUpload extends Activity {
    public static TextView textDisplay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textDisplay = (TextView) findViewById(R.id.main_text);
        textDisplay.setText("file uploaded:\n");
    }
}