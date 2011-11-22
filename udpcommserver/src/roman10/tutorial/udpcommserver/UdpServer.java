package roman10.tutorial.udpcommserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class UdpServer extends Activity {
    /** Called when the activity is first created. */
	private TextView textView; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.text1);
        runUdpServer();
    }
    private static final int UDP_SERVER_PORT = 11111;
    private static final int MAX_UDP_DATAGRAM_LEN = 1500;
    private void runUdpServer() {
    	String lText;
    	byte[] lMsg = new byte[MAX_UDP_DATAGRAM_LEN];
    	DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
    	DatagramSocket ds = null;
    	try {
			ds = new DatagramSocket(UDP_SERVER_PORT);
			//disable timeout for testing
			//ds.setSoTimeout(100000);
			ds.receive(dp);
			lText = new String(lMsg, 0, dp.getLength());
			Log.i("UDP packet received", lText);
			textView.setText(lText);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ds != null) {
				ds.close();
			}
		}
    	
    }
}