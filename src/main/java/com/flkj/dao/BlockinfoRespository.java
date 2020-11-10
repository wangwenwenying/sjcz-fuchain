package com.flkj.dao;

import com.flkj.pojo.Blockinfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author ：www
 * @date ：Created in 20-3-20 上午9:47
 * @description：
 * @modified By：
 * @version:
 */
public interface BlockinfoRespository extends ElasticsearchRepository<Blockinfo,String> {
}
