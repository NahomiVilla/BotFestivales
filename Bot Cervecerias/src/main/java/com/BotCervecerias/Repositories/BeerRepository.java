package com.BotCervecerias.Repositories;

import com.BotCervecerias.Models.Beers;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeerRepository extends CrudRepository<Beers,Integer> {

}
