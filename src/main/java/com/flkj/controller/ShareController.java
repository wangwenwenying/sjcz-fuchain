package com.flkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flkj.dao.ErDao;
import com.flkj.dao.ShareDao;
import com.flkj.pojo.Er;
import com.flkj.pojo.Share;
import com.flkj.result.Result;
import com.flkj.result.ResultEnum;
import com.flkj.result.ResultUtil;
import com.flkj.utils.Erbuild;
import com.flkj.utils.VeDate;
import com.flkj.view.ShareMetadataView;
import com.flkj.viewdao.ShareMetadataViewDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ：www
 * @date ：Created in 20-8-17 上午11:13
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块share")
@RestController
@RequestMapping("share")
public class ShareController {
	@Autowired
	private ShareDao shareDao;
	@Autowired
	private ErDao erDao;
	@Autowired
	private ShareMetadataViewDao shareMetadataViewDao;
	@ApiOperation(value = "上链租户管理 > 租户共享详情")
	@GetMapping("selectAll")
	public Result selectAll(String input,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Specification<ShareMetadataView> spec = new Specification<ShareMetadataView>() {
			@Override
			public Predicate toPredicate(Root<ShareMetadataView> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				if(!StringUtils.isEmpty(input)) {
					list.add(cb.equal(root.get("dataName"), input));
				}
				Predicate[] arr = new Predicate[list.size()];
				return cb.and(list.toArray(arr));
			}
		};
		Page<ShareMetadataView> all = shareMetadataViewDao.findAll(spec,pageRequest);
		return ResultUtil.success(all);
	}
	@ApiOperation(value = "selectAlls")
	@GetMapping("selectAlls")
	public Result selectAlls(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		List<ShareMetadataView> all = shareMetadataViewDao.findAll();
		return ResultUtil.success(all);
	}
	/*
	*{
	  "share": {
		"shareUsershareid":1,
		"shareUsersharedid":2,
		"shareKssj":"2020-08-25 00:00:00",
		"shareJssj":"2020-09-25 00:00:00",
		"shareUsersharename":"123",
		"shareUsersharedname":"123",
		"shareUsershareuid":"1",
		"shareUsershareduid":"2"
	  },
	  "metatateids": [2,3]
	}
	*
	* */
	@ApiOperation(value = "新建共享")
	@PostMapping("save")
	public Result save(@RequestBody JSONObject jsonObject) {
		JSONArray metadateids = jsonObject.getJSONArray("metatateids");
		JSONObject jsonObject1 = jsonObject.getJSONObject("share");
		Share share = JSON.parseObject(jsonObject1.toJSONString(),Share.class);
		Share save = shareDao.save(share);
		for (Object metadateid:metadateids) {
			Er erbuild = Erbuild.erbuild(save.getShareId(), "2", (Integer) metadateid, "4", 38);
			erDao.save(erbuild);
		}
		return ResultUtil.success();
	}

	@ApiOperation(value = "租户数据共享 > 数据共享列表")
	@GetMapping("selectByuseridd")
	public Result selectByuseridd(long userid,String input,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Page<ShareMetadataView> byShareUsershareid;
		if (!StringUtils.isEmpty(input)) {
			byShareUsershareid = shareMetadataViewDao.selectAllds(userid, VeDate.getStringDate(),input,pageRequest);
		} else {
			byShareUsershareid = shareMetadataViewDao.selectAlld(userid,VeDate.getStringDate(),pageRequest);
		}
		return ResultUtil.success(byShareUsershareid);
	}

	@ApiOperation(value = "租户数据共享 > 数据共享列表")
	@GetMapping("selectByuserid")
	public Result selectByuserid(long userid,String input,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Page<ShareMetadataView> byShareUsershareid;
		if (!StringUtils.isEmpty(input)) {
			byShareUsershareid = shareMetadataViewDao.selectAlls(userid,input,pageRequest);
		} else {
			byShareUsershareid = shareMetadataViewDao.selectAll(userid,pageRequest);
		}
		return ResultUtil.success(byShareUsershareid);
	}
	@ApiOperation(value = "租户数据共享详情")
	@GetMapping("selectByid")
	public Result selectByid(long id) {
		Optional<Share> byId = shareDao.findById(id);
		return ResultUtil.success(byId.orElse(new Share()));
	}
	@ApiOperation(value = "租户数据共享详情")
	@GetMapping("findAllByShareId")
	public Result findAllByShareId(long shareid) {
		List<ShareMetadataView> byShareId = shareMetadataViewDao.findByShareId(shareid);
		return ResultUtil.success(byShareId);
	}
}
