package everyos.discord.exobot.util;

public class TimeUtil {
    public static String formatTime(double seconds) {
        StringBuilder str = new StringBuilder();

        int hours = (int) Math.floor(seconds / (60 * 60));
        seconds = seconds % (60 * 60);
        int minutes = (int) Math.floor(seconds / 60);
        seconds = seconds % 60;

        if (hours > 0 && hours < 10) str.append("0");
        if (hours > 0) str.append(hours + ":");
        if (minutes < 10) str.append("0");
        str.append(minutes + ":");
        if (seconds < 10) str.append("0");
        str.append((int) seconds);


        return str.toString();
    }
}