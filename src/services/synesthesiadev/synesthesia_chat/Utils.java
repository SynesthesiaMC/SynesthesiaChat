package services.synesthesiadev.synesthesia_chat;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static final String IPV4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

	private static final Pattern pattern = Pattern.compile(IPV4_PATTERN);

	public static String chat(final String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static boolean ipChecker(final String msg) {
		Matcher matcher = pattern.matcher(msg);
		return matcher.matches();
	}
	
	public static boolean urlChecker(final String msg) {
		if(msg.contains("http") || msg.contains("www")) {
			return true;
		} else {
			return false;
		}
	}
	
    public static String formatTime(int secs) {
        int remainder = secs % 86400;

        int days 	= secs / 86400;
        int hours 	= remainder / 3600;
        int minutes	= (remainder / 60) - (hours * 60);
        int seconds	= (remainder % 3600) - (minutes * 60);

        String fDays 	= (days > 0 	? " " + days + " day" 		+ (days > 1 ? "s" : "") 	: "");
        String fHours 	= (hours > 0 	? " " + hours + " hour" 	+ (hours > 1 ? "s" : "") 	: "");
        String fMinutes = (minutes > 0 	? " " + minutes + " minute"	+ (minutes > 1 ? "s" : "") 	: "");
        String fSeconds = (seconds > 0 	? " " + seconds + " second"	+ (seconds > 1 ? "s" : "") 	: "");

        return new StringBuilder().append(fDays).append(fHours)
                .append(fMinutes).append(fSeconds).toString();
    }
	
}
