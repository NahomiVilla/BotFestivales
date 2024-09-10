package com.BotCervecerias.Services;

import com.BotCervecerias.Models.Beers;
import com.BotCervecerias.Repositories.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeerService {

    @Autowired
    private BeerRepository beerRepository;


    public Beers registerBeer(Beers beers){
        //if (userRepository.findByEmail(users.getEmail()).isPresent()){
          //  throw  new RuntimeException("el correo ya está en uso.");
        //}

        return beerRepository.save(beers);
    }
}
