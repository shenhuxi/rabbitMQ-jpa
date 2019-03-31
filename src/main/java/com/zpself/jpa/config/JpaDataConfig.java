package com.zpself.jpa.config;

import com.zpself.jpa.repository.MyRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@Configuration
@EnableJpaRepositories(basePackages = "com.zpself.**.repository",
                            repositoryFactoryBeanClass = MyRepositoryFactoryBean.class)
@EnableSpringDataWebSupport  
public class JpaDataConfig {  
  
}  