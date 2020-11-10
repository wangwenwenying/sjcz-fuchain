package com.flkj.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:49
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "tag")
public class Tag {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long tagId;
	private String tagName;
	private String tagCode;
	private String tagBelong;
	@Column(insertable = false)
	private String tagType;
}
