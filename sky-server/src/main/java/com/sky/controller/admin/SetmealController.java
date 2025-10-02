package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmealVO =  setmealService.getById(id);
        return Result.success(setmealVO);
    }

}
