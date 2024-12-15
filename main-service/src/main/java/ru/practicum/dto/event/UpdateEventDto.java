package ru.practicum.dto.event;

import lombok.*;

@Setter
@Getter
public class UpdateEventDto extends UpdateEventBase {
    private StateAction stateAction;
}
