package com.zpself.jpa.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * BaseEntity公共类
 */
@MappedSuperclass  
public class BaseEntity implements Serializable{

	protected static final long serialVersionUID = 6584294526314176797L;

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@ApiModelProperty(value = "id主键")
	protected Long id;

	@ApiModelProperty(value = "创建人ID")
	protected Long createUserId;

	@ApiModelProperty(value = "创建人名")
	protected String createUserName;

	@ApiModelProperty(value = "创建时间")
	@Temporal(TemporalType.TIMESTAMP) 
	@org.hibernate.annotations.CreationTimestamp   
	protected Date createDate;

	@ApiModelProperty(value = "修改人ID")
	protected Long modifyUserId;

	@ApiModelProperty(value = "修改人名")
	protected String modifyUserName;

	@ApiModelProperty(value = "修改时间")
	@Temporal(TemporalType.TIMESTAMP) 
	@org.hibernate.annotations.UpdateTimestamp  
	protected Date modifyDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(Long modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public String getModifyUserName() {
		return modifyUserName;
	}

	public void setModifyUserName(String modifyUserName) {
		this.modifyUserName = modifyUserName;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	
}
