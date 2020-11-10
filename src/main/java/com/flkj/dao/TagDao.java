package com.flkj.dao;

import com.flkj.pojo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午12:00
 * @description：
 * @modified By：
 * @version:
 */
public interface TagDao extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
	List<Tag> findAllByTagType(String tagtype);
}
