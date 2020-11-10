package com.flkj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author ：www
 * @date ：Created in 20-8-17 下午3:16
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Document(indexName = "blockinfo",type = "doc")
public class Blockinfo {
	@Id
	private String id;
	private String dataHash;
	@Field(type = FieldType.Date, format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date slsj;
	private String blockNumber;
	private Long txCount;
	private Long envelopeCount;
	private String previousHashID;
	private String channelId;
}
