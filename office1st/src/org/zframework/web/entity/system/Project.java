package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 新增功能：项目管理的实体类
 * @author xinyun.hu
 *
 */
@Entity
@Table(name = "sys_project")
public class Project {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_project")
	@SequenceGenerator(name = "seq_sys_project", sequenceName = "seq_sys_project")
	private Integer id;//项目id主键
	@Column
	@NotNull
	@NotEmpty
	private String projectname;//项目名称
	@Column
	@NotNull
	@NotEmpty
	private String projecttype;//项目类型
	@Column
	@NotNull
	@NotEmpty
	private String createprotime;//项目创建的时间
	@Column
	@Length(min=0,max=20)
	private String appointdays;//指定的天数
	@Column
	@Length(min=0,max=20)
	private String proplaydays;//项目用时
	@Column
	@Length(min=0,max=20)
	private String proschedule;//项目进度
	@Column
	@Length(min=0,max=50)
	private String proparticipant;//项目参与人
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public String getProjecttype() {
		return projecttype;
	}
	public void setProjecttype(String projecttype) {
		this.projecttype = projecttype;
	}
	public String getCreateprotime() {
		return createprotime;
	}
	public void setCreateprotime(String createprotime) {
		this.createprotime = createprotime;
	}
	public String getAppointdays() {
		return appointdays;
	}
	public void setAppointdays(String appointdays) {
		this.appointdays = appointdays;
	}
	public String getProplaydays() {
		return proplaydays;
	}
	public void setProplaydays(String proplaydays) {
		this.proplaydays = proplaydays;
	}
	public String getProschedule() {
		return proschedule;
	}
	public void setProschedule(String proschedule) {
		this.proschedule = proschedule;
	}
	public String getProparticipant() {
		return proparticipant;
	}
	public void setProparticipant(String proparticipant) {
		this.proparticipant = proparticipant;
	}
}
