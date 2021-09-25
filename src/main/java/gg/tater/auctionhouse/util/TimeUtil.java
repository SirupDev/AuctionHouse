package gg.tater.auctionhouse.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeUtil {

    public String formatStamp(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
                .withZone(odt.getOffset());
        return formatter.format(instant);
    }

    public String formatTime(long stamp) {
        long millis = stamp - System.currentTimeMillis();

        long seconds = millis / 1000;
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;
        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            String length = days > 1 ? "days" : "day";
            builder.append(days).append(" ").append(length).append(", ");
        }

        if (hours > 0) {
            String length = hours > 1 ? "hours" : "hour";
            builder.append(hours).append(" ").append(length).append(", ");
        }
        if (minutes > 0) {
            String length = minutes > 1 ? "minutes" : "minute";
            builder.append(minutes).append(" ").append(length).append(", ");
        }
        if (sec > 0) {
            String length = sec > 1 ? "seconds" : "second";
            builder.append(sec).append(" ").append(length).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

    public String formatTime(int seconds) {
        if (seconds <= 0) {
            return "Expired.";
        }

        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;
        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            String length = days > 1 ? "days" : "day";
            builder.append(days).append(" ").append(length).append(", ");
        }

        if (hours > 0) {
            String length = hours > 1 ? "hours" : "hour";
            builder.append(hours).append(" ").append(length).append(", ");
        }
        if (minutes > 0) {
            String length = minutes > 1 ? "minutes" : "minute";
            builder.append(minutes).append(" ").append(length).append(", ");
        }
        if (sec > 0) {
            String length = sec > 1 ? "seconds" : "second";
            builder.append(sec).append(" ").append(length).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }
}
