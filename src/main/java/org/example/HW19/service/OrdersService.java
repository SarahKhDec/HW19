package org.example.HW19.service;

import org.example.HW19.entity.Offers;
import org.example.HW19.entity.Orders;
import org.example.HW19.entity.enumeration.OrderStatus;
import org.example.HW19.exceptions.DateAndTimeException;
import org.example.HW19.exceptions.LessProposedPriceException;
import org.example.HW19.exceptions.OrderNotFoundException;
import org.example.HW19.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;

    private final OffersService offersService;

    public OrdersService(OrdersRepository ordersRepository, OffersService offersService) {
        this.ordersRepository = ordersRepository;
        this.offersService = offersService;
    }

    public Orders save(Orders orders) {
        if (orders.getProposedPrice() < orders.getUnderDuty().getBasePrice()) {
            throw new LessProposedPriceException("your bid price is lower than the base price.");
        } else if (orders.getDateAndTime().isBefore(LocalDateTime.now())) {
            throw new DateAndTimeException("the entered date is less than today's date.");
        } else {
            return ordersRepository.save(orders);
        }
    }

    public List<Orders> findAllByUnderDutyIdAndStatus(Long underDutyId) {
        return ordersRepository.findAllByUnderDutyIdAndOrderStatus(underDutyId);
    }

    public Orders findById(long id) {
        return ordersRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("no order found with this ID."));
    }

    public void addOffers(Orders orders, Offers offers) {
        orders.addOffers(offers);
    }

    public Orders findOrderInOrdersList(Long underDutyId, Long orderId) {
        return ordersRepository.findByUnderDutyIdAndOrderStatus(underDutyId, orderId);
    }

    public Orders update(Orders orders) {
        return ordersRepository.save(orders);
    }

    public Orders selectOfferForOrder(Long orderId, Long offerId) {
        Orders order = findById(orderId);
        Offers offers = offersService.getOffer(orderId, offerId);
        assert offers != null;
        order.setExpert(offers.getExpert());
        return update(order);
    }

    public void updateOrderStatusToStarted(Long orderId, Long offerId) {
        Orders order = findById(orderId);
        Offers offer = offersService.getOffer(orderId, offerId);
        if (LocalDateTime.now().isBefore(offer.getSuggestedTime())) {
            throw new DateAndTimeException("You cannot change the status of a task to 'Started' before it starts.");
        } else {
            order.setOrderStatus(OrderStatus.STARTED);
            update(order);
        }
    }

    public void updateOrderStatusToDone(Long orderId, Long offerId) {
        Orders order = findById(orderId);
        Offers offer = offersService.getOffer(orderId, offerId);
        if (LocalDateTime.now().isBefore(offer.getSuggestedTime())) {
            throw new DateAndTimeException("You cannot change the status of a task to 'Done' before it starts.");
        } else {
            order.setOrderStatus(OrderStatus.DONE);
            update(order);
        }
    }
}
