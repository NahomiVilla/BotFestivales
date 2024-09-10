package com.BotCervecerias.Models;

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
@Table(name="events-breweries")
public class EventBreweries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "companies")
    private Companies brewer;
    @ManyToOne
    @JoinColumn(name = "events")
    private Events events;

    public Long getBrewer() {
        return brewer != null ? brewer.getId():null;
    }
    public void setBrewer(Long brewer) {
        if (this.brewer == null) {
            this.brewer = new Companies();
        }
        this.brewer.setId(brewer);
    }

    public Long getEvents() {
        return events != null ? events.getId():null;
    }
    public void setEvents(Long event) {
        if (this.events == null) {
            this.events = new Events();
        }
        this.events.setId(event);
    }
}
