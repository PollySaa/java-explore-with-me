package ru.practicum.controller.privetapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.service.request.RequestService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController {
     RequestService requestService;

     @PostMapping("/{user-id}/requests")
     @ResponseStatus(HttpStatus.CREATED)
     public RequestDto createRequest(@PathVariable("user-id") Long id,
                                     @RequestParam(required = false) Long eventId) {
         log.info("Выполнение createRequest");
         return requestService.createRequest(id, eventId);
     }

     @PatchMapping("/{user-id}/requests/{request-id}/cancel")
     public RequestDto cancelRequest(@PathVariable("user-id") Long id,
                                     @PathVariable("request-id") Long requestId) {
         log.info("Выполнение cancelRequest");
         return requestService.cancelRequest(id, requestId);
     }

     @GetMapping("/{user-id}/requests")
     public List<RequestDto> getRequestsByUserId(@PathVariable("user-id") Long id) {
         log.info("Выполнение getRequestsByUserId");
         List<RequestDto> requestDto = new ArrayList<>();
         List<Request> requests = requestService.getRequestsByUserId(id);
         for (Request request : requests) {
             requestDto.add(RequestMapper.toRequestDto(request));
         }
         return requestDto;
     }
}
