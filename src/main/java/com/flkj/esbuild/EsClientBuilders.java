package com.flkj.esbuild;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsClientBuilders {

    private static Logger log =  LoggerFactory.getLogger(EsClientBuilders.class);
    /**
     * 初始化 clientBuilder的详细说明
     * @return
     * oS1oHruLRImg_-OAJYq9EA
     */
    public static RestClientBuilder getClientBulider(String hosts,String username,String password) {

        String [] hostNamesPort = hosts.split(",");

        String host;
        int port;
        String[] temp;

        RestClientBuilder restClientBuilder = null;

        // restClient 初始化
        if (0 != hostNamesPort.length) {
            for (String hostPort : hostNamesPort) {
                temp = hostPort.split(":");
                host = temp[0].trim();
                port = Integer.parseInt(temp[1].trim());

                restClientBuilder = RestClient.builder(new HttpHost(host, port, "http"));
            }
        }

        /*RestClientBuilder 在构建 RestClient 实例时可以设置以下的可选配置参数*/

        // 设置请求头，避免每个请求都必须指定
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("header", "value")
        };
        restClientBuilder.setDefaultHeaders(defaultHeaders);

        //设置在同一请求进行多次尝试时应该遵守的超时时间。默认值为30秒，与默认`socket`超时相同。
        //如果自定义设置了`socket`超时，则应该相应地调整最大重试超时。
   //     restClientBuilder.setMaxRetryTimeoutMillis(10000);

        //设置每次节点发生故障时收到通知的侦听器。内部嗅探到故障时被启用。
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {

       //     @Override
            public void onFailure(HttpHost host) {
                // TODO 这里是当嗅探器测出某个节点有故障时，如何提醒用户，或有问题后做其他操作。
                log.error("ES cluster node failure:" + host);
            }
        });

        // 设置修改默认请求配置的回调（例如：请求超时，认证，或者其他设置）
        restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setSocketTimeout(10000);
            }
        });


        //设置修改 http 客户端配置的回调（例如：ssl 加密通讯，线程IO的配置，或其他任何设置）

        // 简单的身份认证
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                //线程设置
                httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(10).build());
                return httpClientBuilder;
            }
        });

        return restClientBuilder;
    }
}