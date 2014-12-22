package org.drumm.android.alarms;

import java.util.HashSet;

public class ActiveAlarmManager {
	private static HashSet<Integer> activeAlarmIds = new HashSet<Integer>();

	public static void addActiveAlarm(int id) {
		activeAlarmIds.add(id);
	}

	public static void removeActiveAlarm(int id) {
		activeAlarmIds.remove(id);
	}

	public static boolean isActive(int id) {
		return activeAlarmIds.contains(id);
	}
}
