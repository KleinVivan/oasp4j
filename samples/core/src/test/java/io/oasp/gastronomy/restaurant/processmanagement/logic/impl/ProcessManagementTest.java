package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
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

    List<ProcessInstance> processInstancesList =
        this.processEngine.getRuntimeService().createProcessInstanceQuery().active().list();

    for (ProcessInstance pi : processInstancesList) {
      this.processEngine.getRuntimeService().deleteProcessInstance(pi.getProcessInstanceId(), null);
    }
    TestUtil.logout();
  }

  /**
   *
   */
  @Test
  public void testStartProcess() {

    // given
    BpmnModelInstance modelInstance =
        Bpmn.createExecutableProcess("testprocess").name("BPMN API Test Process").startEvent().name("Event received")
            .userTask().name("Assign Approver").camundaAssignee("demo").endEvent().done();

    // deploy process model
    this.processEngine.getRepositoryService().createDeployment().addModelInstance("testprocess.bpmn", modelInstance)
        .deploy();

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("var1", 1);
    variables.put("var2", 2);
    // try to create a dummy process programmatically
    ProcessInstance processInstance = this.processmanagement.startProcess("testprocess", "123", variables);
    assertThat(processInstance).isNotNull();
    String processInstanceId = processInstance.getProcessInstanceId();

    assertThat(this.processEngine.getRuntimeService().getVariables(processInstanceId)).isNotEmpty();
  }

  @Test
  public void testStopProcess() {

    // given
    BpmnModelInstance modelInstance =
        Bpmn.createExecutableProcess("testprocess").name("BPMN API Test Process").startEvent().name("Event received")
            .userTask().name("Assign Approver").camundaAssignee("demo").endEvent().done();

    // deploy process model
    this.processEngine.getRepositoryService().createDeployment().addModelInstance("testprocess.bpmn", modelInstance)
        .deploy();

    ProcessInstance processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("testprocess");

    this.processmanagement.stopProcess(processInstance.getProcessInstanceId(), "Testing purpose");

    ProcessEngineTests.assertThat(processInstance).isEnded();

    ProcessDefinition myProcessDefinition = this.processEngine.getRepositoryService().createProcessDefinitionQuery()
        .processDefinitionName("BPMN API Test Process").latestVersion() // we are only interested in the latest version
        .singleResult();

    // we onlywant the finished process instances
    List<HistoricProcessInstance> processInstances = this.processEngine.getHistoryService()
        .createHistoricProcessInstanceQuery().processDefinitionId(myProcessDefinition.getId()).finished().list();

    ProcessEngineTests.assertThat(processInstances.size()).isEqualTo(1);

  }

  @Test
  public void testAssignUserToCurrentTask() {

    // given
    BpmnModelInstance modelInstance =
        Bpmn.createExecutableProcess("testprocess").name("BPMN API Test Process").startEvent().name("Event received")
            .userTask().name("Assign Approver").camundaAssignee("demo").endEvent().done();

    // deploy process model
    this.processEngine.getRepositoryService().createDeployment().addModelInstance("testprocess.bpmn", modelInstance)
        .deploy();

    ProcessInstance processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("testprocess");

    // then
    this.processmanagement.setAssigneeToCurrentTask(processInstance, "A User");
    String assignee = this.processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId())
        .singleResult().getAssignee();
    assertThat(assignee).isNotEmpty();
    assertThat(assignee).isEqualTo("A User");
  }

  @Test
  public void testCompleteCurrentTask() {

    // given
    BpmnModelInstance modelInstance =
        Bpmn.createExecutableProcess("testprocess").name("BPMN API Test Process").startEvent().name("Event received")
            .userTask().id("UT_assignApprover").name("Assign Approver").camundaAssignee("demo").endEvent().done();

    //
    // deploy process model
    this.processEngine.getRepositoryService().createDeployment().addModelInstance("testprocess.bpmn", modelInstance)
        .deploy();

    ProcessInstance processInstance = this.processEngine.getRuntimeService().startProcessInstanceByKey("testprocess");

    Bpmn.writeModelToStream(System.out, modelInstance);

    ProcessEngineTests.assertThat(this.processEngine.getRuntimeService().createProcessInstanceQuery()
        .processInstanceId(processInstance.getId()).singleResult()).isStarted().isWaitingAt("UT_assignApprover");

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("var1", 11);
    variables.put("var2", 22);
    this.processmanagement.completeCurrentTask(processInstance, variables);

    // assert that task is completed
    Task task = this.processEngine.getTaskService().createTaskQuery().singleResult();
    assertThat(task).isNull();

    // assert that process is ended
    ProcessEngineTests.assertThat(processInstance).isEnded();
  }

}
