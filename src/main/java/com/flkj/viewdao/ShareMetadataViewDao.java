package com.flkj.viewdao;

import com.flkj.view.ShareMetadataView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午3:03
 * @description：
 * @modified By：
 * @version:
 */
public interface ShareMetadataViewDao extends JpaRepository<ShareMetadataView, Long>, JpaSpecificationExecutor<ShareMetadataView> {
	@Query(value = "SELECT * FROM `sharemetadataview` where share_usersharedid =?1 and share_jssj >?2"
			,nativeQuery = true,countQuery = "SELECT count(*) FROM `sharemetadataview` where share_usersharedid =?1 and share_jssj >?2")
	Page<ShareMetadataView> selectAlld(long shareuserdid,String time,Pageable pageable);

	@Query(value = "SELECT * FROM `sharemetadataview` where share_usersharedid =?1 and data_name=?2 and share_jssj >?3"
			,nativeQuery = true,countQuery = "SELECT count(*) FROM `sharemetadataview` where share_usersharedid =?1 and data_name=?2 and share_jssj >?3")
	Page<ShareMetadataView> selectAllds(long shareuserdid,String dataname,String time,Pageable pageable);

	@Query(value = "SELECT * FROM `sharemetadataview` where share_usershareid =?1"
			,nativeQuery = true,countQuery = "SELECT count(*) FROM `sharemetadataview` where share_usershareid =?1")
	Page<ShareMetadataView> selectAll(long shareuserid,Pageable pageable);

	@Query(value = "SELECT * FROM `sharemetadataview` where share_usershareid =?1 and data_name=?2"
			,nativeQuery = true,countQuery = "SELECT count(*) FROM `sharemetadataview` where share_usershareid =?1 and data_name=?2")
	Page<ShareMetadataView> selectAlls(long shareuserid,String dataname,Pageable pageable);

	List<ShareMetadataView> findByShareId(long shareid);

	Page<ShareMetadataView> findByShareUsersharedid(long userid, Pageable pageable);
}
