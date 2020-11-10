package com.flkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flkj.dao.TagDao;
import com.flkj.pojo.Tag;
import com.flkj.result.Result;
import com.flkj.result.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：www
 * @date ：Created in 20-8-17 上午10:12
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块tag")
@RestController
@RequestMapping("tag")
public class TagController {
	@Autowired
	private TagDao tagDao;
	@ApiOperation(value = "新增数据标签")
	@PostMapping("save")
	public Result save(@RequestBody Tag tag) {
		Tag save = tagDao.save(tag);
		return ResultUtil.success();
	}
	@ApiOperation(value = "存证元数据管理 > 数据标签管理")
	@GetMapping("selectAll")
	public Result selectAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Page<Tag> all = tagDao.findAll(pageRequest);
		return ResultUtil.success(all);
	}
	@ApiOperation(value = "查询所有正常的标签")
	@GetMapping("selectAllFornormal")
	public Result selectAllFornormal(){
		List<Tag> allByTagType = tagDao.findAllByTagType("0");
		return ResultUtil.success(allByTagType);
	}

}
