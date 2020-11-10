package com.flkj.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:29
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "guide")
public class Guide {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long guideId;
	@Column(insertable = false)
	private String guideOid;
	private String guideName;
	private String guideGz;
	private String guideTbsj;
	@Column(insertable = false)
	private String guideType;
}
