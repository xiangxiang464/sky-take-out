package com.sky.service;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void updateDish(DishDTO dishDTO);

    DishVO getById(Long id);

    void updateStatus(Integer status , Long id);

    List<DishVO> getByCategoryId(Long categoryId);
}
