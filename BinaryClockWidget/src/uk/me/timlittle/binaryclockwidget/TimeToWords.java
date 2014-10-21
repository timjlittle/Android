package uk.me.timlittle.binaryclockwidget;

import java.util.Calendar;

public class TimeToWords {
	private static final StringBuilder builder = 
	        new StringBuilder();
	
	public synchronized static String[] timeToWords( 
	        Calendar date )
	    {
			String padding = "000000";
			
	        builder.setLength( 0 );
	        int h = date.get( Calendar.HOUR_OF_DAY );
	        int m = date.get( Calendar.MINUTE );
	        int s = date.get( Calendar.SECOND );
	        
			String hours = Integer.toBinaryString(h);
			String minutes = Integer.toBinaryString(m);
			String seconds = Integer.toBinaryString(s);
			
			if (hours.length() < 6) {
				hours = padding.substring(0, 6 - hours.length()) + hours;
			}
			builder.append("H : ");
			builder.append(hours);
			builder.append(";");

			if (minutes.length() < 6) {
				 minutes = padding.substring(0, 6 - minutes.length()) + minutes;
			}
			builder.append("M : ");
			builder.append(minutes);
			builder.append(";");

			
			if (seconds.length() < 6) {
				 seconds = padding.substring(0, 6 - seconds.length()) + seconds;
			}

			builder.append("S : ");			
			builder.append(seconds);
			builder.append(";");
			
	        
	        
	        return builder.toString().split( ";" );
	    }	        
}
