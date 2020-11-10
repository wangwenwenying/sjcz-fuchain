package com.flkj.viewdao;

import com.flkj.view.UserMedataTagView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午3:03
 * @description：
 * @modified By：
 * @version:
 */
public interface UserMedataTagViewDao extends JpaRepository<UserMedataTagView, Long>, JpaSpecificationExecutor<UserMedataTagView> {
	Optional<UserMedataTagView> findByDataUrl(String dataurl);

	List<UserMedataTagView> findByUserUid(String useruid);
	Page<UserMedataTagView> findByUserId(long userid, Pageable pageable);
	List<UserMedataTagView> findByUserId(long userid);
}
