package org.zframework.web.entity.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sys_roleresourcebtn")
public class RoleResourceButton implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3362497855652065216L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator="seq_sys_roleresourcebtn")
	@SequenceGenerator(name="seq_sys_roleresourcebtn",sequenceName="seq_sys_roleresourcebtn")
	private Integer id;
	@Column
	private Integer roleId;
	@Column
	private Integer resourceId;
	@Column
	private Integer buttonId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public Integer getResourceId() {
		return resourceId;
	}
	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}
	public Integer getButtonId() {
		return buttonId;
	}
	public void setButtonId(Integer buttonId) {
		this.buttonId = buttonId;
	}
}
