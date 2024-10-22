package com.BotCervecerias.Services;

import com.BotCervecerias.Models.*;
import com.BotCervecerias.Repositories.BeerRepository;
import com.BotCervecerias.Repositories.CompaniesRepository;
import com.BotCervecerias.Repositories.EventBreweryRepository;
import com.BotCervecerias.Repositories.EventsRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompaniesService {
    @Autowired
    private CompaniesRepository companiesRepository;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private EventBreweryRepository eventBreweryRepository;
    @Autowired
    private EventsRepository eventsRepository;

    public Companies updateCompanyProfile(Long id, Companies profileDetails) throws IllegalAccessException {
        Companies companyProfile = companiesRepository.findById(id)
                .orElseThrow(() -> new IllegalAccessException("Company profile not found for this id :: " + id));
        companyProfile.setName(profileDetails.getName());
        companyProfile.setLogo(profileDetails.getLogo());
        companyProfile.setAddres(profileDetails.getAddres());
        return companiesRepository.save(companyProfile);
    }

    public  void deleteCompany(Optional<Users> user, Long id){
        if (user.isPresent()){
            if (user.get().getUsersTypeId().equals(1L)){
                List<Beers>cervezas=beerRepository.findAllByCompanies_Id(id);
                beerRepository.deleteAll(cervezas);
                List<EventBreweries>eventos=eventBreweryRepository.findAllByBrewer_Id(id);
                eventBreweryRepository.deleteAll(eventos);
            } else if (user.get().getUsersTypeId().equals(2L)) {
                List<Events>eventsList=eventsRepository.findAllByOrganizer_Id(id);
                List<EventBreweries>eventosOrg=eventBreweryRepository.findAllByEventsIn(eventsList);
                eventBreweryRepository.deleteAll(eventosOrg);
                eventsRepository.deleteByOrganizer_Id(id);

            }
        }
        companiesRepository.deleteById(id);
    }
}
