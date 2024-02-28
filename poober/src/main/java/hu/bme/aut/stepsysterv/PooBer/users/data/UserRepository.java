package hu.bme.aut.stepsysterv.PooBer.users.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for users to be stored in database
 * Apart from default repository functions, there is a function to find a user by its username (which is unique)
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.name=?1")
    User findByName(String name);
}