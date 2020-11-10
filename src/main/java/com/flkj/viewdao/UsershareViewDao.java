package com.flkj.viewdao;

import com.flkj.view.UsershareView;
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
public interface UsershareViewDao extends JpaRepository<UsershareView, Long>, JpaSpecificationExecutor<UsershareView> {
	@Query(value = "SELECT * ,count(share_usersharedid) as shares FROM usershareview GROUP BY user_id",nativeQuery = true,countQuery = "select count(user_id) from usershareview GROUP BY user_id")
	Page<UsershareView> selectAll(Pageable pageable);

	@Query(value = "SELECT * ,count(share_usersharedid) as shares FROM usershareview where user_name =?1 GROUP BY user_id",nativeQuery = true,countQuery = "select count(user_id) from usershareview where user_name =?1 GROUP BY user_id")
	Page<UsershareView> selectAllByname(String username,Pageable pageable);

	@Query(value = "SELECT * ,count(share_usersharedid) as shares FROM usershareview where user_id = ?1 GROUP BY share_usershareid",nativeQuery = true)
	UsershareView findByUsershareid(long usershareid);
}
