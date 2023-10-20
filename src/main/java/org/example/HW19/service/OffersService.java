package org.example.HW19.service;

import org.example.HW19.entity.Offers;
import org.example.HW19.entity.Orders;
import org.example.HW19.exceptions.DateAndTimeException;
import org.example.HW19.exceptions.LessProposedPriceException;
import org.example.HW19.exceptions.OffersNotFoundException;
import org.example.HW19.repository.OffersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OffersService {

    private final OffersRepository offersRepository;

    public OffersService(OffersRepository offersRepository) {
        this.offersRepository = offersRepository;
    }

    public Offers save(Offers offers, Orders order) {
        if (offers.getProposedPrice() < order.getUnderDuty().getBasePrice()) {
            throw new LessProposedPriceException("your bid price is lower than the base price.");
        } else if (offers.getSuggestedTime().isBefore(LocalDateTime.now())) {
            throw new DateAndTimeException("the entered date is less than today's date.");
        } else if (offers.getSuggestedTime().isBefore(order.getDateAndTime())) {
            throw new DateAndTimeException("The suggested time to start the work is less than the time entered by the customer.");
        } else {
            return offersRepository.save(offers);
        }
    }

    public List<Offers> findAllByOrderId(Long orderId) {
        return offersRepository.findAllByOrderId(orderId);
    }

    public Offers findById(long id) {
        return offersRepository.findById(id).orElseThrow(() -> new OffersNotFoundException("no offer found with this ID."));
    }

    public Offers getOffer(Long orderId, Long offerId) {
        return offersRepository.findByOrderId(orderId, offerId);
    }
}
