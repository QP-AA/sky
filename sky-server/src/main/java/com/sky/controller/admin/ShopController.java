package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("UserShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;
    /*
        设置营业状态
     */
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置营业状态 {}", status == 1 ? "营业中" : "打烊了");
        redisTemplate.opsForValue().set("SHOP STATUS", status);
        return Result.success();
    }

    /*
        获取营业状态
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        log.info("获取营业状态");
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get("SHOP STATUS");
        return Result.success(shopStatus);
    }
}
