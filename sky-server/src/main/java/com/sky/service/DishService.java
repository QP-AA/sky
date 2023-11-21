package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    public void saveWithFlavor(DishDTO dishDTO);

    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    public void deleteBatch(List<Long> ids);

    public DishVO getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDTO dishDTO);
}
