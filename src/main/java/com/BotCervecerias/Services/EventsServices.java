package com.BotCervecerias.Services;

import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Models.EventBreweries;
import com.BotCervecerias.Models.Events;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.CompaniesRepository;
import com.BotCervecerias.Repositories.EventBreweryRepository;
import com.BotCervecerias.Repositories.EventsRepository;
import com.BotCervecerias.Request.EventRequest;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventsServices {
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private CompaniesRepository companiesRepository;
    @Autowired
    private EventBreweryRepository eventBreweryRepository;


    public Events registerEvents(EventRequest events, Long companyId) {
        if (eventsRepository.findByName(events.getName()).isPresent()) {
            throw new RuntimeException("Ya existe un evento con ese nombre... intente con otro nombre");
        }

        Events event = new Events();
        event.setName(events.getName().toUpperCase());
        event.setDate(events.getDate());
        event.setDescription(events.getDescription());
        event.setDirection(events.getDirection());
        event.setActive(true);


        Companies organizer = companiesRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa organizadora no encontrada"));
        if (organizer.getId() == null) {
            organizer = companiesRepository.save(organizer);
        }
        event.setOrganizer(organizer); // Establecer el organizador


        event = eventsRepository.save(event);

        List<Long> breweriesIds = events.getBreweriesIds();
        if (breweriesIds != null && !breweriesIds.isEmpty()) {
            List<Companies> breweries = companiesRepository.findAllByIdIn(breweriesIds);
            for (Companies brewery : breweries) {
                // Crear la relación en EventBreweries
                EventBreweries eventBrewery = new EventBreweries();
                eventBrewery.setEvents(event);
                eventBrewery.setBrewer(brewery);
                eventBreweryRepository.save(eventBrewery);  // Guardar en la tabla intermedia
            }
            event.setBreweries(breweries);
        }
        return event;
    }

    public void changeStatus(Events events,Boolean status){

        if (status.equals(true)){
            events.setActive(true);
        }else {
            events.setActive(false);
        }
        eventsRepository.save(events);
    }

    public Events editEvent(Events evento,Events nuevaInfo){
        evento.setName(nuevaInfo.getName());
        evento.setDirection(nuevaInfo.getDirection());
        evento.setDate(nuevaInfo.getDate());
        evento.setDescription(nuevaInfo.getDescription());
        return eventsRepository.save(evento);
    }

    public Events aggBrewery(Events evento,Companies company){
        List<Companies> brewerie = evento.getBreweries();
        System.out.println(brewerie);
        System.out.println(brewerie);
        if (brewerie == null) {
            evento.setBreweries(new ArrayList<>());
        }else {
            brewerie.add(company);
        }
        return eventsRepository.save(evento);
    }
    public void deleteBrewery(Long eventId,Long id){
        Optional<Events> optionalEvent = eventsRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Events event = optionalEvent.get();
            // Remove the brewery from the event's breweries list
            event.getBreweries().removeIf(brewery -> brewery.getId().equals(id));
            // Save the updated event
            eventsRepository.save(event);
            //eventBreweryRepository.deleteByBrewer_Id(id);
        }else {
            throw new RuntimeException("Event not found");
        }
    }

    public Events getEvent(String name){
        try {
            Optional<Events> evento= eventsRepository.findByName(name);
            if (evento.isEmpty()) {
                return null;
            }
            Events events=evento.get();
            return events;
        } catch (Exception e) {
            return null;
        }
    }
}
