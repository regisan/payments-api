package com.regisan.payments.api.util;

import org.junit.Test;

import java.time.Month;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class DateUtilTest {

    private static final int DUE_DATE = 10;
    private static final int DAYS_BEFORE_DUE_DATE = 7;

    @Test
    public void testDueDateSameBillingCycleButPreviousMonth() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.MAY.getValue(), 25);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JUNE.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void testDueDateSameBillingCycleAndSameMonth() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.JUNE.getValue(), 2);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JUNE.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void testDueDateNextBillingCycle() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.JUNE.getValue(), 5);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JULY.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void testDueDateEndOfFebruary() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2019, Month.FEBRUARY.getValue(), 28);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.MARCH.getValue(), dueDate.get(Calendar.MONTH));
    }

    @Test
    public void testDueDateEndOfTheYear() {

        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2018, Month.DECEMBER.getValue(), 31);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.getNextDueDate(eventDate.getTime(), DUE_DATE, DAYS_BEFORE_DUE_DATE));

        assertEquals(DUE_DATE, dueDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(Month.JANUARY.getValue(), dueDate.get(Calendar.MONTH));
        assertEquals(2019, dueDate.get(Calendar.YEAR));
    }

    @Test
    public void testAddMonth() {

        Calendar date = Calendar.getInstance();
        date.set(2019, Month.DECEMBER.getValue(), 10);

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(DateUtil.addMonth(date.getTime(), 1));

        assertEquals(Month.JANUARY.getValue(), dueDate.get(Calendar.MONTH));
    }
}
