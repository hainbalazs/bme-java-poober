package hu.bme.aut.stepsysterv.PooBer.billing.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerUID;
    private Long userUID;
    private Date date;
    private long duration;
    private int price;
    private Boolean payed;

    public Invoice(){
        this.ownerUID = 0L;
        this.userUID = 0L;
        this.date = null;
        this.duration = 0;
        this.price = 0;
        this.payed = false;
    }

    public Invoice(Long ownerUID, Long userUID, Date date, long duration, int price) {
        this.ownerUID = ownerUID;
        this.userUID = userUID;
        this.date = date;
        this.duration = duration;
        this.price = price;
        this.payed = false;
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerUID() {
        return ownerUID;
    }

    public Long getUserUID() {
        return userUID;
    }

    public Date getDate() {
        return date;
    }

    public long getDuration() {
        return duration;
    }

    public int getPrice() {
        return price;
    }

    public Boolean getPayed() { return payed; }

    public void payBill() {
        payed = true;
    }
}
