package com.rishi.springbootecom.service;


import com.rishi.springbootecom.model.Order;
import com.rishi.springbootecom.model.OrderItem;
import com.rishi.springbootecom.model.Product;
import com.rishi.springbootecom.model.dto.OrderItemRequest;
import com.rishi.springbootecom.model.dto.OrderItemResponse;
import com.rishi.springbootecom.model.dto.OrderRequest;
import com.rishi.springbootecom.model.dto.OrderResponse;
import com.rishi.springbootecom.repo.OrderRepo;
import com.rishi.springbootecom.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductRepo pRepo;
    @Autowired
    private OrderRepo oRepo;

    public OrderResponse placeOrder(OrderRequest request) {
        Order order=new Order();
        String orderId="ORD"+ UUID.randomUUID().toString().substring(0,8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");

        List<OrderItem> orderItems=new ArrayList<>();
        for(OrderItemRequest itemReq: request.items()) {
            Product product = pRepo.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));
            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
            pRepo.save(product);
            OrderItem orderItem=OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())))
                    .order(order)
                    .build();
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        Order savedOrder = oRepo.save(order);
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for(OrderItem item: order.getOrderItems()){
            OrderItemResponse orderItemResponse=new OrderItemResponse(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getTotalPrice()
            );
            itemResponses.add(orderItemResponse);
        }
        OrderResponse orderResponse= new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getCustomerName(),
                savedOrder.getEmail(),
                savedOrder.getStatus(),
                savedOrder.getOrderDate(),
                itemResponses);
        return orderResponse;
    }

    public List<OrderResponse> getAllOrdersResponses() {
        List<Order> orders=oRepo.findAll();
        List<OrderResponse> orderResponses=new ArrayList<>();
        for(Order order: orders){
            List<OrderItemResponse> itemResponses=new ArrayList<>();
            for(OrderItem item: order.getOrderItems()){
                OrderItemResponse orderItemResponse=new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getTotalPrice()
                );
                itemResponses.add(orderItemResponse);
            }
            OrderResponse orderResponse=new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    itemResponses
            );
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }
}
