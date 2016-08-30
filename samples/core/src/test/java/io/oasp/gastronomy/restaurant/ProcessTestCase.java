package io.oasp.gastronomy.restaurant;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.oasp.gastronomy.restaurant.general.common.base.AbstractRestServiceTest;
import io.oasp.gastronomy.restaurant.tablemanagement.logic.api.to.TableEto;
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
public class ProcessTestCase extends AbstractRestServiceTest {

  private TablemanagementRestService service;

  private static final String BPMN_LOCATION = "./src/main/resources/processMealOrder.bpmn";

  private static final String BPMN_NAME = "processMealOrder.bpmn";

  private static final String PROCESS_KEY = "processMealOrder";

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private TaskService taskService;

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

  // @Rule
  // public ProcessEngineRule rule =
  // new ProcessEngineRule(new StandaloneInMemProcessEngineConfiguration().buildProcessEngine());

  /**
   * This test method finds the first free table and submits it to the process instance
   */
  @Test
  public void addTableToProcess() {

    // setup

    // then

    // given
    long id = 102;
    TableEto table = this.service.getTable(id);
    assertThat(table).isNotNull();
    assertThat(table.getId()).isEqualTo(id);

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("tableId", id);

    // when
    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);

    // then
    assertNotNull(processInstance);
    // org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat(processInstance).isStarted();

    ProcessEngineTests.assertThat(processInstance).isStarted();
    ProcessEngineTests.assertThat(processInstance).hasPassed("ServiceTask_DoItAll");

    // Task task = this.taskService.createTaskQuery().singleResult();
    // assertNotNull(task);
    // assertEquals("Gericht servieren", task.getName());

    /** if ProcessEngineRule in use: **/
    // ProcessInstance processInstance =
    // this.rule.getProcessEngine().getRuntimeService().startProcessInstanceByKey("processMealOrder");
    // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
    // assertNotNull(processInstance);
    // Task task = this.rule.getProcessEngine().getTaskService().createTaskQuery().singleResult();
    // assertNotNull(task);
    // assertEquals("Gericht servieren", task.getName());
  }

  /**
   * to test mocking - not working yet
   */
  // @Test
  // public void mockingTest() {
  //
  // // "Das ist ein Test" = result
  //
  // // this.mocky.getMockyData(); // returns "Das ist ein "
  // // when(this.mocky.getMockyData()).thenReturn("KEIN");
  //
  // String expectedResult = this.mocky.getMockyData() + " Test";
  // // String expectedResult = "Das ist ein Test";
  // String expectedMockyResult = "KEIN Test";
  // String test = this.processMealOrder.dummyMethod();
  //
  // Assert.assertNotNull(test);
  // Assert.assertEquals(expectedResult, test);
  //
  // }

}
