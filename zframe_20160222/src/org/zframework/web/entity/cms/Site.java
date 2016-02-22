package org.zframework.web.entity.cms;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;

@Entity
@Table(name="CMS_SITE")
public class Site implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7275187751421520655L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_cms_site")
	@SequenceGenerator(name="seq_cms_site",sequenceName="seq_cms_site")
	@NotNull
	public int id;

	@Column
	@NotNull
	public String name;

	@Column
	public String url;

	@Column
	public String email;

	@Column
	public String keyword;

	@Column
	public String intro;

	@Column
	public String indexUrl;

	@Column
	public String ftpUrl;

	@Column
	public int ftpPort;

	@Column
	public String ftpUser;

	@Column
	public String ftpPass;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url=url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email=email;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword=keyword;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro=intro;
	}

	public String getIndexUrl() {
		return indexUrl;
	}

	public void setIndexUrl(String indexUrl) {
		this.indexUrl=indexUrl;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl=ftpUrl;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort=ftpPort;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser=ftpUser;
	}

	public String getFtpPass() {
		return ftpPass;
	}

	public void setFtpPass(String ftpPass) {
		this.ftpPass=ftpPass;
	}

}
