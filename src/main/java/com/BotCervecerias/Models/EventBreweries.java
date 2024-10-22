package com.BotCervecerias.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="events_breweries")
@JsonIgnoreProperties({"events"})
public class EventBreweries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Companies brewer;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Events events;

    @Override
    public String toString() {
        return "EventBreweries{id=" + id + ", brewer=" + (brewer != null ? brewer.getName() : "null") +
                ", events=" + (events != null ? events.getName() : "null") + "}";
    }

}
