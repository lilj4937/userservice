package com.shcx.user.userservice.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.shcx.user.userservice.dao", transactionManagerRef = "jpaTransactionManager", entityManagerFactoryRef = "localContainerEntityManagerFactoryBean")
@EnableTransactionManagement
public class JpaConfiguration {

	@Autowired
	@Bean
	public JpaTransactionManager jpaTransactionManager(
			@Qualifier(value = "sqlite3DataSource") DataSource dataSource,
			EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
		jpaTransactionManager.setDataSource(dataSource);

		return jpaTransactionManager;
	}

	@Autowired
	@Bean
	LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(
			@Qualifier(value = "sqlite3DataSource") DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setDataSource(dataSource);
		localContainerEntityManagerFactoryBean
				.setPackagesToScan("com.shcx.user.userservice.pojo");
		localContainerEntityManagerFactoryBean
				.setJpaVendorAdapter(jpaVendorAdapter);
		return localContainerEntityManagerFactoryBean;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		hibernateJpaVendorAdapter.setShowSql(true);
		hibernateJpaVendorAdapter
				.setDatabasePlatform("com.enigmabridge.hibernate.dialect.SQLiteDialect");
		return hibernateJpaVendorAdapter;
	}
}
