package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {
    @ApiModelProperty("员工id")
    private Long id;
    @ApiModelProperty(value = "员工用户名，不可重复")
    private String username;
    @ApiModelProperty("员工姓名，可重复")
    private String name;
    @ApiModelProperty("员工手机号，11位")
    private String phone;
    @ApiModelProperty("员工性别")
    private String sex;
    @ApiModelProperty("员工身份证")
    private String idNumber;

}
