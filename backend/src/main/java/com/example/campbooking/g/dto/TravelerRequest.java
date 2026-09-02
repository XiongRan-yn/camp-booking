package com.example.campbooking.g.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 出行人（常用旅客）新增/修改请求。
 * id 仅在「修改」时传入，新增时留空。
 */
public class TravelerRequest {

    /** 修改时必填，新增时为空 */
    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 年龄（选填） */
    private Integer age;

    /** 性别：male / female（选填） */
    private String gender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
