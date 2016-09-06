package io.oasp.gastronomy.restaurant;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.withVariables;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.oasp.gastronomy.restaurant.general.common.base.AbstractRestServiceTest;
import io.oasp.gastronomy.restaurant.processmanagement.UserTaskController;
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

  private static final String BPMN_LOCATION = "./src/main/resources/prototypeProcess.bpmn";

  private static final String BPMN_NAME = "prototypeProcess.bpmn";

  private static final String PROCESS_KEY = "prototypeProcess";

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
  //
  // @Mock
  // private Mocky mocky;

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

  /**
   * This test method checks the UserTaskListener
   */
  @Test
  public void testUserTaskListener() {

    // Given
    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);
    assertNotNull(processInstance);

    // Check that is waiting at user task, check assignee

    ProcessEngineTests.assertThat(processInstance).isStarted().isWaitingAt("UserTask_recordData").task()
        .isAssignedTo("Dummy User");

    ProcessEngineTests.complete(task(), withVariables("decision", "apfel"));

    ProcessEngineTests.assertThat(processInstance).isStarted().isWaitingAt("UserTask_makeDecision");
    ProcessEngineTests.complete(task(), withVariables("decision", "yes"));

    ProcessEngineTests.assertThat(processInstance).isEnded();

  }

  /**
   * This method checks the complete method of the TaskController
   */
  @Test
  public void testCompleteTask() {

    // Given
    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);

    // mock/simulate UserTaskForm, where data is recorded and given to the process via submit button
    String variableName = "decision";
    String variableValue = "birne";

    Task userTask = this.taskService.createTaskQuery().singleResult();

    assertNotNull(userTask);
    String taskName = userTask.getName();

    assertThat(taskName.equals("UserTask_recordData"));

    // Then
    try {
      this.userTaskController.completeTask(variableName, variableValue);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // check if instance has passed UserTask and Gateway
    ProcessEngineTests.assertThat(processInstance).hasPassed("UserTask_recordData");

    ExecutionEntity executionEntity = (ExecutionEntity) this.runtimeService.createExecutionQuery().singleResult();
    String executionId = executionEntity.getId();
    Object processVariable = this.runtimeService.getVariable(executionId, "decision");

    assertThat((String) processVariable).isEqualTo("apfel");

  }

}
