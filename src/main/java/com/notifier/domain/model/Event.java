package com.notifier.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private String title;
    private String description;
    private LocalDateTime dateTime;
    private boolean isAllDay;


    public Event(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.dateTime = LocalDateTime.of(date, LocalDateTime.MIN.toLocalTime());
        this.isAllDay = true;
    }

    public Event(String title, String description, LocalDateTime dateTime) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.isAllDay = false;
    }


}