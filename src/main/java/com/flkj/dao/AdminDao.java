package com.flkj.dao;

import com.flkj.pojo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 上午11:55
 * @description：
 * @modified By：
 * @version:
 */
public interface AdminDao extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {
	Optional<Admin> findByAdminNameAndAdminPassw(String adminName,String adminPassw);
}
