package hu.bme.aut.stepsysterv.PooBer.wcManager.data;

import hu.bme.aut.stepsysterv.PooBer.users.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.wcId=?1")
    Reservation findByWcId(Integer wcId);

    @Query("SELECT r FROM Reservation r WHERE r.reservedFor=?1")
    Reservation findByReservedFor(Long reservedFor);
}
