package ru.practicum.controller.privetapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.ResultRequestStatusDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController {
     RequestService requestService;

     @PostMapping("/{user-id}/requests")
     public RequestDto createRequest(@PathVariable("user-id") Long id,
                                     @RequestParam Long eventId) {
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
     public List<ResultRequestStatusDto> getRequestsByUserId(@PathVariable("user-id") Long id) {
         log.info("Выполнение getRequestsByUserId");
         return requestService.getRequestsByUserId(id);
     }
}