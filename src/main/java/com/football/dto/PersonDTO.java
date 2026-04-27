package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class PersonDTO {

    private Long id;

    @NotNull(message = "所属部门不能为空")
    private Long orgId;

    @NotBlank(message = "人员类型不能为空")
    private String personType;

    @NotBlank(message = "人员姓名不能为空")
    private String personName;

    private String phone;

    private String email;

    private Integer gender;

    private LocalDate birthday;

    private String idCard;

    private String address;

    private String avatar;

    private String remark;

    private Integer status;
}
