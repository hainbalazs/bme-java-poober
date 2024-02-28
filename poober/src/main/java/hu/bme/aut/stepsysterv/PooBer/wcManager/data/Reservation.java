package hu.bme.aut.stepsysterv.PooBer.wcManager.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    public Long getReservedFor() {
        return reservedFor;
    }

    public void setReservedFor(Long reservedFor) {
        this.reservedFor = reservedFor;
    }

    public Integer getWcId() {
        return wcId;
    }

    public void setWcId(Integer wcId) {
        this.wcId = wcId;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    private Long reservedFor;

    private Integer wcId;

    private Timestamp start;

    public Timestamp getFirstOpen() {
        return firstOpen;
    }

    public void setFirstOpen(Timestamp firstOpen) {
        this.firstOpen = firstOpen;
    }

    private Timestamp firstOpen;


    public Long getId() {
        return id;
    }
}
