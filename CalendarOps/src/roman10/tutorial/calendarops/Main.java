package roman10.tutorial.calendarops;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

class MyCalendar {
	public String name;
	public String id;
	public MyCalendar(String _name, String _id) {
		name = _name;
		id = _id;
	}
	@Override
	public String toString() {
		return name;
	}
}

public class Main extends Activity {
    /*********************************************************************
     * UI part*/
	private Spinner m_spinner_calender;
	private Button m_button_add;
	private Button m_button_add2;
	private Button m_button_getEvents;
	private TextView m_text_event;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*get calendar list and populate the view*/
        getCalendars();
        populateCalendarSpinner();
        populateAddBtn();
        populateAddBtn2();
        populateTextEvent();
        populateGetEventsBtn();
    }
    private void populateCalendarSpinner() {
    	m_spinner_calender = (Spinner)this.findViewById(R.id.spinner_calendar);
    	ArrayAdapter l_arrayAdapter = new ArrayAdapter(this.getApplicationContext(), android.R.layout.simple_spinner_item, m_calendars);
    	l_arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	m_spinner_calender.setAdapter(l_arrayAdapter);
    	m_spinner_calender.setSelection(0);
    	m_spinner_calender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> p_parent, View p_view,
					int p_pos, long p_id) {
				m_selectedCalendarId = m_calendars[(int)p_id].id;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
    }
    private void populateAddBtn() {
    	m_button_add = (Button) this.findViewById(R.id.button_add);
    	m_button_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addEvent();
			}
		});
    }
    private void populateAddBtn2() {
    	m_button_add2 = (Button) this.findViewById(R.id.button_add2);
    	m_button_add2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addEvent2();
			}
		});
    }
    private void populateGetEventsBtn() {
    	m_button_getEvents = (Button) findViewById(R.id.button_get_events);
    	m_button_getEvents.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getLastThreeEvents();
			}
		});
    }
    private void populateTextEvent() {
    	m_text_event = (TextView) findViewById(R.id.text_event);
    	String l_str = "title: roman10 calendar tutorial test\n" +
    		"description: This is a simple test for calendar api\n" + 
    		"eventLocation: @home\n" + 
    		"start time:" + getDateTimeStr(0) + "\n" + 
    		"end time: " + getDateTimeStr(30) + "\n" + 
    		"event status: confirmed\n" + 
    		"all day: no\n" + 
    		"has alarm: yes\n";
    	m_text_event.setText(l_str);
    }
    /****************************************************************
     * Data part
     */
    /*retrieve a list of available calendars*/
    private MyCalendar m_calendars[];
    private String m_selectedCalendarId = "0";
    private void getCalendars() {
    	String[] l_projection = new String[]{"_id", "displayName"};
    	Uri l_calendars;
    	if (Build.VERSION.SDK_INT >= 8) {
    		l_calendars = Uri.parse("content://com.android.calendar/calendars");
    	} else {
    		l_calendars = Uri.parse("content://calendar/calendars");
    	}
    	Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, null, null, null);	//all calendars
    	//Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, "selected=1", null, null);   //active calendars
    	if (l_managedCursor.moveToFirst()) {
    		m_calendars = new MyCalendar[l_managedCursor.getCount()];
    		String l_calName;
    		String l_calId;
    		int l_cnt = 0;
    		int l_nameCol = l_managedCursor.getColumnIndex(l_projection[1]);
    		int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);
    		do {
    			l_calName = l_managedCursor.getString(l_nameCol);
    			l_calId = l_managedCursor.getString(l_idCol);
    			m_calendars[l_cnt] = new MyCalendar(l_calName, l_calId);
    			++l_cnt;
    		} while (l_managedCursor.moveToNext());
    	}
    }
    /*add an event to calendar*/
    private void addEvent() {
    	ContentValues l_event = new ContentValues();
    	l_event.put("calendar_id", m_selectedCalendarId);
    	l_event.put("title", "roman10 calendar tutorial test");
    	l_event.put("description", "This is a simple test for calendar api");
    	l_event.put("eventLocation", "@home");
    	l_event.put("dtstart", System.currentTimeMillis());
    	l_event.put("dtend", System.currentTimeMillis() + 1800*1000);
    	l_event.put("allDay", 0);
    	//status: 0~ tentative; 1~ confirmed; 2~ canceled
    	l_event.put("eventStatus", 1);
    	//0~ default; 1~ confidential; 2~ private; 3~ public
    	l_event.put("visibility", 0);
    	//0~ opaque, no timing conflict is allowed; 1~ transparency, allow overlap of scheduling
    	l_event.put("transparency", 0);
    	//0~ false; 1~ true
    	l_event.put("hasAlarm", 1);
    	Uri l_eventUri;
    	if (Build.VERSION.SDK_INT >= 8) {
    		l_eventUri = Uri.parse("content://com.android.calendar/events");
    	} else {
    		l_eventUri = Uri.parse("content://calendar/events");
    	}
    	Uri l_uri = this.getContentResolver().insert(l_eventUri, l_event);
    	Log.v("++++++test", l_uri.toString());
    }
    /*add an event through intent, this doesn't require any permission
     * just send intent to android calendar
     * http://www.openintents.org/en/uris*/
    private void addEvent2() {
    	Intent l_intent = new Intent(Intent.ACTION_EDIT);
    	l_intent.setType("vnd.android.cursor.item/event");
    	//l_intent.putExtra("calendar_id", m_selectedCalendarId);  //this doesn't work
    	l_intent.putExtra("title", "roman10 calendar tutorial test");
    	l_intent.putExtra("description", "This is a simple test for calendar api");
    	l_intent.putExtra("eventLocation", "@home");
    	l_intent.putExtra("beginTime", System.currentTimeMillis());
    	l_intent.putExtra("endTime", System.currentTimeMillis() + 1800*1000);
    	l_intent.putExtra("allDay", 0);
    	//status: 0~ tentative; 1~ confirmed; 2~ canceled
    	l_intent.putExtra("eventStatus", 1);
    	//0~ default; 1~ confidential; 2~ private; 3~ public
    	l_intent.putExtra("visibility", 0);
    	//0~ opaque, no timing conflict is allowed; 1~ transparency, allow overlap of scheduling
    	l_intent.putExtra("transparency", 0);
    	//0~ false; 1~ true
    	l_intent.putExtra("hasAlarm", 1);
    	try {
    		startActivity(l_intent);
    	} catch (Exception e) {
    		Toast.makeText(this.getApplicationContext(), "Sorry, no compatible calendar is found!", Toast.LENGTH_LONG).show();
    	}
    }
    /*get a list of events
     * http://jimblackler.net/blog/?p=151*/
    private void getLastThreeEvents() {
    	Uri l_eventUri;
    	if (Build.VERSION.SDK_INT >= 8) {
    		l_eventUri = Uri.parse("content://com.android.calendar/events");
    	} else {
    		l_eventUri = Uri.parse("content://calendar/events");
    	}
    	String[] l_projection = new String[]{"title", "dtstart", "dtend"};
    	Cursor l_managedCursor = this.managedQuery(l_eventUri, l_projection, "calendar_id=" + m_selectedCalendarId, null, "dtstart DESC, dtend DESC");
    	//Cursor l_managedCursor = this.managedQuery(l_eventUri, l_projection, null, null, null);
    	if (l_managedCursor.moveToFirst()) {
    		int l_cnt = 0;
    		String l_title;
    		String l_begin;
    		String l_end;
    		StringBuilder l_displayText = new StringBuilder();
    		int l_colTitle = l_managedCursor.getColumnIndex(l_projection[0]);
    		int l_colBegin = l_managedCursor.getColumnIndex(l_projection[1]);
    		int l_colEnd = l_managedCursor.getColumnIndex(l_projection[1]);
    		do {
    			l_title = l_managedCursor.getString(l_colTitle);
    			l_begin = getDateTimeStr(l_managedCursor.getString(l_colBegin));
    			l_end = getDateTimeStr(l_managedCursor.getString(l_colEnd));
    			l_displayText.append(l_title + "\n" + l_begin + "\n" + l_end + "\n----------------\n");
    			++l_cnt;
    		} while (l_managedCursor.moveToNext() && l_cnt < 3);
    		m_text_event.setText(l_displayText.toString());
    	}
    }
    /************************************************
     * utility part
     */
    private static final String DATE_TIME_FORMAT = "yyyy MMM dd, HH:mm:ss";
    public static String getDateTimeStr(int p_delay_min) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		if (p_delay_min == 0) {
			return sdf.format(cal.getTime());
		} else {
			Date l_time = cal.getTime();
			l_time.setMinutes(l_time.getMinutes() + p_delay_min);
			return sdf.format(l_time);
		}
	}
    public static String getDateTimeStr(String p_time_in_millis) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
    	Date l_time = new Date(Long.parseLong(p_time_in_millis));
    	return sdf.format(l_time);
    }
}