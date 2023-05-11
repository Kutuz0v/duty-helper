package scpc.dutyhelper.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
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
                (difference_In_Seconds != 0 ? difference_In_Seconds + " сек., " : "");
    }
}
