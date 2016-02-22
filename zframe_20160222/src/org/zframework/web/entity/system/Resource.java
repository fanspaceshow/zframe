package org.zframework.web.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="sys_resource")
public class Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3849571087678678873L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_sys_resource")
	@SequenceGenerator(name="seq_sys_resource",sequenceName="seq_sys_resource")
	private int id;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=1,max=50)
	private String name;
	@Column
	private String url;
	@Column
	private String icon;
	@Column
	private int location;
	@Column
	private int enabled;
	@Column
	@Length(min=0,max=500)
	private String descript;
	@Column
	private int parentId;
	@Transient
	@ManyToMany(cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_roleresource",joinColumns={@JoinColumn(name="resourceid")},inverseJoinColumns={@JoinColumn(name="roleid")})
	private List<Role> roles = new ArrayList<Role>();
	
	@ManyToMany(cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_resourcebutton",joinColumns={@JoinColumn(name="resourceid")},inverseJoinColumns={@JoinColumn(name="buttonid")})
	@OrderBy("id asc")
	private List<Button> buttons = new ArrayList<Button>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public int getEnabled() {
		return enabled;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public List<Button> getButtons() {
		return buttons;
	}
	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}
}
