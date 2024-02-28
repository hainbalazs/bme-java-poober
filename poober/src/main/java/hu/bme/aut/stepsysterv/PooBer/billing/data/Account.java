package hu.bme.aut.stepsysterv.PooBer.billing.data;

import javax.persistence.*;

@Entity
public class Account {
    /// unique id for each account
    /// id of the account's owner
    // might be class User uid
    @Id
    private Long uid;

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    /// balance of the account
    // we assume it is always in the same currency, if we want to support others we can make the conversion during the top-up phase
    private Long balance;

    /// ** constructor
    public Account() {
        balance = 0L;
    }

    /// ** basic getter functions
    public Long getUid() {
        return uid;
    }

    public Long getBalance() {
        return balance;
    }

    /// ** functions for fund manipulation
    public void topUpFunds(long amount) {
        balance += amount;
    }

    // private since the function should never be used by itself
    private void withdrawFunds(long amount){
        balance -= amount;
    }

    public static void transferFunds(Account from, Account to, long amount){
        from.withdrawFunds(amount);
        to.topUpFunds(amount);
    }

    public static void transferFunds_withCommission(Account from, Account to, Account master, long amount){
        from.withdrawFunds(amount);
        long commission = Math.round(amount * 0.1);
        master.topUpFunds(commission);
        to.topUpFunds(amount - commission);
    }
}
