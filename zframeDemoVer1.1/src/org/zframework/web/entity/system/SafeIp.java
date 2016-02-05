package org.zframework.web.entity.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="sys_safeip")
public class SafeIp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2825213735777441799L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_sys_safeip")
	@SequenceGenerator(name="seq_sys_safeip",sequenceName="seq_sys_safeip")
	private Integer id;
	@Column
	@NotEmpty
	@Length(min=1,max=20)
	private String ip;
	@Column
	private int enabled;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
}
