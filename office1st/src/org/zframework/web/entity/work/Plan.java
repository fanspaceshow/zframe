package org.zframework.web.entity.work;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.zframework.web.entity.system.User;

@Entity
@Table(name="work_plan")
public class Plan implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3926267453373505724L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_work_plan")
	@SequenceGenerator(name="seq_work_plan",sequenceName="seq_work_plan")
	private int id;
	@Column
	@NotNull
	private String title;
	@Column
	private String descript;
	@Column
	private String startTime;
	@Column
	private String endTime;
	@Column
	private String executor;
	@Column
	private String createTime;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="createUser")
	private User createUser;
	/**
	 * 1:待办 2:进行中 3:完成 4:关闭 5:延期
	 */
	@Column
	private int state;
	@Column
	private String completeTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getExecutor() {
		return executor;
	}
	public void setExecutor(String executor) {
		this.executor = executor;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}
	
}
