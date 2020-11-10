package com.flkj.viewdao;

import com.flkj.view.UserGuidemetadataView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午3:03
 * @description：
 * @modified By：
 * @version:
 */
public interface UserGuideMetadataViewDao extends JpaRepository<UserGuidemetadataView, Long>, JpaSpecificationExecutor<UserGuidemetadataView> {
	@Query(value = "SELECT *,count(userguidemetadataview.data_id) as jhnysjs,count(data_sjts) as ztyjtm FROM `userguidemetadataview` GROUP BY userguidemetadataview.guide_id"
			,nativeQuery = true,countQuery = "select count(userguidemetadataview.guide_id) from userguidemetadataview GROUP BY userguidemetadataview.guide_id")
	Page<UserGuidemetadataView> selectAll(Pageable pageable);
	@Query(value = "SELECT *,count(userguidemetadataview.data_id) as jhnysjs,count(data_sjts) as ztyjtm FROM `userguidemetadataview` where guide_name=?1  GROUP BY userguidemetadataview.guide_id"
			,nativeQuery = true,countQuery = "select count(userguidemetadataview.guide_id) from userguidemetadataview where guide_name=?1 GROUP BY userguidemetadataview.guide_id")
	Page<UserGuidemetadataView> selectAllByname(String guideName,Pageable pageable);
}
