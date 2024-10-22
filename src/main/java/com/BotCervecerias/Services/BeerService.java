package com.BotCervecerias.Services;

import com.BotCervecerias.Models.Beers;
import com.BotCervecerias.Models.BeersType;
import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Repositories.BeerRepository;
import com.BotCervecerias.Repositories.BeersTypeRepository;
import com.BotCervecerias.Repositories.CompaniesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeerService {
    @Autowired

    private BeersTypeRepository beersTypeRepository;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private CompaniesRepository companiesRepository;

    public Beers registerBeer(Long company,Beers beer){
        Beers beers= new Beers();
        beers.setName(beer.getName());
        beers.setBeersTypeId(beer.getBeersTypeId());
        beers.setAlcohol_grad(beer.getAlcohol_grad());
        beers.setBtu(beer.getBtu());
        beers.setImage(beer.getImage());
        beers.setDescription(beer.getDescription());
        beers.setCompanies(company);
        return beerRepository.save(beers);
    }

    public List<Beers> getAllBeers(Long idUser){
        Optional<Companies> existingCompany = companiesRepository.findByUsers_Id(idUser);
        if (existingCompany.isEmpty()) {
            return null;
        }
        Long idCerveceria = existingCompany.get().getId();
        List<Beers>beersList=beerRepository.findAllByCompanies_Id(idCerveceria);

        return beersList;
    }

    public Beers updateInfoBeer(Beers beer,Beers nuevaInfo){

        beer.setName(nuevaInfo.getName());
        beer.setDescription(nuevaInfo.getDescription());
        beer.setImage(nuevaInfo.getImage());
        Optional<BeersType> type=beersTypeRepository.findByName(nuevaInfo.getBeersTypeName());
        if (type.isPresent()){
            beer.setBeersType(type.get());
        }
        //beer.setBeersTypeName(nuevaInfo.getBeersTypeName());

        return beerRepository.save(beer);
    }
}
