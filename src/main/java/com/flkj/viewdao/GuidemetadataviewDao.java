package com.flkj.viewdao;

import com.flkj.view.GuidemetadataView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-26 下午2:45
 * @description：
 * @modified By：
 * @version:
 */
public interface GuidemetadataviewDao extends JpaRepository<GuidemetadataView, Long>, JpaSpecificationExecutor<GuidemetadataView> {

	List<GuidemetadataView> findByGuideId(Long guideid);
}
