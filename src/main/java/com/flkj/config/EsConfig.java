package com.flkj.config;

import com.flkj.esbuild.HighLevelClient;
import lombok.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ：www
 * @date ：Created in 20-8-12 下午3:03
 * @description：
 * @modified By：
 * @version:
 */
@Data
@Component
@ConfigurationProperties(prefix = "es")
public class EsConfig implements CommandLineRunner {
	private static Logger log = LoggerFactory.getLogger(EsConfig.class);
	private String hosts;
	private String username;
	private String password;

	private RestHighLevelClient restHighLevelClient;

	@Override
	public void run(String... args) {
		try {
			HighLevelClient hc = new HighLevelClient(getHosts(),getUsername(),getPassword());
			restHighLevelClient = hc.getClient();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("elasticsearch build success");
	}
}
