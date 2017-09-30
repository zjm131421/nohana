package com.nohana.http;


import com.nohana.helper.CharsetHelpers;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class ConnectionManager {

    private static final Logger logger = LogManager.getLogger(ConnectionManager.class);

    private static final Object connectionManagerLocker = new Object();
    private static PoolingHttpClientConnectionManager connectionManager; //http请求连接池管理器


    public static CloseableHttpClient newHttpClient(){
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .build();
    }

    public static CloseableHttpClient newHttpRetryClient(){
        UnconditionalRequestRetryHandler unconditionalRequestRetryHandler = new UnconditionalRequestRetryHandler(3);
        return HttpClients.custom()
                .setRetryHandler(unconditionalRequestRetryHandler)
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .build();

    }

    public static void shutdown(){
        shutdownConnectionManager();
    }

    public static void init(KeyMaterialFactory keyMaterialFactory){
        if(connectionManager == null){
            synchronized(connectionManagerLocker){
                if(connectionManager == null){
                    initConnectionManager(keyMaterialFactory);
                }
            }
        }
    }

    private static void initConnectionManager(KeyMaterialFactory keyMaterialFactory) {
        SSLContextBuilder sslContextBuilder = SSLContexts.custom();
        if(keyMaterialFactory != null){
            keyMaterialFactory.get().forEach(keyMaterial -> {
                if(null != keyMaterial){
                    try {
                        //加载客户端证书用的
                        sslContextBuilder.loadKeyMaterial(keyMaterial.getKeyStore(),keyMaterial.getPassword());
                    } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
                        logger.printf(Level.ERROR,"Failed to add keyStore:%s", ExceptionUtils.getMessage(e));
                    }
                }
            });
        }
        try {
            //加载服务端相关信息用的
            sslContextBuilder.loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            logger.printf(Level.ERROR, "Failed to add keyStore: %s", ExceptionUtils.getMessage(e));
        }

        SSLConnectionSocketFactory sslSocketFactory;
        try {
            sslSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.printf(Level.ERROR, "Failed to build ssl context: %s", ExceptionUtils.getMessage(e));
            sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        }

        //设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("https",sslSocketFactory)
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();

        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setValidateAfterInactivity(5*1000);//空闲时间超时，释放socket重新建立
        connectionManager.setMaxTotal(500); //最大连接数
        connectionManager.setDefaultMaxPerRoute(50);//单路由最大并发数
        connectionManager.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(CharsetHelpers.UTF8).build());
    }

    private static void shutdownConnectionManager(){
        if(null != connectionManager){
            synchronized (connectionManagerLocker){
                connectionManager.shutdown();
                connectionManager = null;
            }
        }
    }


}
