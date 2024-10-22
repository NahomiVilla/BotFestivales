package com.BotCervecerias.Controllers;

import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Models.Events;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.CompaniesRepository;
import com.BotCervecerias.Repositories.EventsRepository;
import com.BotCervecerias.Repositories.UserRepository;
import com.BotCervecerias.Request.EventRequest;
import com.BotCervecerias.Services.EventsServices;
import com.BotCervecerias.security.jwt.JwtUtils;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventsController {
    @Autowired
    private EventsServices eventsServices;
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompaniesRepository companiesRepository;
    @Autowired
    private JwtUtils jwtUtils;


    @PostMapping("/register")
    public ResponseEntity<?> EventsRegister(@RequestBody EventRequest events, @RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", "");

        String email = jwtUtils.getEmailToken(jwtToken);
        Optional<Users> users=userRepository.findByEmail(email);
        if (users.isPresent()) {
            Long idUser = users.get().getId();
            Optional<Companies> existingCompany = companiesRepository.findByUsers_Id(idUser);
            Events newEvent;
            if (existingCompany.isPresent()) {
                Long company = existingCompany.get().getId();
                try {
                     newEvent = eventsServices.registerEvents(events,company);

                    return ResponseEntity.ok(newEvent);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Error: " + e.getMessage());
                }
            }
        }


        return ResponseEntity.status(403).body("Unauthorized");
    }
    @PostMapping("/getInfoEvent")
    public ResponseEntity <Events> findEventByName(@RequestBody String Name) {
        try {
            Optional<Events> evento= eventsRepository.findByName(Name);
            if (evento.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Events events=evento.get();
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getInfoAllEvents")
    public ResponseEntity <List<Events>> getInfoAllEvents(@RequestHeader("Authorization") String token) {
        System.out.println("iniciando obtencion de eventos");
        String jwtToken = token.replace("Bearer ", "");

        String email = jwtUtils.getEmailToken(jwtToken);
        Optional<Users> users=userRepository.findByEmail(email);
        if (users.isPresent()) {
            Long idUser = users.get().getId();
            Optional<Companies> existingCompany = companiesRepository.findByUsers_Id(idUser);

            if (existingCompany.isPresent()) {
                Long company = existingCompany.get().getId();

                List<Events> eventos = eventsRepository.findAllByOrganizer_Id(company);
                for (Events evento :eventos){
                    if (evento.getActive()==null){
                        evento.setActive(true);
                    }
                }
                if (eventos.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(eventos, HttpStatus.OK);

            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<?> ChangeStatus(@PathVariable Long id, @RequestParam Boolean status){
        Optional<Events>  evento2=eventsRepository.findById(id);
        if (evento2.isPresent()) {
            Events evento=evento2.get();
            eventsServices.changeStatus(evento,status);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);


    }

    @PutMapping("/editEvent/{id}")
    public  ResponseEntity<Events> EditEvent(@PathVariable Long id,@RequestBody Events nuevaInfo){
        Optional<Events> events=eventsRepository.findById(id);
        if (events.isPresent()){
            Events evento=events.get();
            Events eventoActualizado=eventsServices.editEvent(evento,nuevaInfo);
            return new ResponseEntity<>(eventoActualizado,HttpStatus.OK);
        }return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/aggBrewery/{idEvent}")
    public ResponseEntity<?> aggBrewery(@PathVariable Long idEvent,@RequestBody Companies company){
        System.out.println("iniciando preoceso de adicion...");
        Optional<Events> event=eventsRepository.findById(idEvent);
        if (event.isPresent()) {
            Events evento=event.get();
            eventsServices.aggBrewery(evento,company);
            return new ResponseEntity<>(HttpStatus.OK);
        }return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteBrewery/{idcompany}")
    public  ResponseEntity<?> deleteBrewery(@RequestParam Long eventId,@PathVariable Long idcompany){

        Optional<Companies> company=companiesRepository.findById(idcompany);
        System.out.println(company);
        if (company.isPresent()){
            eventsServices.deleteBrewery(eventId,idcompany);
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Cervecería no encontrada", HttpStatus.NOT_FOUND);
        }


    }


    @DeleteMapping("/deleteEvent/{name}")
    public ResponseEntity<?> deleteEvent(@PathVariable String name){
        Optional<Events> evento=eventsRepository.findByName(name);
        if (evento.isPresent()){
            Events event=evento.get();
            eventsRepository.deleteById(event.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
