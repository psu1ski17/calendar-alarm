package org.drumm.android.calendar;

import java.util.Collection;

public interface ICalendarProvider {

	public abstract Collection<Calendar> getAllCalendars();

	public abstract Calendar getCalendarByName(String name);

	public abstract Collection<Event> getEventsFromCalendar(String name,
			long start, long end);

	public abstract Collection<Event> getEventsFromCalendar(Calendar cal,
			long queryStart, long queryEnd);

}