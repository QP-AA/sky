package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品");
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }


    /*
        菜品分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询");
        PageResult result = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(result);
    }


    /*
    `   批量删除
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {

        log.info("批量删除");
        dishService.deleteBatch(ids);
        return Result.success();
    }


    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品");
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("更新菜品");
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /*
        根据菜品分类查询菜品
     */
    @GetMapping("/list")
    public Result<List<Dish>> getByCategoryId(Long categoryId) {
        log.info("根据菜品分类查询菜品");

        List<Dish> res = dishService.getByCategoryId(categoryId);
        return Result.success(res);
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {

        log.info("菜品起售停售");
        dishService.startOrStop(status, id);
        return Result.success();
    }
 }
