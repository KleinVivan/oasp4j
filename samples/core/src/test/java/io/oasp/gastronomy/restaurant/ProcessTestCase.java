package io.oasp.gastronomy.restaurant;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.junit.Assert.assertEquals;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootApp.class)
// @TestPropertySource(properties = { "flyway.locations=filesystem:src/test/resources/db/tablemanagement" })
public class ProcessTestCase {// extends AbstractRestServiceTest {

  // private TablemanagementRestService service;

  private static final String BPMN_LOCATION = "./src/main/resources/processMealOrder.bpmn";

  private static final String BPMN_NAME = "processMealOrder.bpmn";

  private static final String PROCESS_KEY = "processMealOrder";

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private TaskService taskService;

  @Before
  public void loadModel() throws FileNotFoundException {
    // LOGGER.debug("Loading the model for unit testing.");

    Deployment deployment = this.repositoryService.createDeployment()
        .addInputStream(BPMN_NAME, new FileInputStream(BPMN_LOCATION)).deploy();
    assertNotNull(deployment.getId());
    assertTrue(this.repositoryService.getDeploymentResourceNames(deployment.getId()).contains(BPMN_NAME));

  }

  // @Rule
  // public ProcessEngineRule rule =
  // new ProcessEngineRule(new StandaloneInMemProcessEngineConfiguration().buildProcessEngine());

  // /**
  // * Provides initialization previous to the creation of the text fixture.
  // */
  // @Before
  // public void init() {
  //
  // getDbTestHelper().resetDatabase();
  // this.service = getRestTestClientBuilder().build(TablemanagementRestService.class);
  //
  // }
  //
  // /**
  // * Provides clean up after tests.
  // */
  // @After
  // public void clean() {
  //
  // this.service = null;
  //
  // }

  @Test
  public void addTableToProcess() {

    // ProcessInstance processInstance =
    // this.rule.getProcessEngine().getRuntimeService().startProcessInstanceByKey("processMealOrder");

    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);

    assertNotNull(processInstance);

    assertThat(processInstance).hasPassed("ServiceTask_DoItAll");

    Task task = this.taskService.createTaskQuery().singleResult();

    // Task task = this.rule.getProcessEngine().getTaskService().createTaskQuery().singleResult();
    assertNotNull(task);
    assertEquals("Gericht servieren", task.getName());

    // assertEquals("Bestellung bearbeiten", task.getName());
    // assertEquals("ServiceTask_DoItAll", task.getId());
  }

}
