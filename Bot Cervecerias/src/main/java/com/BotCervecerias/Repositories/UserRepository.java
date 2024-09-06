package com.BotCervecerias.Repositories;
import com.BotCervecerias.Models.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends CrudRepository<Users,Integer>{
    Optional<Users>findByCorreo(String correo);
}
