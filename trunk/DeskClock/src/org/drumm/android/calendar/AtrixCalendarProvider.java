package org.drumm.android.calendar;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public class AtrixCalendarProvider implements ICalendarProvider {
	private static final String EVENT_POSTFIX = "events";
	private static final String CALENDAR_POSTFIX = "calendars";
	private static final String PREFIX = "content://";
	private static final String WHEN_POSTFIX = "instances/when";
	// String uriString = "content://calendar/calendars";

	// uriString = "content://calendar/events";
	private String provider = null;
	private Uri calendarUri = null;
	private Context context;
	private Uri calendarWhenUri;

	public AtrixCalendarProvider(Context context) {
		this(android.os.Build.VERSION.SDK_INT, context);
	}

	public AtrixCalendarProvider(int version, Context context) {
		setupProvider(version);
		this.context = context;

	}

	private void setupProvider(int version) {
		// content://com.android.calendar/calendars
		if (version >= 10 && version < 14) {
			provider = "com.android.calendar";
		} else if (version < 10) {
			provider = "calendar";
		}

		if (version < 14) {
			calendarUri = Uri.parse(PREFIX + provider + "/" + CALENDAR_POSTFIX);
			Uri.parse(PREFIX + provider + "/" + EVENT_POSTFIX);
			this.calendarWhenUri = Uri.parse(PREFIX + provider + "/"
					+ WHEN_POSTFIX);
		} else {
			calendarUri = CalendarContract.Calendars.CONTENT_URI;
			calendarWhenUri = CalendarContract.Instances.CONTENT_URI;
		}
	}

	/* (non-Javadoc)
	 * @see org.drumm.android.calendar.ICalendarProvider#getAllCalendars()
	 */
	@Override
	public Collection<Calendar> getAllCalendars() {
		Cursor managedCursor = null;
		try {
			String[] projection = new String[] { "_id", "name" };
			Collection<Calendar> calendars;
			managedCursor = context.getContentResolver().query(calendarUri,
					projection, null, null, null);
			if (managedCursor == null) {
				calendars = null;
			} else {
				calendars = new LinkedList<Calendar>();
			}
			if (managedCursor != null && managedCursor.moveToFirst()) {
				String calName;
				String calId;
				int nameColumn = managedCursor.getColumnIndex("name");
				int idColumn = managedCursor.getColumnIndex("_id");
				do {
					calName = managedCursor.getString(nameColumn);
					calId = managedCursor.getString(idColumn);
					calendars.add(new Calendar(calId, calName));
				} while (managedCursor.moveToNext());
			}
			return calendars;
		} finally {
			if (managedCursor != null) {
				managedCursor.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.drumm.android.calendar.ICalendarProvider#getCalendarByName(java.lang.String)
	 */
	@Override
	public Calendar getCalendarByName(String name) {
		Cursor managedCursor = null;
		try {
			String[] projection = new String[] { "_id", "name" };
			Calendar cal = null;
			managedCursor = context.getContentResolver().query(calendarUri,
					projection, null, null, null);
			if (managedCursor != null && managedCursor.moveToFirst()) {
				String calName;
				String calId;
				int nameColumn = managedCursor.getColumnIndex("name");
				int idColumn = managedCursor.getColumnIndex("_id");
				do {
					calName = managedCursor.getString(nameColumn);
					calId = managedCursor.getString(idColumn);
					if (calName.equals(name)) {
						cal = new Calendar(calId, calName);
						break;
					}
				} while (managedCursor.moveToNext());
			}
			return cal;
		} finally {
			if (managedCursor != null) {
				managedCursor.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.drumm.android.calendar.ICalendarProvider#getEventsFromCalendar(java.lang.String, long, long)
	 */
	@Override
	public Collection<Event> getEventsFromCalendar(String name, long start,
			long end) {
		Calendar cal = getCalendarByName(name);
		return getEventsFromCalendar(cal, start, end);
	}

	/* (non-Javadoc)
	 * @see org.drumm.android.calendar.ICalendarProvider#getEventsFromCalendar(org.drumm.android.calendar.Calendar, long, long)
	 */
	@Override
	public Collection<Event> getEventsFromCalendar(Calendar cal,
			long queryStart, long queryEnd) {
		Cursor eventCursor = null;
		try {
			Uri.Builder builder = calendarWhenUri.buildUpon();
			ContentUris.appendId(builder, queryStart);
			ContentUris.appendId(builder, queryEnd);
			ContentResolver cr = context.getContentResolver();
			eventCursor = cr.query(builder.build(), new String[] { "title",
					"begin", "end", "allDay", "description" }, "Calendars._id="
					+ cal.getId(), null, "startDay ASC, startMinute ASC");
			// eventCursor = cr.query(builder.build(), new String[] {"title",
			// "dtstart","dtend", "allDay", "description"},
			// "Calendars._id="+cal.getId(),null,
			// "startDay ASC, startMinute ASC");
			// dtstart and dtend are recurring start and end
			// eventCursor = cr.query(builder.build(), new String[] {"title"},
			// "Calendars._id="+cal.getId(),null,
			// "startDay ASC, startMinute ASC");
			Collection<Event> events = new LinkedList<Event>();
			while (eventCursor.moveToNext()) {
				final String title = eventCursor.getString(0);
				final Date begin = new Date(eventCursor.getLong(1));
				final Date end = new Date(eventCursor.getLong(2));
				final Boolean allDay = !eventCursor.getString(3).equals("0");
				final String desc = eventCursor.getString(4);
				Event event = new Event(title, desc, begin, end, allDay);
				events.add(event);
				System.out.println("Title: " + title + " Begin: " + begin
						+ " End: " + end + " All Day: " + allDay);
			}
			return events;
		} finally {
			if (eventCursor != null) {
				eventCursor.close();
			}
		}
	}
}
