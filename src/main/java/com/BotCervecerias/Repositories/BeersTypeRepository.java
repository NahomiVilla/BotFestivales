package com.BotCervecerias.Repositories;

import com.BotCervecerias.Models.Beers;
import com.BotCervecerias.Models.BeersType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BeersTypeRepository extends CrudRepository<BeersType,Long> {
    Optional<BeersType> findByName(String name);
}
