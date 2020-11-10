package com.flkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flkj.config.EnvConfigFabric;
import com.flkj.config.EsConfig;
import com.flkj.dao.ErDao;
import com.flkj.dao.MetadataDao;
import com.flkj.dao.UserDao;
import com.flkj.esbuild.HighLevelClient;
import com.flkj.pojo.Er;
import com.flkj.pojo.Metadata;
import com.flkj.pojo.User;
import com.flkj.result.Result;
import com.flkj.result.ResultEnum;
import com.flkj.result.ResultUtil;
import com.flkj.service.MetadataService;
import com.flkj.utils.Erbuild;
import com.flkj.utils.VeDate;
import com.flkj.view.UserMedataTagView;
import com.flkj.viewdao.UserMedataTagViewDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author ：www
 * @date ：Created in 20-8-14 下午4:24
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块metadate")
@RestController
@RequestMapping("metadate")
public class MetadateController {
	@Autowired
	private MetadataDao metadataDao;
	@Autowired
	private ErDao erDao;
	@Autowired
	private UserMedataTagViewDao userMedataTagViewDao;
	@Autowired
	private EsConfig esConfig;
	@Autowired
	private UserDao userDao;
	@Autowired
	private EnvConfigFabric envConfigFabric;
	@Autowired
	private MetadataService metadataService;
	@ApiOperation(value = "编辑元数据")
	/*
	*
	*{
		"userid": 1,
		"metadata": {
			"dataUrl":"asldkjasldf",
			"dataName": "dataName",
			"dataByname": "dataByname",
			"dataIndexs": "dataIndexs,dataIndexs1",
			"dataIndexsname": "dataIndexsname,dataIndexsname1",
			"dataType": "dataType",
			"dataState": "dataState",
			"dataFlag": "dataFlag",
			"dataCrype": "dataCrype",
			"dataWho": "dataWho",
			"dataTagid": 1
		}
	}
	* */
	@PostMapping("save")
	@Transactional
	public Result save(@RequestBody JSONObject jsonObject) {
		Long userid = jsonObject.getLong("userid");
		User user = userDao.findById(userid).orElse(new User());
		user.setUserYsjgs(user.getUserYsjgs()+1);
		userDao.save(user);
		JSONObject jsonObject1 = jsonObject.getJSONObject("metadata");
		Metadata metadata = JSON.parseObject(jsonObject1.toJSONString(),Metadata.class);
		Optional<Metadata> byDataUrl = metadataDao.findByDataUrl(metadata.getDataUrl());
		if (byDataUrl.isPresent()) {
			return ResultUtil.error(ResultEnum.METADATA_ERROR);
		}
		String[] indexs = metadata.getDataIndexs().split(",");
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonObject3 = new JSONObject();
		for (String str:indexs) {
			jsonObject3.put(str,str);
		}
		jsonObject2.put("head",jsonObject3);
		jsonObject2.put("data","data");
		metadata.setDataField(jsonObject2.toJSONString());
		Metadata save = metadataDao.save(metadata);
		Er erbuild = Erbuild.erbuild(userid, "1.1", save.getDataId(), "4", 37);
		erDao.save(erbuild);
		return ResultUtil.success();
	}

	@ApiOperation(value = "根据元数据标示查询元数据")
	@GetMapping("selectByUrl")
	public Result selectByUrl(String url) {
		Optional<UserMedataTagView> byDataUrl = userMedataTagViewDao.findByDataUrl(url);
		if (byDataUrl.isPresent()) {
			return ResultUtil.success(byDataUrl.orElse(new UserMedataTagView()));
		} else {
			return ResultUtil.error(ResultEnum.NOTFOUND);
		}
	}

	@ApiOperation(value = "元数据列表")
	@GetMapping("selectAllNopage")
	public Result selectAllNopage(String uid) {
		List<UserMedataTagView> all = userMedataTagViewDao.findByUserUid(uid);
		return ResultUtil.success(all);
	}

	@ApiOperation(value = "元数据列表")
	@GetMapping("selectAll")
	public Result selectAll(String input,String dataType,String tagName,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Specification<UserMedataTagView> spec = new Specification<UserMedataTagView>() {
			@Override
			public Predicate toPredicate(Root<UserMedataTagView> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				if(!StringUtils.isEmpty(dataType)) {
					list.add(cb.equal(root.get("dataType"), dataType));
				}
				if(!StringUtils.isEmpty(tagName)) {
					list.add(cb.equal(root.get("tagName"), tagName));
				}
				if(!StringUtils.isEmpty(input)) {
					list.add(cb.equal(root.get("dataName"), input));
				}

				//此时条件之间是没有任何关系的。
				Predicate[] arr = new Predicate[list.size()];
				return cb.and(list.toArray(arr));
			}
		};
		Page<UserMedataTagView> all = userMedataTagViewDao.findAll(spec,pageRequest);
		return ResultUtil.success(all);
	}
	@ApiOperation(value = "根据id查询元数据")
	@GetMapping("selectByid")
	public Result selectByid(long id) {
		Optional<Metadata> byId = metadataDao.findById(id);
		if (byId.isPresent()) {
			return ResultUtil.success(byId.orElse(new Metadata()));
		} else {
			return ResultUtil.error(ResultEnum.NOTFOUND);
		}
	}

	@ApiOperation(value = "根据id查询元数据json")
	@GetMapping("selectByids")
	public Result selectByids(long id) {
		Optional<Metadata> byId = metadataDao.findById(id);
		if (byId.isPresent()) {
			Metadata metadata = byId.orElse(new Metadata());
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1 = JSON.parseObject(metadata.getDataField());
			return ResultUtil.success(jsonObject1);
		} else {
			return ResultUtil.error(ResultEnum.NOTFOUND);
		}
	}

	@ApiOperation(value = "根据userid查询元数据列表")
	@GetMapping("selectByUseridNopage")
	public Result selectByUseridNopage(long userid) {
		List<UserMedataTagView> byUserId = userMedataTagViewDao.findByUserId(userid);
		return ResultUtil.success(byUserId);
	}

	@ApiOperation(value = "根据userid查询元数据列表")
	@GetMapping("selectByUserid")
	public Result selectByUserid(long userid,String input,String dataType,String tagName,@RequestParam(defaultValue = "1") int page,
								 @RequestParam(defaultValue = "10") int size) {
		if (userid==0) {
			return ResultUtil.error(ResultEnum.INPUT_ERROR);
		}
		Specification<UserMedataTagView> spec = new Specification<UserMedataTagView>() {
			@Override
			public Predicate toPredicate(Root<UserMedataTagView> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				if(!StringUtils.isEmpty(dataType)) {
					list.add(cb.equal(root.get("dataType"), dataType));
				}
				if(!StringUtils.isEmpty(tagName)) {
					list.add(cb.equal(root.get("tagName"), tagName));
				}
				if(!StringUtils.isEmpty(input)) {
					list.add(cb.equal(root.get("dataName"), input));
				}
				list.add(cb.equal(root.get("userId"),userid));
				//此时条件之间是没有任何关系的。
				Predicate[] arr = new Predicate[list.size()];
				return cb.and(list.toArray(arr));
			}
		};
		PageRequest pageRequest = PageRequest.of(page-1,size);
		Page<UserMedataTagView> byUserId = userMedataTagViewDao.findAll(spec, pageRequest);
		return ResultUtil.success(byUserId);
	}

	@ApiOperation(value = "根据datahash查询数据")
	@GetMapping("selectByDataHash")
	public JSONObject selectByDataHash(String account,String dataHash,String dataName) {
		JSONObject jsonObject = new JSONObject();
		try {
			RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
			SearchRequest searchRequest = new SearchRequest(account);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

			if (!StringUtils.isEmpty(dataHash)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("head.dataHash", dataHash));

			}
			if (!StringUtils.isEmpty(dataName)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("head.dataName", dataName));
			}
			searchSourceBuilder.query(boolQueryBuilder);
			searchRequest.source(searchSourceBuilder);
			List list = new ArrayList();
			SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			RestStatus restStatus = search.status();
			if (restStatus != RestStatus.OK) {
				System.out.println("error");
			}
			SearchHits hits = search.getHits();
			TotalHits totalHits = hits.getTotalHits();
			long total = totalHits.value;
			hits.forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));

			jsonObject = returnResult(200,"成功",list);

			return jsonObject;
		} catch (IOException e) {
			e.printStackTrace();
			return returnResult(400,"网络异常");
		}
	}


	private JSONObject returnResult(int code,String msg,Object data){
		JSONObject result = new JSONObject(true);
		result.put("code",code);
		result.put("msg",msg);
		result.put("data",data);
		return result;
	}
	private JSONObject returnResult(int code,String msg){
		JSONObject result = new JSONObject(true);
		result.put("code",code);
		result.put("msg",msg);
		return result;
	}
	@ApiOperation(value = "索引结果")
	@GetMapping("selectByIndexs")
	public Result selectByIndexs(long metadataid,long userid,String input,@RequestParam(defaultValue = "1") int page,
								 @RequestParam(defaultValue = "10") int size) {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		Optional<User> byId1 = userDao.findById(userid);
		String indice = byId1.orElse(new User()).getAccount();
		Optional<Metadata> byId = metadataDao.findById(metadataid);
		JSONObject jsonObject = new JSONObject();
		if (byId.isPresent()) {
			SearchRequest searchRequest = new SearchRequest(indice);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			String dataName = byId.orElse(new Metadata()).getDataName();
			searchSourceBuilder.query(QueryBuilders.termQuery("head.dataName.keyword",dataName));
			String[] indexs = byId.orElse(new Metadata()).getDataIndexs().split(",");
			if (!StringUtils.isEmpty(input)) {
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
				for (String str:indexs) {
					boolQueryBuilder.should(QueryBuilders.matchQuery("head.dataIndex."+str+".keyword", input));
				}
				searchSourceBuilder.query(boolQueryBuilder);
			}
			searchSourceBuilder.from(page-1);
			searchSourceBuilder.size(size);
			searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
			searchSourceBuilder.sort("_id", SortOrder.DESC);
			searchRequest.source(searchSourceBuilder);
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
		}
		return ResultUtil.success(jsonObject);
	}
	@ApiOperation(value = "交易详情")
	@GetMapping("selectByTxid")
	public Result selectByTxid(String txid,String id,long userid) {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		JSONObject jsonObject = envConfigFabric.fm.queryBlockByTransactionID(txid);
		Optional<User> byId1 = userDao.findById(userid);
		String indice = byId1.orElse(new User()).getAccount();
		SearchRequest searchRequest = new SearchRequest(indice);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		boolQueryBuilder.must(QueryBuilders.termQuery("id.keyword",id));
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(searchSourceBuilder);
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
			jsonObject.put("data",list.get(0));
			return ResultUtil.success(jsonObject);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultUtil.success(jsonObject);
	}
	@ApiOperation(value = "selectForTj")
	@GetMapping("selectForTj")
	public Result selectForTj(String time,long metadataid,String account) {
		UserMedataTagView userMedataTagView = userMedataTagViewDao.findById(metadataid).orElse(new UserMedataTagView());
		String metadataname = userMedataTagView.getDataName();
		JSONArray jsonArray = new JSONArray();
		if ("hour".equals(time)) {
			String timenow = VeDate.getStringDateShort();
			for (int i=0; i<6; i++) {
				int s = i*4;
				int e = (i+1)*4-1;
				String ss,es;
				if (s<10) {
					ss = "0" + String.valueOf(s);
				} else {
					ss = String.valueOf(s);
				}
				if (e<10) {
					es ="0" + String.valueOf(e);
				} else {
					es = String.valueOf(e);
				}
				String timestart = timenow +" "+ ss +":00:00";
				String timeend = timenow +" "+ es +":59:59";
				long sumtimebetween = metadataService.sumtimebetween(account, metadataname, timestart, timeend);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("time",timestart);
				jsonObject.put("value",sumtimebetween);
				jsonArray.add(jsonObject);
			}
		}
		if ("day".equals(time)) {
			String [] days = VeDate.dataforweekago();
			for (String str:days) {
				String timestart = str+" 00:00:00";
				String timeend = str+" 23:59:59";
				long sumtimebetween = metadataService.sumtimebetween(account, metadataname, timestart, timeend);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("time",str);
				jsonObject.put("value",sumtimebetween);
				jsonArray.add(jsonObject);
			}
		}
		if ("week".equals(time)) {
			String dataforago0 = VeDate.dataforago(0);
			String dataforago1 = VeDate.dataforago(7);
			String dataforago2 = VeDate.dataforago(14);
			String dataforago3 = VeDate.dataforago(21);
			String dataforago4 = VeDate.dataforago(28);
			String dataforago5 = VeDate.dataforago(35);
			String dataforago6 = VeDate.dataforago(42);
			String dataforago7 = VeDate.dataforago(49);
			String[] strings = {dataforago7,dataforago6,dataforago5,dataforago4,dataforago3,dataforago2,dataforago1,dataforago0};
			for (int i=0; i<strings.length-1; i++) {
				String timestart = strings[i] +" 00:00:00";
				String timeend = strings[i+1] + " 23:59:59";
				long sumtimebetween = metadataService.sumtimebetween(account, metadataname, timestart, timeend);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("time",strings[i+1]);
				jsonObject.put("value",sumtimebetween);
				jsonArray.add(jsonObject);
			}
		}
		return ResultUtil.success(jsonArray);
	}
	/*
	{
	"userid": 50,
	"inputs": [{
			"key": "id",
			"value": "13"
		},
		{
			"key": "relation",
			"value": "关联"
		}
	],
	"datatype": "table"
}
	 */
	@ApiOperation(value = "selectAllByIndexAndInput")
	@PostMapping("selectAllByIndexAndInput")
	public Result selectAllByIndexAndInput(@RequestBody JSONObject jsonObject,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		System.out.println(jsonObject);
		long userid = jsonObject.getLong("userid");
		JSONArray inputs = jsonObject.getJSONArray("inputs");
		String datatype = jsonObject.getString("datatype");
		JSONObject jsonObjectreturn = new JSONObject();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		Optional<User> byId = userDao.findById(userid);
		if (byId.isPresent()) {
			String index = byId.orElse(new User()).getAccount();
			SearchRequest searchRequest = new SearchRequest(index);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			//BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			if (!StringUtils.isEmpty(datatype)) {
//				boolQueryBuilder.must(QueryBuilders.termQuery("head.dataType",datatype));
				searchSourceBuilder.query(QueryBuilders.termQuery("head.dataType"+".keyword",datatype));
			}
			if (inputs.size()!=0) {
				for (int i=0; i<inputs.size(); i++) {
					JSONObject jsonObject1 ;
					jsonObject1 = inputs.getJSONObject(i);
					String key = jsonObject1.getString("key");
					String value = jsonObject1.getString("value");
					System.out.println(key);
					System.out.println(value);
					searchSourceBuilder.query(QueryBuilders.termQuery("head.dataIndex."+key+""+".keyword",value));
					//boolQueryBuilder.must(QueryBuilders.matchQuery("head.dataIndex."+key+"",value));
				}
			}
			//searchSourceBuilder.query(boolQueryBuilder);
			searchSourceBuilder.from(page-1);
			searchSourceBuilder.size(size);
			searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
			searchSourceBuilder.sort("slsj",SortOrder.DESC);
			searchRequest.source(searchSourceBuilder);
			List list = new ArrayList();
			try {
				SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
				RestStatus restStatus = search.status();
				if (restStatus != RestStatus.OK) {
					System.out.println("error");
				}
				SearchHits hits = search.getHits();
				TotalHits totalHits = hits.getTotalHits();
				hits.forEach(item->list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));
				long total = totalHits.value;
				jsonObjectreturn.put("total", total);
				jsonObjectreturn.put("list", list);
				return ResultUtil.success(jsonObjectreturn);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return ResultUtil.error(ResultEnum.NOTFOUND);

	}

	/*
	{
	"userid": 50,
	"inputs": [{
			"key": "id",
			"value": "13"
		},
		{
			"key": "relation",
			"value": "关联"
		}
	],
	"datatype": "table"
}
	 */
	@ApiOperation(value = "selectAllByIndexAndInputAndAccount")
	@PostMapping("selectAllByIndexAndInputAndAccount")
	public JSONObject selectAllByIndexAndInputAndAccount(@RequestBody JSONObject jsonObject,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		System.out.println(jsonObject);
		String account = jsonObject.getString("account");
		JSONArray inputs = jsonObject.getJSONArray("inputs");
		String datatype = jsonObject.getString("datatype");
		JSONObject jsonObjectreturn = new JSONObject();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		Optional<User> byId = userDao.findByAccount(account);
		if (byId.isPresent()) {
			String index = byId.orElse(new User()).getAccount();
			SearchRequest searchRequest = new SearchRequest(index);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			//BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			if (!StringUtils.isEmpty(datatype)) {
//				boolQueryBuilder.must(QueryBuilders.termQuery("head.dataType",datatype));
				searchSourceBuilder.query(QueryBuilders.termQuery("head.dataType"+".keyword",datatype));
			}
			if (inputs.size()!=0) {
				for (int i=0; i<inputs.size(); i++) {
					JSONObject jsonObject1 ;
					jsonObject1 = inputs.getJSONObject(i);
					String key = jsonObject1.getString("key");
					String value = jsonObject1.getString("value");
					System.out.println(key);
					System.out.println(value);
					searchSourceBuilder.query(QueryBuilders.termQuery("head.dataIndex."+key+""+".keyword",value));
					//boolQueryBuilder.must(QueryBuilders.matchQuery("head.dataIndex."+key+"",value));
				}
			}
			//searchSourceBuilder.query(boolQueryBuilder);
			searchSourceBuilder.from(page-1);
			searchSourceBuilder.size(size);
			searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
			searchSourceBuilder.sort("slsj",SortOrder.DESC);
			searchRequest.source(searchSourceBuilder);
			List list = new ArrayList();
			try {
				SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
				RestStatus restStatus = search.status();
				if (restStatus != RestStatus.OK) {
					System.out.println("error");
				}
				SearchHits hits = search.getHits();
				TotalHits totalHits = hits.getTotalHits();
				hits.forEach(item->list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));
				long total = totalHits.value;
				jsonObjectreturn.put("total", total);
				jsonObjectreturn.put("list", list);
				jsonObject = returnResult(200,"成功",jsonObjectreturn);
				return jsonObject;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return returnResult(400,"帐号异常");

	}

	@ApiOperation(value = "selectAllByIndex")
	@PostMapping("selectAllByIndex")
	public JSONObject selectAllByIndex(@RequestBody JSONObject jsonObject,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		System.out.println(jsonObject);
		String account = jsonObject.getString("account");
		JSONArray inputs = jsonObject.getJSONArray("inputs");
		String datatype = jsonObject.getString("datatype");
		JSONObject jsonObjectreturn = new JSONObject();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		Optional<User> byId = userDao.findByAccount(account);
		if (byId.isPresent()) {
			String index = byId.orElse(new User()).getAccount();
			SearchRequest searchRequest = new SearchRequest(index);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			//BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//			if (!StringUtils.isEmpty(datatype)) {
////				boolQueryBuilder.must(QueryBuilders.termQuery("head.dataType",datatype));
//				searchSourceBuilder.query(QueryBuilders.termQuery("head.dataType"+".keyword",datatype));
//			}
			if (inputs.size()!=0) {
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
				for (int i=0; i<inputs.size(); i++) {
					JSONObject jsonObject1 ;
					jsonObject1 = inputs.getJSONObject(i);
					String key = jsonObject1.getString("key");
					String value = jsonObject1.getString("value");
					boolQueryBuilder.must(QueryBuilders.termQuery("head."+key+""+".keyword",value));
//					searchSourceBuilder.query(QueryBuilders.termQuery("head."+key+""+".keyword",value));
					searchSourceBuilder.query(boolQueryBuilder);
					//boolQueryBuilder.must(QueryBuilders.matchQuery("head.dataIndex."+key+"",value));
				}
			}
			//searchSourceBuilder.query(boolQueryBuilder);
			searchSourceBuilder.from(page-1);
			searchSourceBuilder.size(size);
			searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
			searchSourceBuilder.sort("slsj",SortOrder.DESC);
			searchRequest.source(searchSourceBuilder);
			List list = new ArrayList();
			try {
				SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
				RestStatus restStatus = search.status();
				if (restStatus != RestStatus.OK) {
					System.out.println("error");
				}
				SearchHits hits = search.getHits();
				TotalHits totalHits = hits.getTotalHits();
				hits.forEach(item->list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));
				long total = totalHits.value;
				jsonObjectreturn.put("total", total);
				jsonObjectreturn.put("list", list);
				jsonObject = returnResult(200,"成功",jsonObjectreturn);
				return jsonObject;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return returnResult(400,"帐号异常");

	}

}
