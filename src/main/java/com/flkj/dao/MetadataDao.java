package com.flkj.dao;

import com.flkj.pojo.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:57
 * @description：
 * @modified By：
 * @version:
 */
public interface MetadataDao extends JpaRepository<Metadata, Long>, JpaSpecificationExecutor<Metadata> {
	Optional<Metadata> findByDataUrl(String dataurl);
}
