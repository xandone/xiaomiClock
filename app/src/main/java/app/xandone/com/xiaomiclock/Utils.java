package app.xandone.com.xiaomiclock;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xandone on 2017/1/3.
 */
public class Utils {

    public static int dp2px(Context context, int values) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

    public String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd");
        return format.format(new Date());
    }

    public static int getMillisecond(Calendar calendar) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MILLISECOND);
    }

    public static int getSecond(Calendar calendar) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.SECOND);
    }

    public static int getMinute(Calendar calendar) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MINUTE);
    }

    public static int getClock(Calendar calendar) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.HOUR) % 12;
    }

}
