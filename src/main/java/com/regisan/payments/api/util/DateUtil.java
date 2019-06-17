package com.regisan.payments.api.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static Date getNextDueDate(Date eventDate, int dueDate, int daysBeforeDueDate) {

        Instant instant = Instant.ofEpochMilli(eventDate.getTime());
        LocalDateTime futureDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDate future = futureDateTime.toLocalDate().plusDays(daysBeforeDueDate);

        LocalDate endBillingCycle = LocalDate.of(future.getYear(), future.getMonth(), dueDate);

        if (future.compareTo(endBillingCycle) > 0)
            return Date.from(endBillingCycle.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return Date.from(endBillingCycle.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
