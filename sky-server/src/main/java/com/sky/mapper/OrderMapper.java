package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select count(*) from orders where status = #{id};")
    Integer statistics(Integer id);

    @Select("select * from orders where status = #{toBeConfirmed} and order_time < #{now};")
    List<Orders> getByStatusAndOrderTime(Integer toBeConfirmed, LocalDateTime now);

    @Select("select * from orders where status = #{deliveryInProgress};")
    List<Orders> getByStatus(Integer deliveryInProgress);

    @Select("select COALESCE(sum(amount), 0) from orders where order_time > #{beginTime} and order_time < #{endTime} and status = #{status}")
    Double statisticsAmount(LocalDateTime beginTime, LocalDateTime endTime, Integer status);
}
