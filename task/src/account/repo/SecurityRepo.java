package account.repo;

import account.model.SecurityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityRepo extends CrudRepository<SecurityEvent, Integer> {
}
