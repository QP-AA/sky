package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        // 判断地址和购物车是否为空
        AddressBook address = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (address == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getById(BaseContext.getCurrentId());
        if (shoppingCarts == null && shoppingCarts.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 向订单对象中插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis())); // 使用当前时间戳作为订单号
        orders.setPhone(address.getPhone());
        orders.setConsignee(address.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());

        orderMapper.insert(orders); // 向订单中插入数据

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, detail);
            detail.setOrderId(orders.getId());
            orderDetails.add(detail);
        }
        orderDetailMapper.insertBatch(orderDetails); // 订单明细中批量插入数据

        shoppingCartMapper.clean(BaseContext.getCurrentId()); // 删除购物车中数据

        OrderSubmitVO submitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return submitVO;
    }
}
