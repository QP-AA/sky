package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    public void insertBatch(List<DishFlavor> flavors);

    @Delete("delete from dish_flavor where dish_id = #{DishId}")
    public void deleteByDishId(Long DishId);

    public void deleteByDishIds(List<Long> dishIds);

    @Select("select * from dish_flavor where dish_id = #{DishId}")
    List<DishFlavor> getByDishId(Long DishId);
}
