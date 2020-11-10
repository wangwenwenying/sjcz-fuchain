package com.flkj.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.mapping.PrimaryKey;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:35
 * @description：
 * @modified By：
 * @version:
 */
@Data

@Entity
@Embeddable
@Table(name = "sharemetadataview")
public class ShareMetadataView implements Serializable {
	private Long shareId;
	private String shareOid;
	private Long shareUsershareid;
	private Long shareUsersharedid;
	private String shareKssj;
	private String shareJssj;
	private String shareUsersharename;
	private String shareUsersharedname;
	private String shareUsershareuid;
	private String shareUsershareduid;
	private String shareType;
	@Id
	private Long dataId;
	private String dataName;
	private Long dataSjts;
	private String dataIndexs;
	private String dataIndexsname;
	private String dataSjtjsj;

}
