package org.example.basewebsub.entity.base;


import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Basic
	@Column(name = "CREATED_AT", updatable = false)
	protected Date createdDate;

	@CreatedBy
	@Basic
	@Column(name = "CREATED_BY", updatable = false)
	protected String createdBy;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Basic
	@Column(name = "UPDATED_AT")
	protected Date modifiedDate;

	@LastModifiedBy
	@Basic
	@Column(name = "UPDATED_BY")
	protected String modifiedBy;


	@Basic
	@Column(name = "DELETE_FLAG", updatable = false)
	protected Boolean deleteFlag;

	@Basic
	@Column(name = "CREATED_AT", updatable = false)
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Basic
	@Column(name = "CREATED_BY", updatable = false)
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Basic
	@Column(name = "UPDATED_AT")
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}


	@Basic
	@Column(name = "UPDATED_BY")
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Basic
	@Column(name = "DELETE_FLAG")
	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	@PrePersist
	protected void onCreate() {
		if (deleteFlag == null) {
			deleteFlag = false;
		}
	}
}