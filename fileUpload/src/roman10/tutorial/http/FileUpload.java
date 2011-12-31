package roman10.tutorial.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class FileUpload extends Activity {
    public static TextView textDisplay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        test();
    }
    
    private void test() {
    	textDisplay = (TextView) findViewById(R.id.main_text);
        textDisplay.setText("file uploaded:\n");
        createTestingFiles(5);
        prepareUploadFiles();
        startUploadService();
    }
    
    private void createTestingFiles(int fCount) {
    	File folder = new File("/sdcard/fileuploadtest/");
    	folder.mkdirs();
    	try {
    		for (int i= 0; i < fCount; ++i) {
    			FileWriter fOut = new FileWriter(folder.getAbsolutePath() + "test" + i + ".txt");
    			fOut.write(folder.getAbsolutePath() + "test" + i + ".txt");
    			fOut.flush();
    			fOut.close();
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void prepareUploadFiles() {
    	File folder = new File("/sdcard/fileuploadtest/");
    	for (File aFile:folder.listFiles()) {
    		UploadService.addUploadFile(aFile.getAbsolutePath());
    	}
    }
    
    private void startUploadService() {
    	Intent l_intent = new Intent(this.getApplicationContext(), UploadService.class);
		this.startService(l_intent);
    }
}