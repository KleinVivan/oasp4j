package io.oasp.gastronomy.restaurant.processmanagement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ProcessTestCase extends AbstractRestServiceTest {

  private TablemanagementRestService service;

  // private static final String BPMN_LOCATION = "./src/main/resources/processMealOrder.bpmn";
  //
  // private static final String BPMN_NAME = "processMealOrder.bpmn";
  //
  // private static final String PROCESS_KEY = "processMealOrder";

  private static final String BPMN_LOCATION = "./src/main/resources/processOrderCustomerType.bpmn";

  private static final String BPMN_NAME = "processOrderCustomerType.bpmn";

  private static final String PROCESS_KEY = "processOrderCustomerType";

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
  public void testGatewayProcess() {

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("orderId", 21);

    // when
    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);

    // then
    assertNotNull(processInstance);
    System.out.println("Process Instance: " + processInstance);

    // ProcessEngineTests.assertThat(processInstance).isStarted();
    ProcessEngineTests.assertThat(processInstance).isStarted().isWaitingAt("UserTask_receiveOrder").task()
        .hasCandidateGroup("waiter");

    Task receiveOrderTask = this.taskService.createTaskQuery().taskCandidateGroup("waiter").singleResult();
    assertNotNull(receiveOrderTask);

    System.out.println("Waiting at Task: " + receiveOrderTask.getName());

    // not a registered customer
    variables.put("registered", Boolean.TRUE);
    this.taskService.complete(receiveOrderTask.getId(), variables);
    ProcessEngineTests.assertThat(processInstance).hasPassed("UserTask_receiveOrder", "gateway_type",
        "ServiceTask_processMealReg");

    List<Task> tasks =
        this.taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();

    for (int i = 0; i < tasks.size(); i++) {
      System.out.println(tasks.get(0).getName());
    }

    Task currentTask = this.taskService.createTaskQuery().singleResult();
    assertNotNull(currentTask);

    // TODO Human Task aus unterem Zweig abfragen, um den Test fehlschlagen zu lassen!

    // Task getPaymentTask = this.taskService.createTaskQuery().taskCandidateGroup("waiter").singleResult();
    // assertNotNull(getPaymentTask);
    //
    // System.out.println("Waiting at Task: " + getPaymentTask.getName());
    // assertThat((getPaymentTask.getName()).equals("Zahlung erhalten"));

    // Task task = this.taskService.createTaskQuery().singleResult();
    // assertNotNull(task);
    // assertEquals("Gericht servieren", task.getName());
  }

  /**
   * This test method finds the first free table and submits it to the process instance
   */
  // @Test
  // public void addTableToProcess() {
  //
  // // setup
  //
  // // then
  //
  // // given
  // long id = 102;
  // TableEto table = this.service.getTable(id);
  // assertThat(table).isNotNull();
  // assertThat(table.getId()).isEqualTo(id);
  //
  // Map<String, Object> variables = new HashMap<String, Object>();
  // variables.put("tableId", id);
  //
  // // when
  // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
  //
  // // then
  // assertNotNull(processInstance);
  // // org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat(processInstance).isStarted();
  //
  // ProcessEngineTests.assertThat(processInstance).isStarted();
  // ProcessEngineTests.assertThat(processInstance).hasPassed("ServiceTask_DoItAll");
  //
  // // Task task = this.taskService.createTaskQuery().singleResult();
  // // assertNotNull(task);
  // // assertEquals("Gericht servieren", task.getName());
  //
  // /** if ProcessEngineRule in use: **/
  // // ProcessInstance processInstance =
  // // this.rule.getProcessEngine().getRuntimeService().startProcessInstanceByKey("processMealOrder");
  // // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
  // // assertNotNull(processInstance);
  // // Task task = this.rule.getProcessEngine().getTaskService().createTaskQuery().singleResult();
  // // assertNotNull(task);
  // // assertEquals("Gericht servieren", task.getName());
  // }

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
