package hu.bme.aut.stepsysterv.PooBer.wcManager.data;

import hu.bme.aut.stepsysterv.PooBer.users.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface WcRepository extends CrudRepository<Wc, Integer> {

    @Query("SELECT wc FROM Wc wc WHERE wc.ownerUID=?1")
    Optional<Wc> findByOwnerId(Long owner_id);
}
