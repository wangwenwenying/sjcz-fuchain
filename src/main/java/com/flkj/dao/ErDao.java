package com.flkj.dao;

import com.flkj.pojo.Er;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ErDao extends JpaRepository<Er, Long>, JpaSpecificationExecutor<Er> {
}
