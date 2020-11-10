package com.flkj.view;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-26 下午2:43
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "guidemetadataview")
public class GuidemetadataView {
	private long guideId;
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long dataId;
	private String dataName;

}
