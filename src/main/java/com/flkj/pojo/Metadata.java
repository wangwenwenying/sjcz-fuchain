package com.flkj.pojo;

import javax.persistence.*;
import lombok.Data;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:04
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "metadata")
public class Metadata {
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
	@Column(insertable = false)
	private Long dataSjts;
	private String dataSjtjsj;
	@Column(insertable = false)
	private String dataTypes;
}
