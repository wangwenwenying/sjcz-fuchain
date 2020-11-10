package com.flkj.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:00
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Entity
@Table(name = "admin")
public class Admin {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long adminId;
	private String adminName;
	private String adminPassw;
}
