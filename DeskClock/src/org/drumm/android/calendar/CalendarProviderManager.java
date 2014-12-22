package org.drumm.android.calendar;

import android.content.Context;

public class CalendarProviderManager {
	public CalendarProviderManager(){
		
	}
	
	public ICalendarProvider getCalendarProvider(Context context){
		int version = android.os.Build.VERSION.SDK_INT;
		if (version >= 10 && version < 14) {
			return new AtrixCalendarProvider(context);
		} else if (version < 10) {
			return new AtrixCalendarProvider(context);
		}else if (version>=14){
			return  new GalaxyS3CalendarProvider(context);
		}
		return null;
	}
}
