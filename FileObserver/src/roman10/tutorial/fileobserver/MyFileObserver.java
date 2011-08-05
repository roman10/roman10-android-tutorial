package roman10.tutorial.fileobserver;

import android.os.FileObserver;

public class MyFileObserver extends FileObserver {
	public String absolutePath;
	
	public MyFileObserver(String path) {
		super(path, FileObserver.ALL_EVENTS);
		absolutePath = path;
	}

	@Override
	public void onEvent(int event, String path) {
		if (path == null) {
			return;
		}
		//a new file or subdirectory was created under the monitored directory
		if ((FileObserver.CREATE & event)!=0) {
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is created\n";
		}
		//a file or directory was opened
		if ((FileObserver.OPEN & event)!=0) {
			FileAccessLogStatic.accessLogMsg += path + " is opened\n";
		}
		//data was read from a file
		if ((FileObserver.ACCESS & event)!=0) {
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is accessed/read\n";
		}
		//data was written to a file
		if ((FileObserver.MODIFY & event)!=0) {
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is modified\n";
		}
		//someone has a file or directory open read-only, and closed it
		if ((FileObserver.CLOSE_NOWRITE & event)!=0) {
			FileAccessLogStatic.accessLogMsg += path + " is closed\n";
		}
		//someone has a file or directory open for writing, and closed it 
		if ((FileObserver.CLOSE_WRITE & event)!=0) {
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is written and closed\n";
		}
		//[todo: consider combine this one with one below]
		//a file was deleted from the monitored directory
		if ((FileObserver.DELETE & event)!=0) {
			//for testing copy file
//			FileUtils.copyFile(absolutePath + "/" + path);
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is deleted\n";
		}
		//the monitored file or directory was deleted, monitoring effectively stops
		if ((FileObserver.DELETE_SELF & event)!=0) {
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + " is deleted\n";
		}
		//a file or subdirectory was moved from the monitored directory
		if ((FileObserver.MOVED_FROM & event)!=0) {
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is moved to somewhere " + "\n";
		}
		//a file or subdirectory was moved to the monitored directory
		if ((FileObserver.MOVED_TO & event)!=0) {
			FileAccessLogStatic.accessLogMsg += "File is moved to " + absolutePath + "/" + path + "\n";
		}
		//the monitored file or directory was moved; monitoring continues
		if ((FileObserver.MOVE_SELF & event)!=0) {
			FileAccessLogStatic.accessLogMsg += path + " is moved\n";
		}
		//Metadata (permissions, owner, timestamp) was changed explicitly
		if ((FileObserver.ATTRIB & event)!=0) {
			FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is changed (permissions, owner, timestamp)\n";
		}
	}
}
