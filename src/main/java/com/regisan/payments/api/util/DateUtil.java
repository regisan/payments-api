package com.regisan.payments.api.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static Date getNextDueDate(Date eventDate, int dueDate, int daysBeforeDueDate) {

        LocalDate future = convert(eventDate).plusDays(daysBeforeDueDate);

        LocalDate endBillingCycle = LocalDate.of(future.getYear(), future.getMonth(), dueDate);

        if (future.compareTo(endBillingCycle) > 0)
            return Date.from(endBillingCycle.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return Date.from(endBillingCycle.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date addMonth(Date date, int months) {
        LocalDate localDate = convert(date);
        return Date.from(localDate.plusMonths(months).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate convert(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
    }
}
