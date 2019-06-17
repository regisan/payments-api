package com.regisan.payments.api.util;

import org.junit.Test;

import java.time.Month;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class DateUtilTest {

    private static final int DUE_DATE = 10;
    private static final int DAYS_BEFORE_DUE_DATE = 7;

    @Test
    public void dueDateSameBillingCycleButPreviousMonth() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.MAY.getValue(), 25);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JUNE.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void dueDateSameBillingCycleAndSameMonth() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.JUNE.getValue(), 2);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JUNE.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void dueDateNextBillingCycle() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.JUNE.getValue(), 5);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JULY.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void dueDateEndOfFebruary() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.FEBRUARY.getValue(), 28);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.MARCH.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void dueDateEndOfTheYear() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2018, Month.DECEMBER.getValue(), 31);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JANUARY.getValue(), dueDate.get(Calendar.MONTH));
        assertEquals(2019, dueDate.get(Calendar.YEAR));
    }
}
