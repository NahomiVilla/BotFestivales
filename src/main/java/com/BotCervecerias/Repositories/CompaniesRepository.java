package com.BotCervecerias.Repositories;

import com.BotCervecerias.Models.Companies;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CompaniesRepository extends CrudRepository<Companies,Long> {
    Optional<Companies> findByUsers_Id(Long userId);
    List<Companies> findByNameIn(List<String> names);
    Optional<Companies> findByName(String name);
    List<Companies>findAllByIdIn(List<Long> cervecerias);

}
