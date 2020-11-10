package com.flkj.scheduled;

import com.flkj.config.EsConfig;
import com.flkj.dao.MetadataDao;
import com.flkj.dao.UserDao;
import com.flkj.pojo.Metadata;
import com.flkj.pojo.User;
import com.flkj.utils.VeDate;
import com.flkj.view.UserMedataTagView;
import com.flkj.viewdao.UserMedataTagViewDao;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author ：www
 * @date ：Created in 20-8-21 上午9:51
 * @description：
 * @modified By：
 * @version:
 */
@Component
public class Userscheduled {
	@Autowired
	private EsConfig esConfig;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserMedataTagViewDao userMedataTagViewDao;
	@Autowired
	private MetadataDao metadataDao;
	@Scheduled(cron = "${scheduled.scheduleddata}")
	public void cron() {
		RestHighLevelClient restHighLevelClient = esConfig.getRestHighLevelClient();
		List<User> all = userDao.findAll();
		for (User user:all) {
			String index = user.getAccount();
			GetIndexRequest request = new GetIndexRequest(index);
			try {
				boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
				if (exists) {
					SearchRequest searchRequest = new SearchRequest(index);
					SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
					searchRequest.source(searchSourceBuilder);
					try {
						SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
						SearchHits hits = search.getHits();
						TotalHits totalHits = hits.getTotalHits();
						user.setUserSljys(totalHits.value);
						user.setUserSjtjsj(VeDate.getStringDate());
						System.out.println(user.toString());
						userDao.save(user);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<UserMedataTagView> all1 = userMedataTagViewDao.findAll();
		for (UserMedataTagView userMedataTagView:all1) {
			String index = userMedataTagView.getAccount();
			GetIndexRequest request = new GetIndexRequest(index);
			try {
				boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
				if (exists) {
					String metadataname = userMedataTagView.getDataName();
					SearchRequest searchRequest = new SearchRequest(index);
					SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
					searchSourceBuilder.query(QueryBuilders.matchQuery("head.dataName",metadataname));
					searchRequest.source(searchSourceBuilder);
					try {
						SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
						SearchHits hits = search.getHits();
						TotalHits totalHits = hits.getTotalHits();
						Metadata metadata = new Metadata();
						metadata.setDataId(userMedataTagView.getDataId());
						metadata.setDataSjts(totalHits.value);
						metadata.setDataSjtjsj(VeDate.getStringDate());
						metadataDao.save(metadata);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
