package com.flkj.service;

import com.flkj.config.EsConfig;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author ：www
 * @date ：Created in 20-8-25 上午10:00
 * @description：
 * @modified By：
 * @version:
 */
@Service
public class MetadataService {
	@Autowired
	private EsConfig esConfig;
	public long sumtimebetween(String index,String metadataname,String timestart,String timeend) {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("head.dataName",metadataname));
		searchSourceBuilder.query(QueryBuilders.rangeQuery("slsj").from(timestart).to(timeend));
		searchRequest.source(searchSourceBuilder);
		try {
			SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			SearchHits hits = search.getHits();
			TotalHits totalHits = hits.getTotalHits();
			return totalHits.value;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
