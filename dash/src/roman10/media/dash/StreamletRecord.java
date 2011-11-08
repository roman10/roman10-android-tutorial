package roman10.media.dash;

public class StreamletRecord {
	public double startTime;
	public double endTime;
	public long startSample;
	public long endSample;
	public StreamletRecord(double _sTime, double _eTime, long _sSample, long _eSample) {
		startTime = _sTime;
		endTime = _eTime;
		startSample = _sSample;
		endSample = _eSample;
	}
}
