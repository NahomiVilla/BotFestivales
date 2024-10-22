package com.BotCervecerias.Repositories;

import com.BotCervecerias.Models.Events;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EventsRepository extends CrudRepository<Events,Long> {
    Optional<Events> findByName(String name);
    List<Events>findAllByOrganizer_Id(Long id);
    void deleteByOrganizer_Id(Long id);

}
