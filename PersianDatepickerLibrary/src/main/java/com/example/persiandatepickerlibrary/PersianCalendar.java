package com.example.persiandatepickerlibrary;
import android.annotation.SuppressLint;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * PersianCalendar represents a class which will convert your date to shamsi when you give time as milli sec
 *
 * @author : zahra fatehi
 * @version : 0:0
 */
public class PersianCalendar extends Calendar {

    public static final int AH = 1;
    public static final int BH = 0;
    public static final int ESFAND = 11;
    private final static int EPOCH_OFFSET = 492268;
    private final static int BASE_YEAR = 1349;
    private final static int[] FIXED_DATES = new int[]{
            492347, // False   ,  1349
            492712, // True   ,  1350
            493078, // False   ,  1351
            493443, // False   ,  1352
            493808, // False   ,  1353
            494173, // True   ,  1354
            494539, // False   ,  1355
            494904, // False   ,  1356
            495269, // False   ,  1357
            495634, // True   ,  1358
            496000, // False   ,  1359
            496365, // False   ,  1360
            496730, // False   ,  1361
            497095, // True   ,  1362
            497461, // False   ,  1363
            497826, // False   ,  1364
            498191, // False   ,  1365
            498556, // True   ,  1366
            498922, // False   ,  1367
            499287, // False   ,  1368
            499652, // False   ,  1369
            500017, // True   ,  1370
            500383, // False   ,  1371
            500748, // False   ,  1372
            501113, // False   ,  1373
            501478, // False   ,  1374
            501843, // True   ,  1375
            502209, // False   ,  1376
            502574, // False   ,  1377
            502939, // False   ,  1378
            503304, // True   ,  1379
            503670, // False   ,  1380
            504035, // False   ,  1381
            504400, // False   ,  1382
            504765, // True   ,  1383
            505131, // False   ,  1384
            505496, // False   ,  1385
            505861, // False   ,  1386
            506226, // True   ,  1387
            506592, // False   ,  1388
            506957, // False   ,  1389
            507322, // False   ,  1390
            507687, // True   ,  1391
            508053, // False   ,  1392
            508418, // False   ,  1393
            508783, // False   ,  1394
            509148, // True   ,  1395
            509514, // False   ,  1396
            509879, // False   ,  1397
            510244, // False   ,  1398
            510609, // True   ,  1399
            510975, // False   ,  1400
            511340, // False   ,  1401
            511705, // False   ,  1402
            512070, // False   ,  1403
            512435, // True   ,  1404
            512801, // False   ,  1405
            513166, // False   ,  1406
            513531, // False   ,  1407
            513896, // True   ,  1408
            514262, // False   ,  1409
            514627, // False   ,  1410
            514992, // False   ,  1411
            515357, // True   ,  1412
            515723, // False   ,  1413
            516088, // False   ,  1414
            516453, // False   ,  1415
            516818, // True   ,  1416
            517184, // False   ,  1417
    };
    private final static int[] ACCUMULATED_DAYS_IN_MONTH = new int[]{
            0, 31, 62, 93, 124, 155,
            186, 216, 246, 276, 306, 336};
    private final static int[] MINIMUMS = new int[]{
            0, 1, 0, 1, 0, 1, 1, 1, 1, 0,
            0, 0, 0, 0, 0, -13 * 3600 * 1000, 0};

    private static int[] MAXIMUMS = new int[]{
            1, 292278994, 11, 53, 6, 31,
            366, 7, 6, 1, 11, 23, 59, 59, 999, 14 * 3600 * 1000, 7200000};

    private static int[] LEAST_MAXIMUMS = new int[]{
            1, 292269054, 11, 50, 3,
            28, 355, 7, 3, 1, 11, 23, 59, 59, 999, 50400000, 1200000};

    private final static long ONE_SECOND_IN_MILLIS = 1000,
            ONE_MINUTE_IN_MILLIS = 60 * ONE_SECOND_IN_MILLIS,
            ONE_HOUR_IN_MILLIS = 60 * ONE_MINUTE_IN_MILLIS,
            ONE_DAY_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS;
    private int fixedDate = EPOCH_OFFSET;


    @IntDef(value = {
            Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR,
            Calendar.WEEK_OF_MONTH, Calendar.DAY_OF_MONTH, Calendar.DAY_OF_YEAR,
            Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK_IN_MONTH, Calendar.AM_PM, Calendar.HOUR,
            Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND,
            Calendar.ZONE_OFFSET, Calendar.DST_OFFSET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Fields {
    }

    /**
     * Constructs a new {@code PersianCalendar} initialized to the current date and
     * time with the default {@code Locale} and {@code TimeZone}.
     */
    public PersianCalendar() {
        this(TimeZone.getDefault(), Locale.getDefault());
    }


    /**
     * Constructs a new {@code PersianCalendar} initialized to the current date and
     * time and using the specified {@code TimeZone} and {@code Locale}.
     *
     * @param timezone the {@code TimeZone}.
     * @param locale   the {@code Locale}.
     */
    public PersianCalendar(TimeZone timezone, Locale locale) {
        super(timezone, locale);
        setTimeInMillis(System.currentTimeMillis());
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void add(@Fields int field, int value) {
        if (value == 0) {
            return;
        }
        if (field < 0 || field >= ZONE_OFFSET) {
            throw new IllegalArgumentException();
        }

        if (field == ERA) {
            complete();
            if (fields[ERA] == AH) {
                if (value >= 0) {
                    return;
                }
                set(ERA, BH);
            } else {
                if (value <= 0) {
                    return;
                }
                set(ERA, AH);
            }
            complete();
            return;
        }

        if (field == YEAR || field == MONTH) {
            complete();
            if (field == MONTH) {
                int month = fields[MONTH] + value;
                if (month < 0) {
                    value = (month - 11) / 12;
                    month = 12 + (month % 12);
                } else {
                    value = month / 12;
                }
                set(MONTH, month % 12);
            }
            set(YEAR, fields[YEAR] + value);
            int days = daysInMonth(isLeapYear(fields[YEAR], fields[ERA] == AH), fields[MONTH]);
            if (fields[DATE] > days) {
                set(DATE, days);
            }
            complete();
            return;
        }

        long multiplier = 0;
        getTimeInMillis(); // Update the time
        switch (field) {
            case MILLISECOND:
                time += value;
                break;
            case SECOND:
                time += value * 1000L;
                break;
            case MINUTE:
                time += value * 60000L;
                break;
            case HOUR:
            case HOUR_OF_DAY:
                time += value * 3600000L;
                break;
            case AM_PM:
                multiplier = 43200000L;
                break;
            case DAY_OF_MONTH:
            case DAY_OF_YEAR:
            case DAY_OF_WEEK:
                multiplier = 86400000L;
                break;
            case WEEK_OF_YEAR:
            case WEEK_OF_MONTH:
            case DAY_OF_WEEK_IN_MONTH:
                multiplier = 604800000L;
                break;
        }

        if (multiplier == 0) {
            areFieldsSet = false;
            complete();
            return;
        }

        long delta = value * multiplier;

        /*
         * Attempt to keep the hour and minute constant when we've crossed a DST
         * boundary and the user's units are AM_PM or larger. The typical
         * consequence is that calls to add(DATE, 1) will add 23, 24 or 25 hours
         * depending on whether the DST goes forward, constant, or backward.
         *
         * We know we've crossed a DST boundary if the new time will have a
         * different timezone offset. Adjust by adding the difference of the two
         * offsets. We don't adjust when doing so prevents the change from
         * crossing the boundary.
         */
        int zoneOffset = getTimeZone().getRawOffset();
        int offsetBefore = getOffset(time + zoneOffset);
        int offsetAfter = getOffset(time + zoneOffset + delta);
        int dstDelta = offsetBefore - offsetAfter;
        if (getOffset(time + zoneOffset + delta + dstDelta) == offsetAfter) {
            delta += dstDelta;
        }

        time += delta;
        areFieldsSet = false;
        complete();
    }

    @Override
    protected void computeFields() {
        long timeInZone = time + getOffset(time);
        fixedDate = ((int) Math.floor(timeInZone * 1d / ONE_DAY_IN_MILLIS)) + EPOCH_OFFSET;
        fields[YEAR] = getYearFromFixedDate(fixedDate);
        if (fields[YEAR] <= 0) {
            fields[YEAR] = -fields[YEAR] + 1;
            fields[ERA] = BH;
        } else {
            fields[ERA] = AH;
        }
        int far1 = getFixedDateFar1(fields[YEAR], fields[ERA] == AH);
        fields[DAY_OF_YEAR] = fixedDate - far1 + 1;
        if (fields[DAY_OF_YEAR] < ACCUMULATED_DAYS_IN_MONTH[6]) {
            fields[MONTH] =
                    (int) Math.floor((fields[DAY_OF_YEAR] - 1) / 31d); // month range is 0-11
        } else {
            fields[MONTH] = (int) Math
                    .floor((fields[DAY_OF_YEAR] - 1 - ACCUMULATED_DAYS_IN_MONTH[6]) / 30d) + 6;
        }
        fields[DAY_OF_MONTH] = fields[DAY_OF_YEAR] - ACCUMULATED_DAYS_IN_MONTH[fields[MONTH]];

        long extra = timeInZone - ((fixedDate - EPOCH_OFFSET) * ONE_DAY_IN_MILLIS);
        fields[HOUR_OF_DAY] = (int) Math.floor(extra * 1d / ONE_HOUR_IN_MILLIS);
        if (fields[HOUR_OF_DAY] >= 12) {
            fields[HOUR] = fields[HOUR_OF_DAY] - 12;
            fields[AM_PM] = PM;
        } else {
            fields[HOUR] = fields[HOUR_OF_DAY] - 12;
            fields[AM_PM] = AM;
        }

        extra -= fields[HOUR_OF_DAY] * ONE_HOUR_IN_MILLIS;
        fields[MINUTE] = (int) Math.floor(extra * 1d / ONE_MINUTE_IN_MILLIS);
        extra -= fields[MINUTE] * ONE_MINUTE_IN_MILLIS;
        fields[SECOND] = (int) Math.floor(extra * 1d / ONE_SECOND_IN_MILLIS);
        extra -= fields[SECOND] * ONE_SECOND_IN_MILLIS;
        fields[MILLISECOND] = (int) extra;

        fields[DAY_OF_WEEK] = fixedDate >= 0 ? (fixedDate + 4) % 7 + 1 : (fixedDate - 1) % 7 + 7;
    }

    @Override
    protected void computeTime() {
        // Time is the reference and
        if (!isSet(YEAR) || !isSet(MONTH)) {
            return;
        }
        if (fields[YEAR] == 0) {
            throw new IllegalArgumentException("Year cannot be zero");
        }
        if (!isSet(ERA)) {
            fields[ERA] = AH;
        }
        int extraYear = (int) Math.floor(fields[MONTH] / 12d);
        if (extraYear != 0) {
            if (fields[ERA] == AH ^ extraYear > 0) {
                if (fields[ERA] == AH && fields[YEAR] <= Math.abs(extraYear)) {
                    fields[YEAR] = Math.abs(extraYear) - fields[YEAR] + 1;
                    set(ERA, BH);
                } else if (fields[ERA] == BH && fields[YEAR] <= Math.abs(extraYear)) {
                    fields[YEAR] = Math.abs(extraYear) - fields[YEAR] + 1;
                    set(ERA, AH);
                } else if (fields[ERA] == AH) {
                    fields[YEAR] += extraYear; // the same as -= Math.abs(extraYear)
                } else {
                    fields[YEAR] -= extraYear; // the same as += Math.abs(extraYear)
                }
            } else {
                fields[YEAR] += Math.abs(extraYear);
            }
        }
        fields[MONTH] %= 12; // months of a year is a fixed number (12)
        if (fields[MONTH] < 0) {
            fields[MONTH] += 12; // month range is 0-11
        }

        int fixedDate = getFixedDateFar1(fields[YEAR], fields[ERA] == AH) +
                ACCUMULATED_DAYS_IN_MONTH[fields[MONTH]] +
                (isSet(DAY_OF_MONTH) ? fields[DAY_OF_MONTH] - 1 : 0);

        int timezoneOffset = -getOffset(fixedDate * ONE_DAY_IN_MILLIS);
        time = (fixedDate - EPOCH_OFFSET) * ONE_DAY_IN_MILLIS + ONE_HOUR_IN_MILLIS +
                (
                        isSet(HOUR_OF_DAY) ? fields[HOUR_OF_DAY] : (
                                isSet(HOUR) && isSet(AM_PM) ?
                                        (fields[HOUR] + (fields[AM_PM] == AM ? 0 : 12)) : 0)) *
                        ONE_HOUR_IN_MILLIS +
                (isSet(MINUTE) ? fields[MINUTE] : 0) * ONE_MINUTE_IN_MILLIS + timezoneOffset +
                (isSet(SECOND) ? fields[SECOND] : 0) * ONE_SECOND_IN_MILLIS +
                (isSet(MILLISECOND) ? fields[MILLISECOND] : 0);
        areFieldsSet = false;
    }


    /**
     * Compares the given object to this {@code Calendar} and returns whether they are
     * equal. The object must be an instance of {@code Calendar} and have the same
     * properties.
     *
     * @return {@code true} if the given object is equal to this {@code Calendar}, {@code false}
     * otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Calendar)) {
            return false;
        }
        Calendar cal = (Calendar) object;
        return getTimeInMillis() == cal.getTimeInMillis()
                && get(YEAR) == cal.get(YEAR)
                && get(MONTH) == cal.get(MONTH)
                && get(DAY_OF_MONTH) == cal.get(DAY_OF_MONTH)
                && get(HOUR) == cal.get(HOUR)
                && get(MINUTE) == cal.get(MINUTE)
                && get(SECOND) == cal.get(SECOND)
                && get(MILLISECOND) == cal.get(MILLISECOND);
    }

    @Override
    public int getActualMinimum(int field) {
        return getMinimum(field);
    }

    /**
     * Returns the maximum value of the given field for the current date.
     * For example, the maximum number of days in the current month.
     */
    @Override
    public int getActualMaximum(int field) {
        return getMaximum(field);
    }

    /**
     * Returns the minimum value of the given field for the current date.
     */
    @Override
    public int getGreatestMinimum(@Fields int field) {
        return MINIMUMS[field];
    }

    @Override
    public int getLeastMaximum(@Fields int field) {
        return LEAST_MAXIMUMS[field];
    }

    @Override
    public int getMaximum(@Fields int field) {
        return MAXIMUMS[field];
    }

    @Override
    public int getMinimum(@Fields int field) {
        return MINIMUMS[field];
    }

    @Override
    public void roll(@Fields int field, boolean increment) {
        throw new IllegalArgumentException("Not supported");
    }

    private int getOffset(long localTime) {
        return getTimeZone().getOffset(localTime);
    }

    /* To find the year that associated with fixedDat. */
    private int getYearFromFixedDate(int fd) {
        int testYear;
        boolean testAfterH = fd > 0;
        if (testAfterH) {
            testYear = (int) Math.floor(Math.round((fd - 1) / 365.24219)) + 1;
        } else {
            testYear = (int) Math.floor(Math.round(fd / 365.24219));
        }
        if (testYear == 0) {
            testYear = 1;
            testAfterH = true;
        }
        int far1 = getFixedDateFar1(Math.abs(testYear), testAfterH);
        if (far1 <= fd) {
            if (testYear <= 0) {
                return testYear + 1;
            } else {
                return testYear;
            }
        } else {
            // last year of testYear and try to convert it to include zero
            if (testYear <= -1) {
                return testYear;
            } else {
                return testYear - 1;
            }
        }
    }

    /* To find the fixedDate of first day of year. Farvardin 1, 1 must have fixedDate of one. */
    public static int getFixedDateFar1(int year, boolean afterH) {
        if (year <= 0) {
            throw new IllegalArgumentException("Year cannot be negative or zero. Year: " + year);
        }
        if (afterH && year >= BASE_YEAR && year < BASE_YEAR + FIXED_DATES.length - 1) {
            return FIXED_DATES[year - BASE_YEAR];
        }
        // The detail can be found in en.wikibook.com
        int realYear;
        if (afterH) {
            realYear = year - 1;
        } else {
            realYear = -year;
        }

        int days = 1029983 * ((int) Math.floor((realYear + 38) / 2820d));
        int cycle = (realYear + 38) % 2820;
        if (cycle < 0) {
            cycle += 2820;
        }

        days += Math.floor((cycle - 38) * 365.24219) + 1;

        double extra = cycle * 0.24219;
        int frac = getIntegerPart((extra - Math.floor(extra)) * 1000);

        int lastYear = year - 1;
        boolean lastYearAfterH = afterH;
        if (afterH && year == 1) {
            lastYear = 1;
            lastYearAfterH = false;
        } else if (!afterH) {
            lastYear = year + 1;
        }
        if (isLeapYear(lastYear, lastYearAfterH) && frac <= 202) {
            days++;
        }
        return days;
    }

    public static int daysInMonth(boolean leapYear, int month) {
        if (month < 0 || month > ESFAND) {
            throw new IllegalArgumentException();
        }
        if (month == ESFAND) {
            if (leapYear) {
                return 30;
            } else {
                return 29;
            }
        }

        return ACCUMULATED_DAYS_IN_MONTH[month + 1] - ACCUMULATED_DAYS_IN_MONTH[month];
    }

    /**
     * @param year   a year in persian calendar
     * @param afterH is after Hijra
     * @return Returns true if {@code year} is a leap year.
     */
    public static boolean isLeapYear(int year, boolean afterH) {
        // The detail can be found in en.wikibook.com
        double realYear0, realYear1;
        if (afterH) {
            realYear0 = realYear(year, true);
            realYear1 = realYear(year + 1, true);
        } else {
            realYear0 = realYear(year, false);
            realYear1 = realYear(year - 1, false);
        }

        double extraDaysOfOneYear = 0.24219d; // 0.24219 ~ extra days of one year
        double delta = 0.025;
        double leapDays0 = realYear0 * extraDaysOfOneYear + delta;
        double leapDays1 = realYear1 * extraDaysOfOneYear + delta;
        double frac0 = getIntegerPart((leapDays0 - getIntegerPart(leapDays0)) * 1000);
        double frac1 = getIntegerPart((leapDays1 - getIntegerPart(leapDays1)) * 1000);
        int criticalPoint = 266;
        return frac0 <= criticalPoint && frac1 > criticalPoint;
    }

    public static int realYear(int year, boolean afterH) {
        if (afterH) {
            return mod(year + 38, 2820);
        } else {
            return mod(-year + 39, 2820);
        }
    }

    private static int mod(int i, int j) {
        int m = i % j;
        if (m < 0) {
            return m + j;
        } else {
            return m;
        }
    }

    /* To get integer part of a float */
    private static int getIntegerPart(double d) {
        if (d >= 0) {
            return (int) Math.floor(d);
        } else {
            return (int) Math.floor(d) + 1;
        }
    }
}