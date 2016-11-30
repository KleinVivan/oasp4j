package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import io.oasp.gastronomy.restaurant.SpringBootApp;
import io.oasp.gastronomy.restaurant.general.common.DbTestHelper;
import io.oasp.gastronomy.restaurant.general.common.TestUtil;
import io.oasp.gastronomy.restaurant.processmanagement.logic.api.Processmanagement;
import io.oasp.module.test.common.base.ComponentTest;

/**
 * This is the test-case of {@link Processmanagement}.
 *
 * @author vmuschter
 */
@SpringApplicationConfiguration(classes = { SpringBootApp.class })
@WebAppConfiguration
public class ProcessManagementTest extends ComponentTest {

  @Inject
  private DbTestHelper dbTestHelper;

  @Inject
  private Processmanagement processmanagement;

  @Inject
  private ProcessEngine processEngine;

  /**
   * Initialization for the test.
   */
  @Before
  public void setUp() {

    this.dbTestHelper.setMigrationVersion("0002");
    // this.dbTestHelper.resetDatabase();

  }

  /**
   * Log out utility for the test.
   */
  @After
  public void tearDown() {

    TestUtil.logout();
  }

  @Test
  public void testStartProcess() {

    try {

      BpmnModelInstance modelInstance =
          Bpmn.createExecutableProcess("invoice").name("BPMN API Invoice Process").startEvent().name("Invoice received")
              .userTask().name("Assign Approver").camundaAssignee("demo").endEvent().done();

      // deploy process model
      this.processEngine.getRepositoryService().createDeployment().addModelInstance("invoice.bpmn", modelInstance)
          .deploy();

      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("var1", 1);
      variables.put("var2", 2);
      // try to create a dummy process programmatically
      ProcessInstance processInstance = this.processmanagement.startProcess("invoice", "123", variables);
      assertThat(processInstance).isNotNull();
      String processInstanceId = processInstance.getProcessInstanceId();

      assertThat(this.processEngine.getRuntimeService().getVariables(processInstanceId)).isNotEmpty();

    } catch (ConstraintViolationException e) {
      // BV is really painful as you need such code to see the actual error in JUnit.
      StringBuilder sb = new StringBuilder(64);
      sb.append("Constraints violated:");
      for (ConstraintViolation<?> v : e.getConstraintViolations()) {
        sb.append("\n");
        sb.append(v.getPropertyPath());
        sb.append(":");
        sb.append(v.getMessage());
      }
      throw new IllegalStateException(sb.toString(), e);
    }

  }
}
