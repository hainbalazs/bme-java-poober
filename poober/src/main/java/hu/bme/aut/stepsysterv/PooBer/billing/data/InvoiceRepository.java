package hu.bme.aut.stepsysterv.PooBer.billing.data;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;

@Qualifier("invoices")
@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Invoice findByOwnerUIDAndUserUID(Long owner_id, Long user_id);
    List<Invoice> findByPayedFalse();
    List<Invoice> findByDateAfter(Date lastBillingPeriod);

    List<Invoice> findAllByUserUID(long uid);
}
