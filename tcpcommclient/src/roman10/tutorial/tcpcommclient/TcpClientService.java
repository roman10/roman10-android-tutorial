package roman10.tutorial.tcpcommclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TcpClientService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onCreate() {
		runTcpClient();
		this.stopSelf();
	}
	private static final int TCP_SERVER_PORT = 21111;
	private void runTcpClient() {
    	try {
			Socket s = new Socket("localhost", TCP_SERVER_PORT);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			//send output msg
			String outMsg = "TCP connecting to " + TCP_SERVER_PORT + System.getProperty("line.separator"); 
			out.write(outMsg);
			out.flush();
			Log.i("TcpClient", "sent: " + outMsg);
			//accept server response
			String inMsg = in.readLine() + System.getProperty("line.separator");
			Log.i("TcpClient", "received: " + inMsg);
			//close connection
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
}
