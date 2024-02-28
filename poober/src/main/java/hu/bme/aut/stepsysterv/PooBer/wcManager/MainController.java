package hu.bme.aut.stepsysterv.PooBer.wcManager;

import hu.bme.aut.stepsysterv.PooBer.billing.BillingAssistant;
import hu.bme.aut.stepsysterv.PooBer.billing.data.Invoice;
import hu.bme.aut.stepsysterv.PooBer.messagequeue.InvoiceSender;
import hu.bme.aut.stepsysterv.PooBer.users.data.User;
import hu.bme.aut.stepsysterv.PooBer.users.data.UserRepository;
import hu.bme.aut.stepsysterv.PooBer.wcManager.data.Reservation;
import hu.bme.aut.stepsysterv.PooBer.wcManager.data.ReservationRepository;
import hu.bme.aut.stepsysterv.PooBer.wcManager.data.Wc;
import hu.bme.aut.stepsysterv.PooBer.wcManager.data.WcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller	// This means that this class is a Controller
@RequestMapping(path="registry") // This means URL's start with /registry (after Application path)
public class MainController {

	Logger logger = LoggerFactory.getLogger(BillingAssistant.class);

	@Autowired
	private WcRepository wcRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private InvoiceSender invoiceSender;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(path="/add") // Map ONLY POST Requests
	public ResponseEntity<String> addNewWc (Principal p,
											@RequestParam String location,
											@RequestParam Integer price,
											@RequestParam String properties,
											@RequestParam boolean f1,
											@RequestParam boolean f2,
											@RequestParam boolean f3,
											@RequestParam boolean f4,
											@RequestParam boolean f5,
											@RequestParam boolean f6
	){
		Long ownerUID = userRepository.findByName(p.getName()).getId();
		if(!(hasAuthorization(p, ownerUID)))
			return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
		Wc wc = new Wc();
		wc.setOwnerUID(ownerUID);
		wc.setLocation(location);
		wc.setProperties(properties);
		wc.setReservedFor(null);
		wc.setPrice(price);
		setFeatures(wc, f1,f2,f3,f4,f5,f6);

		wcRepository.save(wc);
		logger.info("User with {} UID added new Wc with id: ", ownerUID, wc.getId());

		return new ResponseEntity<>("Saved", HttpStatus.CREATED);
	}

	private void setFeatures(Wc wc, boolean f1,boolean f2,boolean f3,boolean f4,boolean f5,boolean f6){
		wc.setF1(f1);
		wc.setF2(f2);
		wc.setF3(f3);
		wc.setF4(f4);
		wc.setF5(f5);
		wc.setF6(f6);
	}

	//Endpoint updating users own WC
	@PutMapping(path="/update")
	public ResponseEntity<String> updateWc (Principal p,
											@RequestParam String location,
											@RequestParam Integer price,
											@RequestParam String properties,
											@RequestParam boolean f1,
											@RequestParam boolean f2,
											@RequestParam boolean f3,
											@RequestParam boolean f4,
											@RequestParam boolean f5,
											@RequestParam boolean f6){

		Wc wc = wcRepository.findByOwnerId(userRepository.findByName(p.getName()).getId()).orElse(null);
		if(wc == null)
			return new ResponseEntity<>("", HttpStatus.NOT_FOUND);

		wc.setLocation(location);
		wc.setPrice(price);
		wc.setProperties(properties);
		setFeatures(wc, f1,f2,f3,f4,f5,f6);
		wcRepository.save(wc);
		return new ResponseEntity<>("", HttpStatus.OK);
	}


	//Endpoint requesting deletion of user's own WC
	@DeleteMapping(path="/delete")
	public ResponseEntity<String> deleteWc(Principal p){

		Wc wc = wcRepository.findByOwnerId(userRepository.findByName(p.getName()).getId()).orElse(null);
		if(wc == null){
			return new ResponseEntity<>("Delete unsuccesful", HttpStatus.BAD_REQUEST);
		}
		wcRepository.deleteById(wc.getId());
		logger.info("Wc with id {} removed", wc.getId());

		return new ResponseEntity<>("Deleted", HttpStatus.OK);
	}

	@GetMapping(path="/id/{wcId}")
	public @ResponseBody Optional<Wc> getWcById(@PathVariable Integer wcId){
		logger.info("Single wc data requested");

		return wcRepository.findById(wcId);
	}

	//Endpoint requesting data about authenticated user's WC
	@GetMapping(path="/mywc")
	public @ResponseBody Optional<Wc> getMyWc(Principal p){
		logger.info("User's own wc data requested");

		return wcRepository.findByOwnerId(userRepository.findByName(p.getName()).getId());
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<Wc> getAllWcs() {
		logger.info("All wc data requested");
		Iterable<Wc> allwc=  wcRepository.findAll();
		return allwc;
	}

	@GetMapping(path="/id/{wcId}/reserve")
	public ResponseEntity<String> reserveWcById(Principal p, @PathVariable Integer wcId){
		if(reservationRepository.findByWcId(wcId) != null) {
			logger.warn("Wc already taken");
			return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
		}

		Long userUID = userRepository.findByName(p.getName()).getId();
		Reservation r = new Reservation();
		r.setStart(new Timestamp(System.currentTimeMillis()));
		r.setReservedFor(userUID);
		r.setWcId(wcId);
		reservationRepository.save(r);
		logger.info("{} reserved wc {}", userUID, r.getWcId());

		return new ResponseEntity<>("", HttpStatus.ACCEPTED);
	}

	@GetMapping(path="/myreservation")
	public @ResponseBody Reservation myReservation(Principal p){
		User u = userRepository.findByName(p.getName());
		Reservation r = reservationRepository.findByReservedFor(u.getId());

		if(r == null){
			logger.debug("Queried reservation was not found.");
			r = new Reservation();
			r.setWcId(-1);

		}
		return r;
	}

	@PostMapping(path="/door/{wcId}")
	public ResponseEntity<String> invoice(@PathVariable Integer wcId){
		Wc wc = wcRepository.findById(wcId).orElse(null);
		if(wc == null){
			logger.error("Wrong wc id provided for pooped endpoint");
			return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
		}

		Reservation r = reservationRepository.findByWcId(wcId);

		if(r == null) {
			logger.info("Door opened, no active reservation");
			return new ResponseEntity<>("", HttpStatus.OK);
		}

		if(r.getFirstOpen() == null){
			r.setFirstOpen(new Timestamp(System.currentTimeMillis()));
			reservationRepository.save(r);
			logger.info("{} started using wc {}", r.getReservedFor(), r.getWcId());
			return new ResponseEntity<>("", HttpStatus.OK);

		}

		long duration = System.currentTimeMillis() - r.getFirstOpen().getTime();
		logger.info("Door opened within 5 seconds since start, ignoring");
		if (duration < 5000) 	return new ResponseEntity<>("", HttpStatus.OK);


		/* 10s = 1 unit price */
		long units = duration / 10000;

		Invoice invoice = new Invoice(wc.getOwnerUID(), r.getReservedFor(), new Date(), (int) duration, (int) (units * wc.getPrice()));

		invoiceSender.sendInvoice(invoice);
		reservationRepository.deleteById(r.getId());
		logger.info("{} finished using wc {}, invoice sent", r.getReservedFor(), r.getWcId());
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@PostMapping(path="/topup")
	public @ResponseBody String topup(Principal p, @RequestParam Integer amount){
		Long userUID = userRepository.findByName(p.getName()).getId();
		invoiceSender.sendTopup(userUID, amount);
		logger.info("{} topped up {}", userUID, amount);
		return "topped up amount: " + amount;
	}

	private boolean hasAuthorization(Principal principal, Long UID){
		if(userRepository.findByName(principal.getName()).getId() == UID) return true;
		return false;
	}


}
