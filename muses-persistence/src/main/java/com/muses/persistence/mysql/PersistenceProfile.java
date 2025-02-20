package com.muses.persistence.mysql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @ClassName PersistenceProfile
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:32
 */
@Configuration
@EntityScan(basePackages = "com.muses.persistence.mysql.entity")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryBeanMysql",
        transactionManagerRef = "transactionManagerMysql",
        basePackages = {"com.muses.persistence.mysql.repo"},
        enableDefaultTransactions = false)
@EnableConfigurationProperties({DataSourceProperties.class, JpaProperties.class})
public class PersistenceProfile {

    @Primary
    @Bean(name = "dataSourceMysql")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource dataSourceMysql() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "jpaPropertiesMysql")
    @ConfigurationProperties(prefix = "spring.jpa.mysql")
    public JpaProperties jpaPropertiesMysql() {
        return new JpaProperties();
    }

    @Primary
    @Bean(name = "entityManagerFactoryBeanMysql")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBeanMysql(@Qualifier("dataSourceMysql") DataSource dataSourceMysql,
                                                                              @Qualifier("jpaPropertiesMysql") JpaProperties jpaProperties,
                                                                              EntityManagerFactoryBuilder factoryBuilder) {
        Map<String, String> properties = jpaProperties.getProperties();
        properties.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());

        return factoryBuilder
                .dataSource(dataSourceMysql)
                .properties(properties)
                .packages("com.muses.persistence.mysql.entity")
                .persistenceUnit("mysqlPersistenceUnit")
                .build();
    }


    @Bean(name = "entityManagerFactoryMysql")
    public EntityManagerFactory entityManagerFactoryMysql(@Qualifier("entityManagerFactoryBeanMysql") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        return localContainerEntityManagerFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "entityManagerMysql")
    public EntityManager entityManagerMysql(@Qualifier("entityManagerFactoryMysql") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }


    @Primary
    @Bean(name = "transactionManagerMysql")
    public PlatformTransactionManager transactionManagerMysql(@Qualifier("entityManagerFactoryMysql") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
