package pl.edu.pja.cardsplayground2.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pja.cardsplayground2.entities.UserEntity;

import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long> {


    Optional<UserEntity> findByUsername(String username);

}
