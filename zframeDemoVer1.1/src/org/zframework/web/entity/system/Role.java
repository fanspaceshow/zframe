package org.zframework.web.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.zframework.web.entity.system.type.RoleType;
@Entity
@Table(name="sys_role")
public class Role implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3926384165269170701L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator="seq_sys_role")
	@SequenceGenerator(name="seq_sys_role",sequenceName="seq_sys_role")
	private Integer id;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=1,max=50)
	private String name;
	@Column
	@Enumerated(EnumType.ORDINAL)
	private RoleType type;
	@Column
	private int enabled;
	@Column
	@Length(min=0,max=500)
	private String descript;
	
	/**
	 * 角色资源列表
	 */
	@ManyToMany(cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_roleresource",joinColumns={@JoinColumn(name="roleid")},inverseJoinColumns={@JoinColumn(name="resourceid")})
	@OrderBy("location asc")
	private List<Resource> resources = new ArrayList<Resource>();

	/**
	 * 该角色下所有用户
	 */
	@ManyToMany(cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_userrole",joinColumns={@JoinColumn(name="roleid")},inverseJoinColumns={@JoinColumn(name="userid")})
	@OrderBy("id asc")
	private List<User> users = new ArrayList<User>();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RoleType getType() {
		return type;
	}
	public void setType(RoleType type) {
		this.type = type;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled?0:1;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
}
