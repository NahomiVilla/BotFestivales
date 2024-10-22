package com.BotCervecerias.Controllers;

import com.BotCervecerias.Models.Beers;
import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Models.Events;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.BeerRepository;
import com.BotCervecerias.Repositories.CompaniesRepository;
import com.BotCervecerias.Repositories.UserRepository;
import com.BotCervecerias.Services.BeerService;
import com.BotCervecerias.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/beer")
public class BeerController {
    @Autowired
    private BeerService beerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompaniesRepository companiesRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private BeerRepository beerRepository;

    @PostMapping("/BeerRegister")
    public ResponseEntity<?> registerBeer(@RequestBody Beers beers, @RequestHeader("Authorization") String token){
        System.out.println("iniciando registro...");
        String jwtToken = token.replace("Bearer ", "");

        String email = jwtUtils.getEmailToken(jwtToken);
        Optional<Users>users=userRepository.findByEmail(email);
        System.out.println("verificando usuario...");
        if (users.isPresent()){
            System.out.println(" usuario existe...");
            Long idUser=users.get().getId();
            Optional<Companies>existingCompany=companiesRepository.findByUsers_Id(idUser);
            Beers newBeer;
            if (existingCompany.isPresent()){

                Long company=existingCompany.get().getId();
                try {
                    newBeer=beerService.registerBeer(company,beers);
                    return ResponseEntity.ok(newBeer);
                }catch (Exception e){
                    return ResponseEntity.badRequest().body("Error: "+e.getMessage());
                }
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/VerInfoBeer/{name}")
    public  ResponseEntity<Beers>getBeer(@PathVariable String name){
        try {
            Beers cerveza= beerRepository.findByName(name);
            return new ResponseEntity<>(cerveza, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/AllBeersInfo")
    public ResponseEntity<List<Beers>> getAllBeers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<Users> userModel = userRepository.findByEmail(authentication.getName());
            List<Beers> beersList = new ArrayList<>();
            if (userModel.isPresent()) {
                Long idUser = userModel.get().getId();
                List<Beers>listService=beerService.getAllBeers(idUser);
                beersList.addAll(listService);
            }
            return new ResponseEntity<>(beersList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/DeleteBeer/{nombreCerveza}")
    public  ResponseEntity<?> deleteBeer(@PathVariable String nombreCerveza){
        Beers beer=beerRepository.findByName(nombreCerveza);
        System.out.println(beer);
        beerRepository.delete(beer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/EditInfoBeer/{nameBeer}")
    public  ResponseEntity<?> editInfo(@PathVariable String nameBeer ,@RequestBody  Beers nuevaInfoBeer){
        System.out.println("iniciando solicitud put...");
        try{
            Beers beer=beerRepository.findByName(nameBeer);
            System.out.println(beer);
            if (beer==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            System.out.println("iniciando service...");
            Beers updateBeer=beerService.updateInfoBeer(beer,nuevaInfoBeer);
            return new ResponseEntity<>(updateBeer,HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
