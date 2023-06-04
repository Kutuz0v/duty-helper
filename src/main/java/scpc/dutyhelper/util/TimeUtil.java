package scpc.dutyhelper.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String calculateTimeDifference(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);

        long years = duration.toDays() / 365;
        duration = duration.minusDays(years * 365);
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();

        List<String> parts = new ArrayList<>();
        if (years != 0) {
            parts.add(years + " р.");
        }
        if (days != 0) {
            parts.add(days + " д.");
        }
        if (hours != 0) {
            parts.add(hours + " г.");
        }
        if (minutes != 0) {
            parts.add(minutes + " хв.");
        }

        return String.join(", ", parts);
    }

    public static String getTimeDifference(Date start_date,
                                           Date end_date) {

        long difference_In_Time
                = end_date.getTime() - start_date.getTime();

        long difference_In_Seconds
                = TimeUnit.MILLISECONDS
                .toSeconds(difference_In_Time)
                % 60;

        long difference_In_Minutes
                = TimeUnit
                .MILLISECONDS
                .toMinutes(difference_In_Time)
                % 60;

        long difference_In_Hours
                = TimeUnit
                .MILLISECONDS
                .toHours(difference_In_Time)
                % 24;

        long difference_In_Days
                = TimeUnit
                .MILLISECONDS
                .toDays(difference_In_Time)
                % 365;

        long difference_In_Years
                = TimeUnit
                .MILLISECONDS
                .toDays(difference_In_Time)
                / 365L;

        return (difference_In_Years != 0 ? difference_In_Years + " р., " : "") +
                (difference_In_Days != 0 ? difference_In_Days + " д., " : "") +
                (difference_In_Hours != 0 ? difference_In_Hours + " г., " : "") +
                (difference_In_Minutes != 0 ? difference_In_Minutes + " хв., " : "") +
                (difference_In_Seconds != 0 ? difference_In_Seconds + " сек.    , " : "");
    }
}
