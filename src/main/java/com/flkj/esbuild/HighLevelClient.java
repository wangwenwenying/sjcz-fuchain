package com.flkj.esbuild;

import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


public class HighLevelClient {

    private static Logger log =  LoggerFactory.getLogger(HighLevelClient.class);
    private  RestClientBuilder restClientBuilder;

    // 实例化客户端
    private static RestHighLevelClient restHighLevelClient;
    // 嗅探器实例化
    private static Sniffer sniffer;

    public HighLevelClient(String hosts){
        this.restClientBuilder = EsClientBuilders.getClientBulider(hosts,null,null);
    }

//    public HighLevelClient(Map<String,String> propertiesMap){
//
//        String hosts = propertiesMap.get("hosts");
//        String userName = propertiesMap.get("username");
//        String password = propertiesMap.get("password");
//
//        this.restClientBuilder = EsClientBuilders.getClientBulider(hosts,userName,password);
//    }

    public HighLevelClient(String hosts,String userName,String password){
        this.restClientBuilder = EsClientBuilders.getClientBulider(hosts,userName,password);
    }

    /**
     * 开启client，sniffer
     * @return
     */
    public RestHighLevelClient getClient() {
        restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        //十秒刷新并更新一次节点
        sniffer = Sniffer.builder(restHighLevelClient.getLowLevelClient())
                .setSniffAfterFailureDelayMillis(10000)
                .build();

        return restHighLevelClient;
    }


    public void closeRestHighLevelClient(){
        if (null != restHighLevelClient) {
            try {
                sniffer.close();
                restHighLevelClient.close();
            } catch (IOException e) {
                log.error("restHighLevelClient close error:" + e.getMessage());
            }
        }
    }
}
