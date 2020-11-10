package com.flkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flkj.config.EnvConfigFabric;
import com.flkj.config.EsConfig;
import com.flkj.dao.BlockinfoRespository;
import com.flkj.dao.UserDao;
import com.flkj.exception.UserException;
import com.flkj.pojo.Blockinfo;
import com.flkj.pojo.User;
import com.flkj.result.Result;
import com.flkj.result.ResultEnum;
import com.flkj.result.ResultUtil;
import com.flkj.service.BlockinfoService;
import com.flkj.utils.VeDate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ：www
 * @date ：Created in 20-8-17 下午3:22
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块blockinfo")
@RestController
@RequestMapping(value = "blockinfo")
public class BlockinfoController {
	@Autowired
	private BlockinfoRespository blockinfoRespository;
	@Autowired
	private EnvConfigFabric envConfigFabric;
	@Autowired
	private BlockinfoService blockinfoService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private EsConfig esConfig;
	private static Logger log = LoggerFactory.getLogger(BlockinfoController.class);
	@PostMapping("save")
	public Result save(@RequestBody Blockinfo blockinfo) {
		Blockinfo save = blockinfoRespository.save(blockinfo);
		return ResultUtil.success();
	}
	@ApiOperation(value = "分页查询所有区块信息")
	@PostMapping("selectAll")
	public Result selectAll(String input,@RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		if (!StringUtils.isEmpty(input)) {
			boolQueryBuilder.should(QueryBuilders.matchQuery("channelId",input));
			boolQueryBuilder.should(QueryBuilders.matchQuery("blockNumber",input));
			boolQueryBuilder.should(QueryBuilders.matchQuery("dataHash",input));
			boolQueryBuilder.should(QueryBuilders.matchQuery("id",input));
		}
		searchSourceBuilder.query(boolQueryBuilder);

		System.out.println("++++++++++++++++++++++++");
		System.out.println(pageNo);
		System.out.println(pageSize);

		searchSourceBuilder.from(pageNo-1);
		searchSourceBuilder.size(pageSize);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort("_id", SortOrder.DESC);
		searchRequest.source(searchSourceBuilder);
		JSONObject jsonObject = new JSONObject();
		List list = new ArrayList();
		try {
			SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			RestStatus restStatus = search.status();
			if (restStatus != RestStatus.OK) {
				return ResultUtil.error(ResultEnum.UNKNOW_ERROR);
			}
			SearchHits hits = search.getHits();
			TotalHits totalHits = hits.getTotalHits();
			long total = totalHits.value;
			hits.forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));
			System.out.println(list);
			jsonObject.put("total", total);
			jsonObject.put("list", list);
			return ResultUtil.success(jsonObject);

		} catch (IOException e) {
			e.printStackTrace();
		}



//		PageRequest pageRequest = PageRequest.of(pageNo-1,pageSize);
//		Page<Blockinfo> all = blockinfoRespository.findAll(pageRequest);
//		return ResultUtil.success(all);
		return ResultUtil.success(jsonObject);
	}
	@ApiOperation(value = "根据区块高度查询区块详情")
	@GetMapping("selectByBlocknumber")
	public Result selectByBlocknumber(long blocknumber) {
		if (StringUtils.isEmpty(blocknumber)) {
			return ResultUtil.error(ResultEnum.DATANULL_ERROR);
		}
		JSONObject jsonObject = envConfigFabric.fm.queryBlockByNumber(blocknumber);
		return ResultUtil.success(jsonObject);
	}
	@ApiOperation(value = "根据txid查询区块详情")
	@GetMapping("selectByTxid")
	public Result selectByTxid(String txid) {
		if (StringUtils.isEmpty(txid)) {
			return ResultUtil.error(ResultEnum.DATANULL_ERROR);
		}
		JSONObject jsonObject = envConfigFabric.fm.queryBlockByTransactionID(txid);
		return ResultUtil.success(jsonObject);
	}
	/** 
	* @Description: 首页区块总条数等信息
	* @Param:  
	* @return:  
	* @Author: www
	* @Date: 20-8-18 
	*/
	@ApiOperation(value = "首页区块总条数等信息")
	@GetMapping("selectForBlock")
	public Result selectForBlock() {
		JSONObject blockinfosum = blockinfoService.blockinfosum();
		JSONObject blockinfosumyestoday = blockinfoService.blockinfosumyestoday();
		long count = blockinfosumyestoday.getLong("yestoday");
		blockinfosum.put("yestodayblockinfosum",count);
		return ResultUtil.success(blockinfosum);
	}
	@ApiOperation(value = "账本区块每日增量")
	@GetMapping("selectForTimeUp")
	public Result selectForTimeUp() {
		JSONArray jsonArray = new JSONArray();
		String[] allchannelname = blockinfoService.getAllchannelname();
		String[] dataforweekago = VeDate.dataforweekago();
		for (String str:dataforweekago) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("key","time");
			jsonObject.put("value",str);
			JSONArray jsonArray1 = new JSONArray();
			for (String string:allchannelname) {
				JSONObject jsonObject1 = new JSONObject();
				String timestart = str+" 00:00:00";
				String timeend = str+" 23:59:59";
				long allnumBychannelnameAndtime = blockinfoService.getAllnumBychannelnameAndtimes(string, timestart, timeend);
				jsonObject1.put("key",string);
				jsonObject1.put("value",allnumBychannelnameAndtime);
				jsonArray1.add(jsonObject1);
			}
			jsonObject.put("channel",jsonArray1);
			jsonArray.add(jsonObject);
		}
		return ResultUtil.success(jsonArray);
	}
	@ApiOperation(value = "租户元数据上链数量占比")
	@GetMapping("selectBlockUserTotal")
	public Result selectBlockUserTotal() {
		List<User> all = userDao.findAll();
		JSONArray jsonArray = new JSONArray();
		for (User user:all) {
			String index = user.getAccount();
			try {
				JSONObject userdatatotal = blockinfoService.userdatatotal(index);
				userdatatotal.put("user",user.getAccount());
				jsonArray.add(userdatatotal);
			} catch (Exception e) {
				//e.printStackTrace();
				log.error(index+":index isnot exist");
			}
		}
		return ResultUtil.success(jsonArray);
	}
	@ApiOperation(value = "租户上链数据情况")
	@GetMapping("selectBlockUserUp")
	public Result selectBlockUserUp() {
		List<User> all = userDao.findAll();
		JSONArray jsonArray = new JSONArray();
		String[] dataforweekago = VeDate.dataforweekago();
		for (String str:dataforweekago) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("key","time");
			jsonObject.put("value",str);
			JSONArray jsonArray1 = new JSONArray();
			for (User user:all) {
				JSONObject jsonObject1 = new JSONObject();
				String timestart = str+" 00:00:00";
				String timeend = str+" 23:59:59";
				long allnumBychannelnameAndtime = blockinfoService.getAllnumBychannelnameAndtime(user.getAccount(), timestart, timeend);
				jsonObject1.put("key",user.getAccount());
				jsonObject1.put("value",allnumBychannelnameAndtime);
				jsonArray1.add(jsonObject1);
			}
			jsonObject.put("channel",jsonArray1);
			jsonArray.add(jsonObject);
		}
		return ResultUtil.success(jsonArray);
	}


}
