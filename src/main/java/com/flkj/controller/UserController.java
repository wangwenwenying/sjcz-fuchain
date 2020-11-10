package com.flkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flkj.dao.UserDao;
import com.flkj.pojo.User;
import com.flkj.result.Result;
import com.flkj.result.ResultEnum;
import com.flkj.result.ResultUtil;
import com.flkj.utils.VeDate;
import com.flkj.view.UsershareView;
import com.flkj.viewdao.UsershareViewDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午2:33
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块user")
@RestController
@RequestMapping("user")
public class UserController {
	@Autowired
	private UserDao userDao;
	@Autowired
	private UsershareViewDao usershareViewDao;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${userinsert.url}") //获取bean的属性
	private String userurl;
	@ApiOperation(value = "新增user")
	/*
	* 	{
		  "account": "string",
		  "password": "string",
		  "userLx": "string",
		  "userName": "string",
		  "userType": "string",
		  "userZq": "2020-09-14 09:39:34",
		  "userEmail":"email.@qq.com",
          "userTel":"18837170105",
          "userUnit":"福建公司"
		}
	*
	* */
	@PostMapping("save")
	@Transactional
	public Result save(@RequestBody User user) {
		Optional<User> byAccount = userDao.findByAccount(user.getAccount());
		if (byAccount.isPresent()) {
			return ResultUtil.error(ResultEnum.ZH_REERO);
		}
		user.setUserUid(UUID.randomUUID().toString());
		user.setUserCjsj(VeDate.getStringDate());
		User save = userDao.save(user);
		if (save.getUserId()!=null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			JSONObject jsonObject = new JSONObject();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject.put("account",save.getAccount());
			jsonObject.put("password",save.getPassword());
			jsonObject.put("userGroup",0);
			jsonObject.put("endTime",save.getUserZq());
			jsonObject1.put("name",save.getUserName());
			jsonObject1.put("unit",save.getUserUnit());
			jsonObject1.put("tel",save.getUserTel());
			jsonObject1.put("email",save.getUserEmail());
			jsonObject.put("userinfo",jsonObject1);
			HttpEntity<String> request = new HttpEntity<>(jsonObject.toJSONString(), headers);
			String stringResponseEntity = restTemplate.postForEntity(userurl, request, String.class).getBody();
			if ("200".equals(JSON.parseObject(stringResponseEntity).getString("code"))) {
				return ResultUtil.success();
			} else if ("9999".equals(JSON.parseObject(stringResponseEntity).getString("code"))){
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ResultUtil.error(ResultEnum.USER_ERROR);
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ResultUtil.error(ResultEnum.UNKNOW_ERROR);
			}
		} else {
			return ResultUtil.error(ResultEnum.UNKNOW_ERROR);
		}
	}

	@ApiOperation(value = "selectByid")
	@GetMapping("selectByid")
	public Result selectByid(long userid) {
		Optional<User> byId = userDao.findById(userid);
		if (!byId.isPresent()) {
			return ResultUtil.error(ResultEnum.NOTFOUND);
		}
		return ResultUtil.success(byId.orElse(new User()));
	}

	@ApiOperation(value = "修改user")
	/*
	*{
		  "userId":8,
		  "account": "strings",
		  "password": "string",
		  "userLx": "string",
		  "userName": "string",
		  "userType": "string",
		  "userZq": "2020-09-14 09:39:34"
		}
	* */
	@PostMapping("update")
	public Result update(@RequestBody User user) {
		Optional<User> byAccount = userDao.findByAccount(user.getAccount());
		if (byAccount.isPresent()) {
			return ResultUtil.error(ResultEnum.ZH_REERO);
		}
		user.setUserGxsj(VeDate.getStringDate());
		userDao.save(user);
		return ResultUtil.success();
	}

	@ApiOperation("selectAllNopage")
	@GetMapping("selectAllNopage")
	public Result selectAllNopage() {
		List<User> all = userDao.findByUserTypeAndUserZqAfter("0",VeDate.getStringDate());
		return ResultUtil.success(all);
	}

	@ApiOperation(value = "上链租户管理 > 链上租户")
	@GetMapping("selectAll")
	public Result selectAll(String username,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page-1 , size);
		Page<UsershareView> usershareViews = null;
		if (StringUtils.isEmpty(username)) {
			usershareViews = usershareViewDao.selectAll(pageRequest);
		}
		if (!StringUtils.isEmpty(username))
		{
			usershareViews = usershareViewDao.selectAllByname(username,pageRequest);
		}
		return ResultUtil.success(usershareViews);
	}
	@ApiOperation(value = "上链租户管理 > 链上租户详情")
	@GetMapping("selectByUserid")
	public Result selectByUserid(long userid) {
		UsershareView byUsershareid = usershareViewDao.findByUsershareid(userid);
		return ResultUtil.success(byUsershareid);
	}
	@ApiOperation(value = "user login")
	@PostMapping("login")
	public Result login(String account,String password) {
		System.out.println(account + password);
		if (StringUtils.isEmpty(account)&&StringUtils.isEmpty(password)){
			return ResultUtil.error(ResultEnum.ZHMMNLL_ERROR);
		}
		Optional<User> byAccountAndPassword = userDao.findByAccountAndPasswordAndUserTypeAndUserZqAfter(account, password,"0", VeDate.getStringDate());
		if (!byAccountAndPassword.isPresent()) {
			return ResultUtil.error(ResultEnum.ZHMM_ERROR);
		}
		return ResultUtil.success(byAccountAndPassword.orElse(new User()));
	}
	@ApiOperation(value = "根据用户uid查询用户信息")
	@GetMapping("selectByUseruid")
	public Result selectByUseruid(String useruid) {
		Optional<User> byUserUid = userDao.findByUserUid(useruid);
		if (!byUserUid.isPresent()) {
			return ResultUtil.error(ResultEnum.NOTFOUND);
		}
		return ResultUtil.success(byUserUid.orElse(new User()));
	}
}
