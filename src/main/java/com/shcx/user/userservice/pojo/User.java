package com.shcx.user.userservice.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Range;

/**
 * 用户表
 * 
 * @Title User.java 
 * @description TODO 
 * @time 2017年11月22日 上午11:16:36 
 * @author LILJ 
 * @version 1.0
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -636309610940049888L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Pattern(regexp = "[A-Za-z]{4,16}",message="只能是字母，并且长度在4-16位之间！")
	@Column(name = "username", nullable = true)
	private String username;

	@Column(name = "password", nullable = true)
	private String password;
	
	@NotNull(message="角色不能为空！")
	@OneToOne
	@JoinColumn(name="role_id",nullable = false)
	private Role role;
	
	@Column(name = "phone")
	private String phone;

	@Range(min = 1, max = 2)
	@Column(name = "sex")
	private Integer sex; //0未知 1男 2女

	@Range(min = 0, max = 1)
	@Column(name = "confirmed", nullable = true)
	private Integer confirmed;//0确定， 1未确定 （用户认证  1不通过，0通过）

	@Range(min = 0, max = 1)
	@Column(name = "locked", nullable = true)
	private Integer locked;//0锁定，1未锁定

	@Range(min = 0, max = 1)
	@Column(name = "logined", nullable = true)
	private Integer logined;//0登录 ， 1未登录
	
	@Column(name = "expiretime")
    private Long expiretime;//Token过期时间
	
	@Email
	@Column(name = "email")
	private String email;
	
	@Column(name = "createdate")
    private Long createDate;

	@Column(name = "lastupdatedate")
	private Long lastUpdateDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Integer confirmed) {
		this.confirmed = confirmed;
	}

	public Integer getLocked() {
		return locked;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public Integer getLogined() {
		return logined;
	}

	public void setLogined(Integer logined) {
		this.logined = logined;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Long getExpiretime() {
		return expiretime;
	}

	public void setExpiretime(Long expiretime) {
		this.expiretime = expiretime;
	}
	
}
