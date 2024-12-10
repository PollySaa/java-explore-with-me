package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.Constants;
import ru.practicum.dto.location.LocationDto;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventDto {

    @Nullable
    @Size(min = 20, max = 2000)
    String annotation;

    @Nullable
    @Size(min = 20, max = 7000)
    String description;

    @Nullable
    Long category;

    @Nullable
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
    LocalDateTime eventDate;

    @Nullable
    LocationDto location;

    @Nullable
    Boolean paid;

    @Nullable
    @PositiveOrZero
    Integer participantLimit;

    @Nullable
    Boolean requestModeration;

    @Nullable
    @Size(min = 3, max = 500)
    String title;

    StateAction stateAction;
}
