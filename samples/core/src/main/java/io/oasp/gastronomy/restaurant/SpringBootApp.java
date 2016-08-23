package io.oasp.gastronomy.restaurant;

import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import io.oasp.module.jpa.dataaccess.api.AdvancedRevisionEntity;

//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class })
@SpringBootApplication(exclude = { EndpointAutoConfiguration.class })
@EntityScan(basePackages = { "io.oasp.gastronomy.restaurant" }, basePackageClasses = { AdvancedRevisionEntity.class })
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SpringBootApp {

  /**
   * Entry point for spring-boot based app
   *
   * @param args - arguments
   */

  @Inject
  private RuntimeService runtimeService;

  public static void main(String[] args) {

    SpringApplication.run(SpringBootApp.class, args);
  }

  // @PostConstruct
  // public void startProcess() {
  //
  // // prepare initial variable map
  // Map<String, Object> variables = new HashMap<String, Object>();
  // variables.put("OrderID", "01");
  // variables.put("Customer", "registered");
  //
  // // Start process instance with data
  //
  // // this.runtimeService.startProcessInstanceByKey("process_meal_order");
  // this.runtimeService.startProcessInstanceByKey("processMealOrder", variables);
  // // this.runtimeService.startProcessInstanceByKey("loanRequest");
  //
  // }
}
