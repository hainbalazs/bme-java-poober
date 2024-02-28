package hu.bme.aut.stepsysterv.PooBer.billing.data;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
* Functionality:
* - finding Accounts by id of its owner
* - finding Accounts in debt
* - persisting them (default)
* */
@Qualifier("accounts")
@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUid(Long user_id);

    List<Account> findByBalanceLessThan(long thresh);

    @Query(value="SELECT a from Account where uid = 0", nativeQuery = true)
    Account findMasterAccount();

    List<Account> findAllByBalanceLessThan(Long balance);
}
