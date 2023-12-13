package com.notifier.infrastructure.controller;

import com.notifier.application.dto.EventDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/events")
@Tag(name = "EventController", description = "Operations related to events")
public interface EventController {

    @Operation(summary = "Get today's events from Google Calendar", description = "Returns a list of events for today from Google Calendar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation. Returns the list of today's events."),
            @ApiResponse(responseCode = "201", description = "Event created successfully."),
            @ApiResponse(responseCode = "204", description = "No content. The operation was successful, but there is no content to return."),
            @ApiResponse(responseCode = "400", description = "Bad request. Check the input parameters."),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication is required, or credentials are invalid."),
            @ApiResponse(responseCode = "403", description = "Forbidden. You do not have permission to access this operation."),
            @ApiResponse(responseCode = "404", description = "Not found. No events for today."),
            @ApiResponse(responseCode = "500", description = "Internal server error. Check the logs for more details.")
    })
    @GetMapping("/today")
    List<EventDTO> getEventsByDaysOffset();
}
