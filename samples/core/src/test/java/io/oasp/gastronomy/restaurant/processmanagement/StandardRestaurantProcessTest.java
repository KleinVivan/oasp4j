package io.oasp.gastronomy.restaurant.processmanagement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
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
public class StandardRestaurantProcessTest extends AbstractRestServiceTest {

  private TablemanagementRestService service;

  private static final String BPMN_LOCATION = "./src/main/resources/Restaurantprozess_Standard_Process_Engine.bpmn";

  private static final String BPMN_NAME = "Restaurantprozess_Standard_Process_Engine.bpmn";

  private static final String PROCESS_KEY = "Process_Standard";

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private TaskService taskService;

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

    // getDbTestHelper().resetDatabase();
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
   * This test method finds the first free table and submits it to the process instance
   */
  @Test
  public void testProcess() {

    // when
    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);

    // then
    assertNotNull(processInstance);
    System.out.println("Process Instance: " + processInstance);

    // ProcessEngineTests.assertThat(processInstance).isStarted();
    ProcessEngineTests.assertThat(processInstance).isStarted().isWaitingAt("UserTask_acceptOrder").task()
        .hasCandidateGroup("cook");

    Task userTask = this.taskService.createTaskQuery().taskCandidateGroup("cook").singleResult();
    assertNotNull(userTask);

    System.out.println("Waiting at Task: " + userTask.getName());

    this.taskService.complete(userTask.getId());

    ProcessEngineTests.assertThat(processInstance).hasPassed("UserTask_acceptOrder");

  }

}
