package io.oasp.gastronomy.restaurant.processmanagement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.oasp.gastronomy.restaurant.SpringBootApp;
import io.oasp.gastronomy.restaurant.general.common.base.AbstractRestServiceTest;
import io.oasp.gastronomy.restaurant.tablemanagement.service.api.rest.TablemanagementRestService;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootApp.class)
@TestPropertySource(properties = { "flyway.locations=filesystem:src/test/resources/db/tablemanagement" })
public class PrototypeProcessTest extends AbstractRestServiceTest {

  private TablemanagementRestService service;

  private static final String BPMN_LOCATION = "./src/main/resources/processes/prototypeProcess.bpmn";

  private static final String BPMN_NAME = "prototypeProcess.bpmn";

  private static final String PROCESS_KEY = "prototypeProcess";

  @Inject
  private ProcessEngine processEngine;

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private TaskService taskService;

  @Inject
  private UserTaskController userTaskController;

  // @Inject
  // private ProcessMealOrder processMealOrder;

  /**
   * Load and deploy the BPMN model - alternative to @Deploy annotation
   *
   * @throws FileNotFoundException
   */
  @Before
  public void loadModel() throws FileNotFoundException {

    Deployment deployment = this.repositoryService.createDeployment()
        .addInputStream(BPMN_NAME, new FileInputStream(BPMN_LOCATION)).deploy();
    assertNotNull(deployment.getId());
    assertTrue(this.repositoryService.getDeploymentResourceNames(deployment.getId()).contains(BPMN_NAME));

  }

  /**
   * Provides initialization previous to the creation of the text fixture.
   */
  @Before
  public void init() {

    this.service = getRestTestClientBuilder().build(TablemanagementRestService.class);

  }

  /**
   * Provides clean up after tests.
   */
  @After
  public void clean() {

    this.service = null;

  }

  @Test
  public void testMultipleInstances() {

    // Given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("processVariable", "Prozessinstanz 1");
    ProcessInstance processInstance1 = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
    assertNotNull(processInstance1);

    variables.put("processVariable", "Prozessinstanz 2");
    ProcessInstance processInstance2 = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
    assertNotNull(processInstance2);

    String id1 = processInstance1.getProcessInstanceId();
    String id2 = processInstance2.getProcessInstanceId();

    boolean comp = id1.equals(id2);
    assertTrue(!comp);

    ProcessInstance resultInstance = this.runtimeService.createProcessInstanceQuery()
        .variableValueEquals("processVariable", "Prozessinstanz 1").singleResult();
    assertNotNull(resultInstance);
    assertThat(resultInstance.getProcessInstanceId()).isEqualTo(processInstance1.getProcessInstanceId());
  }

  // /**
  // * This test method checks the functionality of UserTaskListener
  // */
  // @Test
  // public void testUserTaskListener() {
  //
  // // Given
  // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);
  // assertNotNull(processInstance);
  //
  // // Check that is waiting at user task, check assignee
  //
  // ProcessEngineTests.assertThat(processInstance).isStarted().isWaitingAt("UserTask_recordData").task()
  // .isAssignedTo("Dummy User");
  //
  // ProcessEngineTests.complete(task(), withVariables("decision", "apfel"));
  //
  // ProcessEngineTests.assertThat(processInstance).isStarted().isWaitingAt("UserTask_makeDecision");
  // ProcessEngineTests.complete(task(), withVariables("decision", "yes"));
  //
  // ProcessEngineTests.assertThat(processInstance).isEnded();
  //
  // }
  //
  // /**
  // * This method checks the complete method of the TaskController and the process variable that has been set
  // */
  // @Test
  // public void testCompleteTask() {
  //
  // // Given
  // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);
  //
  // // the decision will be made via UserTaskForm, where data is recorded and given to the process via submit button
  // String variableName = "decision";
  // String variableValue = "birne"; // birne oder apfel
  //
  // Task userTask = this.taskService.createTaskQuery().singleResult();
  //
  // assertNotNull(userTask);
  // String taskName = userTask.getName();
  //
  // assertThat(taskName.equals("UserTask_recordData"));
  //
  // // Then
  // try {
  // this.userTaskController.completeTask(variableName, variableValue);
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  //
  // // check if instance has passed UserTask
  // ProcessEngineTests.assertThat(processInstance).hasPassed("UserTask_recordData");
  //
  // String queriedVariable =
  // (String) this.runtimeService.getVariable(processInstance.getProcessInstanceId(), "decision");
  //
  // Assert.assertEquals(queriedVariable, variableValue);
  //
  // System.out.println("ProcessInstanceId vom Typ Execution ist:" + processInstance.getProcessInstanceId());
  // System.out.println("Prozessvariable nach 'Daten erfassen'" + queriedVariable);
  //
  // // check if instance has passed Decision Gateway
  // ProcessEngineTests.assertThat(processInstance).hasPassed("ExGate_decision");
  //
  // }

}
