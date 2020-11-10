package com.flkj.view;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-17 上午8:42
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "usermetadatatagview")
public class UserMedataTagView {
	private Long userId;
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
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long dataId;
	@Column(insertable = false)
	private String dataOid;
	private String dataUrl;
	private String dataName;
	private String dataByname;
	private String dataIndexs;
	private String dataIndexsname;
	private String dataType;
	private String dataState;
	private String dataFlag;
	private String dataCrype;
	private String dataField;
	private String dataWho;
	private Long dataTagid;
	private Long dataSjts;
	private String dataSjtjsj;
	private String dataTypes;

	private String tagName;
	private String tagCode;
	private String tagBelong;
}
