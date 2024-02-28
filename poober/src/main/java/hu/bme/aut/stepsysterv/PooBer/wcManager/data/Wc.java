package hu.bme.aut.stepsysterv.PooBer.wcManager.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.File;

@Entity // This tells Hibernate to make a table out of this class
public class Wc {


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id=1;

	public Integer getId() {
		return id;
	}


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public File getPics() {
		return pics;
	}

	public void setPics(File pics) {
		this.pics = pics;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public Long getReservedFor() {
		return reservedFor;
	}

	public void setReservedFor(Long reservedFor) {
		this.reservedFor = reservedFor;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Long getOwnerUID() {
		return ownerUID;
	}

	public void setOwnerUID(Long ownerUID) {
		this.ownerUID = ownerUID;
	}

	public boolean isF1() {		return f1;	}

	public void setF1(boolean f1) {
		this.f1 = f1;
	}

	public boolean isF2() {
		return f2;
	}

	public void setF2(boolean f2) {
		this.f2 = f2;
	}

	public boolean isF3() {
		return f3;
	}

	public void setF3(boolean f3) {
		this.f3 = f3;
	}

	public boolean isF4() {
		return f4;
	}

	public void setF4(boolean f4) {
		this.f4 = f4;
	}

	public boolean isF5() {
		return f5;
	}

	public void setF5(boolean f5) {
		this.f5 = f5;
	}

	public boolean isF6() {
		return f6;
	}

	public void setF6(boolean f6) {
		this.f6 = f6;
	}

	private boolean f1;
	private boolean f2;
	private boolean f3;
	private boolean f4;
	private boolean f5;
	private boolean f6;


	private Long ownerUID;

	private String location;

	private File pics;

	private String properties;

	private Long reservedFor;

	private Integer price;




}
