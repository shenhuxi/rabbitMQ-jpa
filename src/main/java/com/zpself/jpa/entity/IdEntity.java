package com.zpself.jpa.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 统一定义实体的ID类
 */
@MappedSuperclass
public class IdEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @ApiModelProperty(value = "id主键")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}