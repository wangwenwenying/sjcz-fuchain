package com.flkj.view;

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
@Table(name = "usershareview")
public class UsershareView {
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
	private Long userSljys;
	private String userSjtjsj;
	private String userZq;
	private String userLx;
	private String account;
	private String password;
	private Long userYsjgs;
	private String userCjsj;
	private String userGxsj;
	private String userType;
	private Long userJyjhs;
	private Long shares;

	private String shareUsershareid;
	private String shareUsersharedid;
	private String shareKssj;
	private String shareJssj;
}
