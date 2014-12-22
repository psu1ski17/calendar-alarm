package org.drumm.android.calendar;

import java.util.Date;

public class Event {
	private String title;
	private String description;
	private Date begin;
	private Date end;
	private boolean allDay;

	Event(String title, String desc, Date begin, Date end, boolean allDay) {
		this.title = title;
		this.description = desc;
		this.begin = begin;
		this.end = end;
		this.allDay = allDay;
	}

	public String toString() {
		return "<title=" + title + " description=" + description + " begin="
				+ begin.toLocaleString() + " end=" + end.toLocaleString()
				+ " allDay=" + allDay + " >";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
}
