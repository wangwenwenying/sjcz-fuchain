package com.flkj.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:35
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "share")
public class Share {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long shareId;
	@Column(insertable = false)
	private String shareOid;
	private String shareUsershareid;
	private String shareUsersharedid;
	private String shareKssj;
	private String shareJssj;
	private String shareUsersharename;
	private String shareUsersharedname;
	private String shareUsershareuid;
	private String shareUsershareduid;
	@Column(insertable = false)
	private String shareType;
}
