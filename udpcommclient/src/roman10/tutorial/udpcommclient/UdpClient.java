package roman10.tutorial.udpcommclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;

public class UdpClient extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        runUdpClient();
        finish();
    }
    private static final int UDP_SERVER_PORT = 11111;
    private void runUdpClient()  {
    	String udpMsg = "hello world from UDP client " + UDP_SERVER_PORT;
    	DatagramSocket ds = null;
    	try {
			ds = new DatagramSocket();
			InetAddress serverAddr = InetAddress.getByName("127.0.0.1");
			DatagramPacket dp;
			dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, UDP_SERVER_PORT);
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ds != null) {
				ds.close();
			}
		}
    }
}