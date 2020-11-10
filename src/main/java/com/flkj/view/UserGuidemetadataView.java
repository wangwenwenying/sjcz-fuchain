package com.flkj.view;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-17 上午11:30
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "userguidemetadataview")
public class UserGuidemetadataView {
	private Long userId;
	private String userUid;
	private String userName;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long guideId;
	@Column(insertable = false)
	private String guideOid;
	private String guideName;
	private String guideGz;
	private String guideTbsj;
	private String guideType;

	private Long dataId;
	private String dataName;
	private Long dataSjts;
	private Long ztyjtm;
	private Long jhnysjs;
}
