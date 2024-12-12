package ru.practicum.constants;

import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final Sort ORDER_BY_EVENT_DAY = Sort.by(Sort.Direction.ASC, "eventDate");
}
