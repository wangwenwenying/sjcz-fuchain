package com.flkj.controller;

import com.alibaba.fastjson.JSONObject;
import com.flkj.dao.AdminDao;
import com.flkj.pojo.Admin;
import com.flkj.result.Result;
import com.flkj.result.ResultEnum;
import com.flkj.result.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午2:17
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块admin")
@RestController
@RequestMapping("admin")
public class AdminController {
	@Autowired
	private AdminDao adminDao;
	@ApiOperation(value = "查询全部admin信息")
	@GetMapping("selectAll")
	public Result selectAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Page<Admin> all = adminDao.findAll(pageRequest);
		return ResultUtil.success(all);
	}
	@ApiOperation(value = "admin登录")
	@PostMapping("login")
	public Result login(String adminName,String adminPassw){
		if (StringUtils.isEmpty(adminName) || StringUtils.isEmpty(adminPassw)) {
			return ResultUtil.error(ResultEnum.ZHMMNLL_ERROR);
		}
		Optional<Admin> byAdminNameAndAdminPassw = adminDao.findByAdminNameAndAdminPassw(adminName, adminPassw);
		if (byAdminNameAndAdminPassw.isPresent()) {
			return ResultUtil.success(byAdminNameAndAdminPassw.orElse(new Admin()));
		} else {
			return ResultUtil.error(ResultEnum.ZHMM_ERROR);
		}
	}

}
