package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> query(ShoppingCart shoppingCar);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart cart);

    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "VALUES (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{id}")
    List<ShoppingCart> getById(Long currentId);

    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void clean(Long currentId);
}
