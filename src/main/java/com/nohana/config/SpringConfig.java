package com.nohana.config;

import com.nohana.SpringThreadTaskErrorHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ErrorHandler;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class SpringConfig {


    //任务调度
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ErrorHandler errorHandler = new SpringThreadTaskErrorHandler();
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(50);
        threadPoolTaskScheduler.setErrorHandler(errorHandler);
        return threadPoolTaskScheduler;
    }

    //线程池
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(50);
        threadPoolTaskExecutor.setMaxPoolSize(1000);
        return threadPoolTaskExecutor;
    }

    //数据源
    @Bean
    public DataSource dataSource(){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(NohanaConfig.getInstance().getJdbcDriverClassName());
        dataSource.setJdbcUrl(NohanaConfig.getInstance().getJdbcUrl());
        dataSource.setUsername(NohanaConfig.getInstance().getJdbcUsername());
        dataSource.setPassword(NohanaConfig.getInstance().getJdbcPassword());
        dataSource.setMaximumPoolSize(10); //最大连接数
        dataSource.setMinimumIdle(2); //最小空闲数
        dataSource.setConnectionTimeout(5000);
        dataSource.addDataSourceProperty("cachePrepStmts", "false");//是否对预编译使用local cache
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");//指定了local cache的大小
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");//长度限制，默认256。超过该长度后，不使用预编译
        return dataSource;
    }

    //允许使用命名参数而不是?占位符
    @Bean
    @Autowired
    public NamedParameterJdbcTemplate jdbcTemplate(DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }

    //事务管理
    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    //以编程的方式实现事务控制
    @Bean
    @Autowired
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager){
        return new TransactionTemplate(transactionManager);
    }

}
