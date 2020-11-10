package com.flkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flkj.dao.ErDao;
import com.flkj.dao.GuideDao;
import com.flkj.dao.UserDao;
import com.flkj.pojo.Er;
import com.flkj.pojo.Guide;
import com.flkj.pojo.User;
import com.flkj.result.Result;
import com.flkj.result.ResultUtil;
import com.flkj.utils.Erbuild;
import com.flkj.view.GuidemetadataView;
import com.flkj.view.UserGuidemetadataView;
import com.flkj.viewdao.GuidemetadataviewDao;
import com.flkj.viewdao.UserGuideMetadataViewDao;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午4:40
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块guide")
@RestController
@RequestMapping("guide")
public class GuideController {
	@Autowired
	private GuideDao guideDao;
	@Autowired
	private ErDao erDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserGuideMetadataViewDao userGuideMetadataViewDao;
	@Autowired
	private GuidemetadataviewDao guidemetadataviewDao;
	@ApiOperation(value = "保存数据引接计划")
	/*
	*
	* {
		  "userid": 1,
		  "guide": {
			"guideName":"guideName",
			"guideGz":"同步",
			"guideTbsj":"time"
		   },
		   "metadataids":[2,3]
		}
	* */
	@PostMapping("save")
	@Transactional
	public Result save(@RequestBody JSONObject jsonObject) {
		Long userid = jsonObject.getLong("userid");
		JSONObject jsonObject1 = jsonObject.getJSONObject("guide");
		Guide guide = JSON.parseObject(jsonObject1.toJSONString(),Guide.class);
		JSONArray metadataids = jsonObject.getJSONArray("metadataids");
		Guide save = guideDao.save(guide);
		User user = userDao.findById(userid).orElse(new User());
		user.setUserJyjhs(user.getUserJyjhs()+1);
		userDao.save(user);
		Er erbuild = Erbuild.erbuild(userid, "1.1", save.getGuideId(), "3", 39);
		erDao.save(erbuild);
		for (int i=0; i<metadataids.size(); i++) {
			long metadataid = metadataids.getInteger(i);
			Er erbuilds = Erbuild.erbuild(save.getGuideId(), "3", metadataid, "4", 40);
			erDao.save(erbuilds);
		}
		return ResultUtil.success();
	}
	@ApiOperation(value = "停用数据引接计划")
	@PostMapping("stop")
	public Result stop(@RequestBody Guide guide) {
		Guide save = guideDao.save(guide);
		return ResultUtil.success();
	}
	@ApiOperation(value = "查询全部数据引接计划")
	@GetMapping("selectAll")
	public Result selectAll(String input,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Page<UserGuidemetadataView> userGuidemetadataViews = null;
		if (StringUtils.isEmpty(input)) {
			userGuidemetadataViews = userGuideMetadataViewDao.selectAll(pageRequest);
		}
		if (!StringUtils.isEmpty(input)) {
			userGuidemetadataViews = userGuideMetadataViewDao.selectAllByname(input,pageRequest);
		}
		return ResultUtil.success(userGuidemetadataViews);
	}
	@ApiOperation(value = "selectAllDataNopage")
	@GetMapping("selectAllDataNopage")
	public Result selectAllDataNopage(long guideid){
		List<GuidemetadataView> byGuideId = guidemetadataviewDao.findByGuideId(guideid);
		return ResultUtil.success(byGuideId);
	}
}
