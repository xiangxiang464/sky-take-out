package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.service.impl.EmployeeServiceImpl;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工登出")
    public Result<String> logout() {
        return Result.success();
    }

    @ApiOperation("添加员工")
    @PostMapping
    public Result<Void> save(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工信息：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }
    @ApiOperation("员工信息分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工信息分页查询：{}", employeePageQueryDTO);
        PageResult pageResult =employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }
    @ApiOperation("员工状态修改")
    @PostMapping("/status/{status}")
    public Result<Void> modifyStatus(@PathVariable Integer status, Long id){
        log.info("员工状态修改：{}", status);
        // 1.这个方法只能改状态
//        employeeService.modifyStatus(status,id);
        //2. 修改成可以改任何字段的
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeService.updateStatus(employee);
        return Result.success();
    }
    /**
     * 查询员工
     * @param id
     * @return
     */
    @ApiOperation("查询员工")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }
    /**
     * 更新员工
     * @param employeeDTO
     * @return
     */
    @ApiOperation("更新员工")
    @PutMapping
    public Result<Void> updateEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("更新员工：{}", employeeDTO);

        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }


}
