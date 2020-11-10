package com.flkj.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.flkj.config.EsConfig;
import com.flkj.dao.BlockinfoRespository;
import com.flkj.result.Result;
import com.flkj.utils.VeDate;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ：www
 * @date ：Created in 20-8-18 上午11:46
 * @description：
 * @modified By：
 * @version:
 */
@Service
public class BlockinfoService {
	@Autowired
	private BlockinfoRespository blockinfoRespository;
	@Autowired
	private EsConfig esConfig;
	public JSONObject blockinfosum() {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		ValueCountAggregationBuilder count = AggregationBuilders.count("count").field("dataHash.keyword");
		SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("sum").field("txCount");
		searchSourceBuilder.aggregation(count);
		searchSourceBuilder.aggregation(sumAggregationBuilder);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedValueCount count2 =  search.getAggregations().get("count");
		ParsedSum sum = search.getAggregations().get("sum");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("blocksum",count2.getValue());
		jsonObject.put("datasum",sum.getValue());
		return jsonObject;
	}
	public JSONObject blockinfosumyestoday() {
		Date dNow = new Date(); //当前时间
		Date dBefore ;
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1); //设置为前一天
		dBefore = calendar.getTime(); //得到前一天的时间
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String defaultStartDate = sdf.format(dBefore); //格式化前一天
		defaultStartDate = defaultStartDate+" 00:00:00";
		String defaultEndDate = defaultStartDate.substring(0,10)+" 23:59:59";
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		ValueCountAggregationBuilder countless = AggregationBuilders.count("countss").field("dataHash.keyword");
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from(defaultStartDate).to(defaultEndDate));
		searchSourceBuilder.aggregation(countless);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedValueCount count2 =  search.getAggregations().get("countss");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("yestoday",count2.getValue());
		return jsonObject;
	}
	public JSONObject blockinfocountBytime() {
		Date dNow = new Date(); //当前时间
		Date dBefore ;
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -7); //设置为前一天
		dBefore = calendar.getTime(); //得到前一天的时间
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String defaultStartDate = sdf.format(dBefore); //格式化前一天
		defaultStartDate = defaultStartDate+" 00:00:00";

		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("time").field("slsj")
				.dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd");

		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from(defaultStartDate).to(VeDate.getStringDate()));
		searchSourceBuilder.aggregation(dateHistogramAggregationBuilder);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedDateHistogram channelname1 = search.getAggregations().get("time");
		System.out.println(JSON.parseObject(JSON.toJSONString(channelname1, SerializerFeature.IgnoreNonFieldGetter)));
		return JSON.parseObject(JSON.toJSONString(channelname1, SerializerFeature.IgnoreNonFieldGetter));
	}

	public JSONObject userdatatotal(String index){
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest(index);
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

		ParsedValueCount counts =  search.getAggregations().get("counts");
		return JSON.parseObject(JSON.toJSONString(counts));
	}

	public String[] getAllchannelname() {
		String dataforaweekday = VeDate.dataforaweekday();
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		TermsAggregationBuilder channelname = AggregationBuilders.terms("channelname").field("channelId.keyword");
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from(dataforaweekday).to(VeDate.getStringDate()));
		searchSourceBuilder.aggregation(channelname);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedStringTerms channelnames = search.getAggregations().get("channelname");
		JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(channelnames,SerializerFeature.IgnoreNonFieldGetter));
		JSONArray jsonArray = jsonObject.getJSONArray("buckets");
		String[] strs = new String[jsonArray.size()];
		for (int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject1 = jsonArray.getJSONObject(i);
			strs[i] = jsonObject1.getString("keyAsString");
			System.out.println(jsonObject1.getString("keyAsString"));
		}
		return strs;
	}

	public long getAllnumBychannelnameAndtime(String index,String timestart,String timeend){
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest(index);
		GetIndexRequest request = new GetIndexRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		ValueCountAggregationBuilder count = AggregationBuilders.count("count").field("_id");
		searchSourceBuilder.query(QueryBuilders.termQuery("head.dataUser.keyword",index));
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from(timestart).to(timeend));

		searchSourceBuilder.aggregation(count);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		String[] indexs = new String[]{index};
		try {
			boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
			if (exists) {
				search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
				ParsedValueCount count2 =  search.getAggregations().get("count");
				return count2.getValue();
			} else {
				return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}


	public long getAllnumBychannelnameAndtimes(String channelId,String timestart,String timeend){
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest("blockinfo");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		ValueCountAggregationBuilder count = AggregationBuilders.count("count").field("_id");
		searchSourceBuilder.query(QueryBuilders.termQuery("channelId.keyword",channelId));
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from(timestart).to(timeend));

		searchSourceBuilder.aggregation(count);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedValueCount count2 =  search.getAggregations().get("count");
		return count2.getValue();
	}

	public long getAllnumByusernameAndtime(String username,String timestart,String timeend){
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest(username);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		ValueCountAggregationBuilder count = AggregationBuilders.count("count").field("dataHash.keyword");
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from(timestart).to(timeend));

		searchSourceBuilder.aggregation(count);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = null;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedValueCount count2 =  search.getAggregations().get("count");
		return count2.getValue();
	}

}
