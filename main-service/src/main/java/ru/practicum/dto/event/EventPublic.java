package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.Constants;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventPublic {

    Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
    String rangeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
    String rangeEnd;

    Boolean onlyAvailable;

    String sort;

    @Min(0)
    Integer from = 0;

    @Min(1)
    Integer size = 10;

    String text;

    List<Long> categories;
}
