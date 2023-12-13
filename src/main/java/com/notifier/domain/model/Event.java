package com.notifier.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Event {

    private String title;
    private String description;
    private LocalDateTime dateTime;
    private boolean isAllDay;

}