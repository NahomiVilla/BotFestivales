package com.BotCervecerias.Repositories;

import com.BotCervecerias.Models.Beers;

import com.BotCervecerias.Models.Companies;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerRepository extends CrudRepository<Beers,Long> {
    List<Beers> findAllByCompanies_Id(Long user);
    Beers findByName(String name);

}
