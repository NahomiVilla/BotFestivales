package com.BotCervecerias.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private String direction;
    @ManyToOne
    @JoinColumn(name = "companies")
    @ToString.Exclude
    private Companies organizer;

    private Boolean active;

    @ManyToMany

    @JoinTable(name="events_breweries",joinColumns = @JoinColumn(name="event_id"),inverseJoinColumns = @JoinColumn(name = "company_id"))
    @JsonIgnore
    @ToString.Exclude
    private List<Companies>breweries;




    public List<Long> getBreweriesIds() {
        return breweries != null ?new ArrayList<>( breweries.stream().map(Companies::getId).toList()) : null;
    }

    public void setBreweriesByIds(List<Long> breweriesIds) {
        this.breweries = breweriesIds.stream().map(id -> {
            Companies company = new Companies();
            company.setId(id);
            return company;
        }).collect(Collectors.toList());
    }
    public List<String> getBreweriesNames() {
        return breweries != null ?new ArrayList<>( breweries.stream()
                .map(Companies::getName)
                .toList()) : null;
    }
    public void setBreweriesNames(List<String> breweriesNames){
        this.breweries = breweriesNames.stream().map(name -> {
            Companies company = new Companies();
            company.setName(name);
            return company;
        }).collect(Collectors.toList());
    }
    public void setOrganizerName(String organizer) {
        if (this.organizer == null) {
            this.organizer = new Companies();
        }
        this.organizer.setCompanyName(organizer);
    }


}
