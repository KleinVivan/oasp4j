package io.oasp.gastronomy.restaurant.general.configuration;

import java.io.IOException;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 */
@Configuration
public class CamundaProcessEngineConfiguration {

  @Value("${camunda.bpm.history-level:none}")
  private String historyLevel;

  // add more configuration here
  // ---------------------------

  // configure data source via application.properties
  @Inject
  private DataSource dataSource;

  @Inject
  private ResourcePatternResolver resourceLoader;

  @Bean
  public PlatformTransactionManager transactionManager() {

    return new DataSourceTransactionManager(this.dataSource);
  }

  @Bean
  public SpringProcessEngineConfiguration processEngineConfiguration() throws IOException {

    SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();

    config.setDataSource(this.dataSource);
    config.setTransactionManager(transactionManager());

    config.setDatabaseSchemaUpdate("true");
    config.setHistory("audit");
    config.setJobExecutorActivate(true);

    // deploy all processes from folder 'processes'
    Resource[] resources = this.resourceLoader.getResources("classpath:/*.bpmn");
    config.setDeploymentResources(resources);

    return config;
  }

  @Bean
  public ProcessEngineFactoryBean processEngine() throws IOException {

    ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
    factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
    return factoryBean;
  }

  @Bean
  public RepositoryService repositoryService(ProcessEngine processEngine) {

    return processEngine.getRepositoryService();
  }

  @Bean
  public RuntimeService runtimeService(ProcessEngine processEngine) {

    return processEngine.getRuntimeService();
  }

  @Bean
  public TaskService taskService(ProcessEngine processEngine) {

    return processEngine.getTaskService();
  }

  @Bean
  public HistoryService historyService(ProcessEngine processEngine) {

    return processEngine.getHistoryService();
  }

  // more engine services and additional beans ...

}