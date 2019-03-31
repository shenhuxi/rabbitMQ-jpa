package com.zpself.jpa.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * BaseEntity公共类
 */
@MappedSuperclass  
public class BusinessEntityNotId implements Serializable{

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

	@ApiModelProperty(value = "机构ID", name = "orgId",dataType="Long")
	protected Long orgId;

	protected String orgName;//add by shihx 20190214

	@ApiModelProperty(value = "删除状态（0-正常,1-删除）", name = "deleteState",dataType="Integer")
	@Column(columnDefinition="int default 0",nullable=false)
	protected Integer deleteState = 0;

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

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public Integer getDeleteState() {
		return deleteState;
	}

	public void setDeleteState(Integer deleteState) {
		this.deleteState = deleteState;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
}
