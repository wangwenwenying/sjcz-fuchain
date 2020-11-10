package com.flkj.dao;

import com.flkj.pojo.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:58
 * @description：
 * @modified By：
 * @version:
 */
public interface ShareDao extends JpaRepository<Share, Long>, JpaSpecificationExecutor<Share> {
}
