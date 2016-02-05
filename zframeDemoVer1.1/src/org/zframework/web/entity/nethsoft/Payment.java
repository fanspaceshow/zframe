package org.zframework.web.entity.nethsoft;

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
@Table(name="NS_PAYMENT")
public class Payment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -423568474109614083L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_ns_payment")
	@SequenceGenerator(name="seq_ns_payment",sequenceName="seq_ns_payment")
	private int id;
	@Column
	@NotNull
	private String datetime;
	@Column
	@NotNull
	private String descript;
	@Column
	private float income;
	@Column
	private float pay;
	@Column
	private float balance;
	@Column
	private String comments;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="takeUser")
	private User takeUser;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public float getIncome() {
		return income;
	}
	public void setIncome(float income) {
		this.income = income;
	}
	public float getPay() {
		return pay;
	}
	public void setPay(float pay) {
		this.pay = pay;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public User getTakeUser() {
		return takeUser;
	}
	public void setTakeUser(User takeUser) {
		this.takeUser = takeUser;
	}
}
