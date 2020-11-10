package com.flkj.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:24
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "er")
public class Er {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	private Long eid;
	private String eoid;
	private Long eids;
	private String eoids;
	private Long relation;
}
