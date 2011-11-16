package roman10.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtilsStatic {
	private static final String DATE_TIME_FORMAT1 = "yyyyMMddHHmmss";
	//get the current date and time as a string in format1
	public static String get_current_date_time_format1_str() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT1);
		return sdf.format(cal.getTime());
	}
}
