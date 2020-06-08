package everyos.discord.luwu.util;

public class TimeUtil {
	public static String formatTime(long time) {
        StringBuilder str = new StringBuilder();

        int hours = getHours(time, false);
        int minutes = getMinutes(time, true);
        int seconds = getSeconds(time, true);

        if (hours > 0 && hours < 10) str.append("0");
        if (hours > 0) str.append(hours + ":");
        if (minutes < 10) str.append("0");
        str.append(minutes + ":");
        if (seconds < 10) str.append("0");
        str.append(seconds);


        return str.toString();
    }
	
	public static int getDays(long time) {
		return (int)(time/1000/60/60/24);
	}
	public static int getHours(long time, boolean wdays) {
		int hours = (int)(time/1000/60/60);
		return wdays?hours%24:hours;
	}
	public static int getMinutes(long time, boolean whours) {
		int mins = (int)(time/1000/60);
		return whours?mins%60:mins;
	}
	public static int getSeconds(long time, boolean wmins) {
		int secs = (int)(time/1000);
		return wmins?secs%60:secs;
	}
}