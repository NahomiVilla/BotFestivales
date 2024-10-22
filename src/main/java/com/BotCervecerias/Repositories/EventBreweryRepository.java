package com.BotCervecerias.Repositories;

import com.BotCervecerias.Models.EventBreweries;
import com.BotCervecerias.Models.Events;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventBreweryRepository extends CrudRepository<EventBreweries,Long> {
    List<EventBreweries> findAllByBrewer_Id(Long id);
    List<EventBreweries>findAllByEventsIn(List<Events>events);
    void deleteByBrewer_Id(Long id);
}
