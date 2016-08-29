package io.oasp.gastronomy.restaurant;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
// @TestPropertySource(properties = { "flyway.locations=filesystem:src/test/resources/db/tablemanagement" })
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

  @Before
  public void loadModel() throws FileNotFoundException {

    Deployment deployment = this.repositoryService.createDeployment()
        .addInputStream(BPMN_NAME, new FileInputStream(BPMN_LOCATION)).deploy();
    assertNotNull(deployment.getId());
    assertTrue(this.repositoryService.getDeploymentResourceNames(deployment.getId()).contains(BPMN_NAME));

  }

  // @Rule
  // public ProcessEngineRule rule =
  // new ProcessEngineRule(new StandaloneInMemProcessEngineConfiguration().buildProcessEngine());

  /**
   * Provides initialization previous to the creation of the text fixture.
   */
  // @Before
  // public void init() {
  //
  // getDbTestHelper().resetDatabase();
  // this.service = getRestTestClientBuilder().build(TablemanagementRestService.class);
  //
  // }

  /**
   * Provides clean up after tests.
   */
  // @After
  // public void clean() {
  //
  // this.service = null;
  //
  // }

  /**
   * This test method finds the first free table and submits it to the process instance
   */
  @Test
  public void addTableToProcess() {

    // // given
    // TableSearchCriteriaTo criteria = new TableSearchCriteriaTo();
    // assertThat(criteria).isNotNull();
    // criteria.setState(TableState.FREE);
    //
    // // when
    // PaginatedListTo<TableEto> tables = this.service.findTablesByPost(criteria);
    // List<TableEto> result = tables.getResult();
    //
    // // then
    // Long firstFreeTable = result.get(0).getId();
    // TableEto table = this.service.getTable(firstFreeTable);
    // assertThat(table).isNotNull();
    // assertThat(table.getId()).isEqualTo(firstFreeTable);
    //
    // String tableId = firstFreeTable.toString();
    // Map<String, Object> variables = new HashMap<String, Object>();
    // variables.put("table", tableId);

    // ProcessInstance processInstance =
    // this.rule.getProcessEngine().getRuntimeService().startProcessInstanceByKey("processMealOrder");

    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);
    // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
    // HistoricProcessInstance historicProcessInstance = this.historyService.createHistoricProcessInstanceQuery()
    // .processInstanceId(processInstance.getId()).singleResult();

    assertNotNull(processInstance);
    // assertThat(processInstance).isStarted();
    // assertThat(processInstance).hasPassed("ServiceTask_DoItAll");

    // Task task = this.taskService.createTaskQuery().singleResult();
    // // Task task = this.rule.getProcessEngine().getTaskService().createTaskQuery().singleResult();

    // assertNotNull(task);
    // assertEquals("Gericht servieren", task.getName());
  }

}
