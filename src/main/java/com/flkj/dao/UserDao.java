package com.flkj.dao;

import com.flkj.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午2:08
 * @description：
 * @modified By：
 * @version:
 */
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	List<User> findByUserTypeAndUserZqAfter(String usertype, String time);
	Optional<User> findByAccountAndPasswordAndUserTypeAndUserZqAfter(String account,String password,String usertype,String time);
	Optional<User> findByAccount(String account);
	Optional<User> findByUserUid(String uid);
}
