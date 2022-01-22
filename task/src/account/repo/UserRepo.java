package account.repo;

import account.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Integer> {
    Optional<User> findUserByEmailIgnoreCase(String email);
    List<User> findAll();
}
