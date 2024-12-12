package ru.practicum.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultRequestStatusDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
