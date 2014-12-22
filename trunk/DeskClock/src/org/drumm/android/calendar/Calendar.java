package org.drumm.android.calendar;

public class Calendar {
	private String id;
	private String name;

	Calendar(String id) {
		this.id = id;
	}

	public Calendar(String calId, String calName) {
		this.id = calId;
		this.name = calName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return id + ":" + name;
	}

}
