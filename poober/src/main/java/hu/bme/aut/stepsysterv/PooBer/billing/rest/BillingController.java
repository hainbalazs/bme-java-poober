package hu.bme.aut.stepsysterv.PooBer.billing.rest;


import hu.bme.aut.stepsysterv.PooBer.billing.data.Account;
import hu.bme.aut.stepsysterv.PooBer.billing.data.AccountRepository;
import hu.bme.aut.stepsysterv.PooBer.billing.data.Invoice;
import hu.bme.aut.stepsysterv.PooBer.billing.data.InvoiceRepository;
import hu.bme.aut.stepsysterv.PooBer.wcManager.data.Wc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller    // This means that this class is a Controller
@RequestMapping(path="billing") // This means URL's start with /registry (after Application path)
public class BillingController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping(path="/all")
    public @ResponseBody
    Iterable<Invoice> getAllWcs() {
        return invoiceRepository.findAll();
    }

    @GetMapping(path="/invoice/{uid}")
    public @ResponseBody
    Iterable<Invoice> getUserInvoice(@PathVariable Integer uid) {
        return invoiceRepository.findAllByUserUID((long) uid);
    }

    @GetMapping(path="/masterbalance")
    public @ResponseBody
    Long getMasterAccount() {
        Account master = accountRepository.findByUid(1L);
        return master.getBalance();
    }

    @GetMapping(path="/balance/{uid}")
    public @ResponseBody String getUserBalance(@PathVariable Integer uid){
        Account account = accountRepository.findByUid((long)uid);
        return account.getBalance().toString();
    }


}
