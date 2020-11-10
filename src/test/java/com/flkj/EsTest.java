package com.flkj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.flkj.config.EnvConfigFabric;
import com.flkj.config.EsConfig;
import com.flkj.config.ParsedStringTermsBucketSerializer;
import com.flkj.dao.BlockinfoRespository;
import com.flkj.dao.UserDao;
import com.flkj.esbuild.HighLevelClient;
import com.flkj.exception.UserException;
import com.flkj.pojo.Tag;
import com.flkj.pojo.User;
import com.flkj.result.ResultEnum;
import com.flkj.result.ResultUtil;
import com.flkj.service.BlockinfoService;
import com.flkj.utils.VeDate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author ：www
 * @date ：Created in 20-8-13 下午2:18
 * @description：
 * @modified By：
 * @version:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {
	@Autowired
	private EsConfig esConfig;
	@Autowired
	private UserDao userDao;
	@Autowired
	private BlockinfoRespository blockinfoRespository;
	@Autowired
	private BlockinfoService blockinfoService;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${userinsert.url}") //获取bean的属性
	private String userurl;
	private static Logger log = LoggerFactory.getLogger(EsTest.class);


	@Test
	public void test01() {
		RestTemplate restTemplate = new RestTemplate();
		User save = userDao.findById(1L).orElse(new User());
		String url = "http://192.168.1.102:8080/fuchain/account/create";
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject1 = new JSONObject();
		jsonObject.put("account", save.getAccount());
		jsonObject.put("password", save.getPassword());
		jsonObject.put("userGroup", 0);
		jsonObject.put("endTime", save.getUserZq());
		jsonObject1.put("name", save.getUserName());
		jsonObject1.put("unit", save.getUserUnit());
		jsonObject1.put("tel", save.getUserTel());
		jsonObject1.put("email", save.getUserEmail());
		jsonObject.put("userinfo", jsonObject1);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(), headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
		String user = responseEntity.getBody();//{"msg":"调用成功！","code":1}
		System.out.println(user);
	}

	@Test
	public void test4() {
		RestTemplate restTemplate = new RestTemplate();

		//创建请求头
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		//也可以这样设置contentType
		//MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		//headers.setContentType(type);

		//加不加Accept都可以
		//headers.add("Accept", MediaType.APPLICATION_JSON.toString());

		String url = "http://localhost:19090/tag/save";

		Tag student = new Tag();
		student.setTagName("asdf");
		String jsonString = JSONObject.toJSONString(student);
		System.out.println(jsonString);//{"age":10,"name":"sansan"}

		HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
		String user = responseEntity.getBody();//{"msg":"调用成功！","code":1}
		System.out.println(user);
	}


	@Test
	public void creat() {
		CreateIndexRequest request = new CreateIndexRequest("test");
		try {
			CreateIndexResponse createIndexResponse = esConfig.getRestHighLevelClient().indices().create(request, RequestOptions.DEFAULT);
			System.out.println(createIndexResponse);
		} catch (IOException e) {
			e.printStackTrace();
			throw new UserException(ResultEnum.UNKNOW_ERROR);
		}
	}

	@Test
	public void init() {
		String id = "3";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("slsj", VeDate.getStringDate());
		jsonObject.put("ledger", "mych");
		jsonObject.put("txid", "asdgasdfgasdfasdf");
		jsonObject.put("id", id);
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put("index", "index");
		jsonObject1.put("keyfirst", "keyfirsttest");
		jsonObject1.put("keysecond", "keysecond");
		JSONObject jsonObject2 = new JSONObject();
		jsonObject.put("head", jsonObject1);
		JSONObject jsonObject3 = new JSONObject();
		jsonObject3.put("message", "asdgasdfawegasdfhawefasgh");
		jsonObject.put("body", jsonObject3);
		IndexRequest indexRequest = new IndexRequest("test")
				.id(id).source(jsonObject, XContentType.JSON);

		try {
			esConfig.getRestHighLevelClient().index(indexRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void test04() {
		String s = "{\"datatype\":\"table\",\"inputs\":[{\"key\":\"tjfss\",\"value\":\"总量\"}],\"userid\":\"52\"}";
//		String s = "{\"datatype\":\"table\",\"userid\":\"51\"}";
		JSONObject jsonObject = JSON.parseObject(s);

		long userid = jsonObject.getLong("userid");
		JSONArray inputs = jsonObject.getJSONArray("inputs");
		String datatype = jsonObject.getString("datatype");
		JSONObject jsonObjectreturn = new JSONObject();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		Optional<User> byId = userDao.findById(52L);
		if (byId.isPresent()) {
			String index = byId.orElse(new User()).getAccount();
			SearchRequest searchRequest = new SearchRequest(index);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			if (!StringUtils.isEmpty(datatype)) {
				searchSourceBuilder.query(QueryBuilders.termQuery("head.dataType.keyword", "document"));
				//searchSourceBuilder.query(QueryBuilders.termQuery("head.dataIndex.tjfss.keyword","增量"));
			}
			if (inputs != null && inputs.size() != 0) {
//				for (int i=0; i<inputs.size(); i++) {
//					JSONObject jsonObject1 ;
//					jsonObject1 = inputs.getJSONObject(i);
//					String key = jsonObject1.getString("key");
//					String value = jsonObject1.getString("value");
//					//searchSourceBuilder.query(QueryBuilders.termQuery("head.dataType.keyword","table"));
				searchSourceBuilder.query(QueryBuilders.termQuery("head.dataIndex.fileName.keyword", "Archive.zip"));
//				}
			}
			searchSourceBuilder.from(0);
			searchSourceBuilder.size(10);
			searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
			searchSourceBuilder.sort("_id", SortOrder.DESC);
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
				hits.forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));
				long total = totalHits.value;
				jsonObjectreturn.put("total", total);
				jsonObjectreturn.put("list", list);

				System.out.println(jsonObjectreturn);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}


	@Test
	public void test03() {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();

		String index = "canal";
		String metadataname = "testDataName";

		SearchRequest searchRequest = new SearchRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("head.dataName", metadataname));
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from("2020-08-02 09:18:36").to("2030-02-02 09:18:36"));
		searchRequest.source(searchSourceBuilder);
		try {
			SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			SearchHits hits = search.getHits();
			TotalHits totalHits = hits.getTotalHits();
			System.out.println(totalHits.value);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * scroll游标超时时间，单位ms
	 */
	private final long SCROLL_TIMEOUT = 30000;
	/**
	 * scroll游标分页每次查询条数
	 */
	private int SCROLL_PageSize = 5000;

	@Test


	public void scrollTest() {
		RestHighLevelClient client = new HighLevelClient("192.168.10.21:9200", "elastic", "kareza").getClient();
		Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10L));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.size(500);
		SearchRequest searchRequest = new SearchRequest()
				// ES7已经去掉type，查询时加type
				.indices("blockinfo")
				.scroll(scroll)
				.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String scrollId = searchResponse.getScrollId();
		SearchHit[] searchHits = searchResponse.getHits().getHits();
		for (SearchHit searchHit : searchHits) {
			System.out.println("++++++++++++++searchHits++++++++++++"+searchHit.getSourceAsString());
		}
		//遍历搜索命中的数据，直到没有数据
		while (searchHits != null && searchHits.length > 0) {
			SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
			scrollRequest.scroll(scroll);
			try {
				searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			scrollId = searchResponse.getScrollId();
			System.out.println(scrollId);
			searchHits = searchResponse.getHits().getHits();
			if (searchHits != null && searchHits.length > 0) {
				for (SearchHit searchHit : searchHits) {
					System.out.println("++++++++++++++scrollRequest++++++++++++"+searchHit.getSourceAsString());
				}
			}
		}
		//clean scroll
		ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
		clearScrollRequest.addScrollId(scrollId);
		ClearScrollResponse clearScrollResponse = null;
		try {
			clearScrollResponse = client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("clear-scroll-error:{}",e);
		}
		boolean succeeded = clearScrollResponse.isSucceeded();
		System.out.println(succeeded);
	}


	@Test
	public void test02() {
		String indice = "zhige";
		RestHighLevelClient client = new HighLevelClient("192.168.10.21:9200", "elastic", "kareza").getClient();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest(indice);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("head.dataName", ""));
		searchRequest.source(searchSourceBuilder);
		try {
			SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			SearchHits hits = search.getHits();
			TotalHits totalHits = hits.getTotalHits();
			System.out.println(totalHits.value);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test05() {
		String indice = "wenwen";
		String dataHash = "41913d7bef5c276e3934ac592a6c6cc1";
		String dataName = "jq";
		RestHighLevelClient client = new HighLevelClient("192.168.10.21:9200", "elastic", "kareza").getClient();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest(indice);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

		if (!StringUtils.isEmpty(dataHash)) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("head.dataHash", dataHash));

		}
		if (!StringUtils.isEmpty(dataName)) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("head.dataName", dataName));
		}
//		searchSourceBuilder.query(QueryBuilders.matchQuery("head.dataName", "jq"));
//		searchSourceBuilder.query(QueryBuilders.matchQuery("head.dataHash", "41913d7bef5c276e3934ac592a6c6cc1"));
		searchSourceBuilder.query(boolQueryBuilder);
		searchRequest.source(searchSourceBuilder);
		List list = new ArrayList();
		try {
			SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			RestStatus restStatus = search.status();
			if (restStatus != RestStatus.OK) {
				System.out.println("error");
			}
			SearchHits hits = search.getHits();
			TotalHits totalHits = hits.getTotalHits();
			System.out.println(totalHits.value);
			hits.forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));
			System.out.println(list);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	@Test
	public void test() {
		String input = "老王2";
		String[] strs = {"user_id", "user_name"};
		String indice = "user";
		RestHighLevelClient client = new HighLevelClient("192.168.25.203:9200", "elastic", "kareza").getClient();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest(indice);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("head.dataName", indice));
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		for (String str : strs) {
			boolQueryBuilder.should(QueryBuilders.matchQuery("head.dataIndex." + str + ".keyword", input));

		}
		searchSourceBuilder.query(boolQueryBuilder);


		searchSourceBuilder.from(0);
		searchSourceBuilder.size(10);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort("_id", SortOrder.DESC);
		searchRequest.source(searchSourceBuilder);
		List list = new ArrayList();
		try {
			SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
			RestStatus restStatus = search.status();
			if (restStatus != RestStatus.OK) {
				System.out.println("error");
			}
			SearchHits hits = search.getHits();
			TotalHits totalHits = hits.getTotalHits();
			System.out.println(totalHits.value);
			hits.forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), JSONObject.class)));
			System.out.println(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void blocktest() throws IOException {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		ValueCountAggregationBuilder count = AggregationBuilders.count("count").field("dataHash.keyword");
		SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("sum").field("txCount");

		searchSourceBuilder.aggregation(count);
		searchSourceBuilder.aggregation(sumAggregationBuilder);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

		ParsedValueCount count2 = search.getAggregations().get("count");

		ParsedSum sum = search.getAggregations().get("sum");
		System.out.println(sum.getValue());
		System.out.println(count2.getValue());
	}

	@Test
	public void blocktests() throws IOException {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("canal");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		ValueCountAggregationBuilder countless = AggregationBuilders.count("countss").field("head.dataHash.keyword");
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from("2020-08-02 09:18:36").to("2030-02-02 09:18:36"));

		searchSourceBuilder.aggregation(countless);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

		ParsedValueCount count2 = search.getAggregations().get("countss");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("test", count2);
		System.out.println(jsonObject);

		System.out.println(count2.getValue());
	}


	@Test
	public void blocktestssss() throws IOException {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		TermsAggregationBuilder channelname = AggregationBuilders.terms("channelname").field("channelId.keyword");


		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from("2020-08-11 00:00:00").to("2020-08-25 23:59:59"));
		searchSourceBuilder.aggregation(channelname);


		searchRequest.source(searchSourceBuilder);
		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

		ParsedStringTerms channelname1 = search.getAggregations().get("channelname");

		JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(channelname1, SerializerFeature.IgnoreNonFieldGetter));
		JSONArray jsonArray = jsonObject.getJSONArray("buckets");
		String[] strs = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject1 = jsonArray.getJSONObject(i);
			strs[i] = jsonObject1.getString("keyAsString");
			System.out.println(jsonObject1.getString("keyAsString"));
		}

//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("test",JSON.parseObject(JSON.toJSONString(channelname1,SerializerFeature.IgnoreNonFieldGetter)));
//
//		System.out.println(JSON.parseObject(JSON.toJSONString(channelname1,SerializerFeature.IgnoreNonFieldGetter)));


//		for (Terms.Bucket entry : channelname1.getBuckets()) {
//			jsonObject.put("key",entry);
//
//			System.out.println(jsonObject);
//		}

	}


	@Test
	public void blocktestss() throws IOException {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("canal");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("time").field("slsj")
				.dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd");
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from("2020-08-11 00:00:00").to("2020-08-21 23:59:59"));
		searchSourceBuilder.aggregation(dateHistogramAggregationBuilder);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

		ParsedDateHistogram terms = search.getAggregations().get("time");


//
//		for (Histogram.Bucket bucket : terms.getBuckets()) {
//			ParsedStringTerms resellerToProduct = bucket.getAggregations().get("channelname");
//			System.out.println(JSON.toJSONString(resellerToProduct,SerializerFeature.IgnoreNonFieldGetter));
//		}


		//System.out.println(JSON.toJSONString(search.getAggregations(),SerializerFeature.IgnoreNonFieldGetter));

//		ParsedDateHistogram channelname1 = search.getAggregations().get("time");


		JSONObject jsonObject = new JSONObject();
		jsonObject.put("test", JSON.parseObject(JSON.toJSONString(terms, SerializerFeature.IgnoreNonFieldGetter)));

		System.out.println(jsonObject);


//		for (Terms.Bucket entry : channelname1.getBuckets()) {
//			jsonObject.put("key",entry);
//
//			System.out.println(jsonObject);
//		}


	}

	@Test
	public void blocktestsss() {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		ValueCountAggregationBuilder countles = AggregationBuilders.count("counts").field("_id");
		searchSourceBuilder.aggregation(countles);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ParsedValueCount counts = search.getAggregations().get("counts");
		System.out.println(JSON.toJSONString(counts));
	}


	@Test
	public void blocktestsssss() throws IOException {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		ValueCountAggregationBuilder count = AggregationBuilders.count("count").field("_id");
		searchSourceBuilder.query(QueryBuilders.termQuery("channelId.keyword", "share"));
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from("2020-08-25 00:00:00").to("2020-09-01 00:00:00"));

		searchSourceBuilder.aggregation(count);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedValueCount count2 = search.getAggregations().get("count");
		System.out.println(count2.getValue());
	}

	@Test
	public void test1() {
		long mych = blockinfoService.getAllnumBychannelnameAndtime("mych", "2020-08-19 00:00:00", "2020-08-19 00:00:00");
		System.out.println(mych);
	}

	@Test
	public void test2() {
		String[] allchannelname = blockinfoService.getAllchannelname();
		for (String str : allchannelname) {
			System.out.println(str);
		}
	}

}
