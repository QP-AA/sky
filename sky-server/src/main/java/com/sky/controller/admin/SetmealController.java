package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /*
        新增套餐
     */
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐");
        setmealService.add(setmealDTO);
        return Result.success();
    }

    /*
        套餐分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询");
        PageResult result = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(result);
    }

    /*
        批量删除套餐
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐");
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
