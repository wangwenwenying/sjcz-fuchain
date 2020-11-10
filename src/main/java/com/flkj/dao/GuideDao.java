package com.flkj.dao;

import com.flkj.pojo.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:56
 * @description：
 * @modified By：
 * @version:
 */
public interface GuideDao extends JpaRepository<Guide, Long>, JpaSpecificationExecutor<Guide> {
}
