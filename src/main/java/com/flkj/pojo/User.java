package com.flkj.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:51
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long userId;
	@Column(insertable = false)
	private String userOid;
	private String userUid;
	private String userName;
	private String userEmail;
	private String userTel;
	private String userUnit;
	@Column(insertable = false)
	private Long userSljys;
	private String userSjtjsj;
	private String userZq;
	private String userLx;
	private String account;
	private String password;
	@Column(insertable = false)
	private Long userYsjgs;
	private String userCjsj;
	private String userGxsj;
	@Column(insertable = false)
	private String userType;
	@Column(insertable = false)
	private Long userJyjhs;
	@Column(insertable = false)
	private Long userGxzhs;
}
