package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;
}
