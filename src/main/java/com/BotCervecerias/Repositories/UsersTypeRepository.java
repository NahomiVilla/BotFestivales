package com.BotCervecerias.Repositories;

import com.BotCervecerias.Models.UsersType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersTypeRepository extends CrudRepository<UsersType,Long> {
}
