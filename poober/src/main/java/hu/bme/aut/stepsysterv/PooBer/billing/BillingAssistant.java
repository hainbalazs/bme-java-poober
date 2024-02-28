package hu.bme.aut.stepsysterv.PooBer.billing;

import hu.bme.aut.stepsysterv.PooBer.billing.data.Account;
import hu.bme.aut.stepsysterv.PooBer.billing.data.AccountRepository;
import hu.bme.aut.stepsysterv.PooBer.billing.data.Invoice;
import hu.bme.aut.stepsysterv.PooBer.billing.data.InvoiceRepository;
import hu.bme.aut.stepsysterv.PooBer.messagequeue.Action;
import hu.bme.aut.stepsysterv.PooBer.messagequeue.BannedSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BillingAssistant{
    Logger logger = LoggerFactory.getLogger(BillingAssistant.class);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    private BannedSender bannedSender;


    public BillingAssistant(BannedSender sender) {
        bannedSender = sender;
    }

    public void createInvoice(Invoice created){
        invoiceRepository.save(created);
        logger.info("New Invoice has been created between {} and {}.", created.getOwnerUID(), created.getUserUID());
    }

    public void createAccount(Account createdAccount) {
        accountRepository.save(createdAccount);
        logger.info("New Account has been created for {}.", createdAccount.getUid());
    }

    // Sec, Min, Hour, Day, Month, DayofTheWeak, (Year)
    @Scheduled(cron="30 * * * * *")
    public void scheduledTransfer() {
        logger.info("Starting scheduled task.");
        logger.info("#1 Transferring payments at the end of the billing period.");
        Account master = accountRepository.findByUid(1L);
        List<Invoice> billsToPay = invoiceRepository.findByPayedFalse();
        logger.debug("Schedule: billsToPay size = {}", billsToPay.size());
        for(Invoice i : billsToPay){
            logger.info("Unpayed invoice has been found between {} and {} for {}.", i.getOwnerUID(), i.getUserUID(), i.getPrice());
            long ownerId = i.getOwnerUID();
            long userId = i.getUserUID();
            Account ownerAcc = accountRepository.findByUid(ownerId);
            Account userAcc = accountRepository.findByUid(userId);
            logger.info("Original balances: {} - {}, {} - {}, Master - {}.", ownerId, ownerAcc.getBalance(), userId, userAcc.getBalance(), master.getBalance());
            Account.transferFunds_withCommission(userAcc, ownerAcc, master, i.getPrice());
            logger.info("The money ({}) has been transferred successfully.", i.getPrice());
            logger.info("New balances: {} - {}, {} - {}, Master - {}.", ownerId, ownerAcc.getBalance(), userId, userAcc.getBalance(), master.getBalance());
            i.payBill();
            accountRepository.save(ownerAcc);
            accountRepository.save(userAcc);
            accountRepository.save(master);
            invoiceRepository.save(i);
        }

        logger.info("#2 Issuing bans for users in debt.");
        List<Account> inDebt = accountRepository.findAllByBalanceLessThan(0L);
        logger.info("{} users are currently in debt.", inDebt.size());
        for (Account a : inDebt) {
            long uid = a.getUid();
            logger.info("Ban has been issued for user {}.", uid);
            bannedSender.send(uid, Action.BANNED);
        }
    }

    public void topUp(long uid, int amount) {
        Account toTopUp = accountRepository.findByUid(uid);
        if(toTopUp == null){
            logger.error("No account with uid {} was found in the database.", uid);
        }

        long originalBalance = toTopUp.getBalance();
        toTopUp.topUpFunds(amount);
        accountRepository.save(toTopUp);
        logger.info("Top up with amount of {} has been completed successfully for user {}", amount, uid);

        if(originalBalance + amount >= 0 && originalBalance < 0){
            logger.info("User {} has payed of his debt, requesting unban.", uid);
            bannedSender.send(uid, Action.UNBANNED);
        }
    }
}
