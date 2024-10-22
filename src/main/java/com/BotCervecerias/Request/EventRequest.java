package com.BotCervecerias.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {
    private String name;
    private String description;
    private Date date;
    private String direction;
    private List<Long> breweriesIds;
}
