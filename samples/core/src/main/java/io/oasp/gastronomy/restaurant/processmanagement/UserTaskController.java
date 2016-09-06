package io.oasp.gastronomy.restaurant.processmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.stereotype.Component;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */

@Component
public class UserTaskController {
  @Inject
  RepositoryService repositoryService;

  @Inject
  RuntimeService runtimeService;

  @Inject
  TaskService taskService;

  // protected static Pattern EXPRESSION_PATTERN = Pattern.compile("[\\$#]\\{\\s*(\\w+)\\s*==\\s*'([^']+)'\\s*}");

  /**
   * Gets the name of the exclusive gateway.
   *
   * @return the name attribute value of the exclusive gateway
   */
  public String getQuestion() {

    String taskId = getTaskId();
    BpmnModelInstance modelInstance = getModelInstance();
    return getGatewayName(taskId, modelInstance);
  }

  /**
   * Returns the name of the following exclusive gateway.
   *
   * @param taskId the ID of the current task
   * @param modelInstance the BPMN model instance
   * @return the name attribute value of the following exclusive gateway
   */
  protected String getGatewayName(String taskId, BpmnModelInstance modelInstance) {

    ExclusiveGateway gateway = getExclusiveGateway(taskId, modelInstance);
    return gateway.getName();
    // return stripLineBreaks(gateway.getName());
  }

  /**
   * Returns a list of button values for every outgoing conditional sequence flows.
   *
   * @return a list of button values as a map
   */
  public List<Map<String, String>> getButtons() {

    String taskId = getTaskId();
    BpmnModelInstance modelInstance = getModelInstance();
    return getButtons(taskId, modelInstance);
  }

  /**
   * Returns a list of values for each button to generate.
   *
   * @param taskId the ID of the current task
   * @param modelInstance the BPMN model instance
   * @return the list of button values
   */
  protected List<Map<String, String>> getButtons(String taskId, BpmnModelInstance modelInstance) {

    ExclusiveGateway gateway = getExclusiveGateway(taskId, modelInstance);

    List<Map<String, String>> buttonValues = new ArrayList<Map<String, String>>();
    for (SequenceFlow sequenceFlow : gateway.getOutgoing()) {
      buttonValues.add(getConditionValues(sequenceFlow));
    }

    return buttonValues;
  }

  /**
   * Gets the condition name, variable name and value for a sequence flow.
   *
   * @param sequenceFlow the sequence flow with the condition
   * @return the value map for this condition
   */
  private Map<String, String> getConditionValues(SequenceFlow sequenceFlow) {

    Map<String, String> values = new HashMap<String, String>();

    values.put("conditionName", sequenceFlow.getName());
    // values.put("conditionName", stripLineBreaks(sequenceFlow.getName()));

    String condition = sequenceFlow.getConditionExpression().getTextContent();
    // Matcher matcher = EXPRESSION_PATTERN.matcher(condition);
    // if (matcher.matches()) {
    // values.put("variableName", stripLineBreaks(matcher.group(1)));
    // values.put("variableValue", stripLineBreaks(matcher.group(2)));
    // }

    values.put("variableName", "decision");
    values.put("variableValue", condition);
    return values;
  }

  /**
   * Completes the user task and sets value of the variable with <code>variableName</code> to <code>variableValue</code>
   * . Is called by button in task form
   *
   * @param variableName the name of the variable to set
   * @param variableValue the value to set the variable to
   * @throws IOException if the task completion fails
   */
  public void completeTask(String variableName, String variableValue) throws IOException {

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put(variableName, variableValue);
    this.taskService.complete(getTaskId(), variables);
  }

  /**
   * Gets the current BPMN model instance.
   *
   * @return the BPMN model instance
   */
  private BpmnModelInstance getModelInstance() {

    String processDefinitionId = this.taskService.createTaskQuery().singleResult().getProcessDefinitionId();
    return this.repositoryService.getBpmnModelInstance(processDefinitionId);
  }

  /**
   * Gets the ID of the current task.
   *
   * @return the task ID
   */
  private String getTaskId() {

    // return this.taskService.createTaskQuery().singleResult().getTaskDefinitionKey();
    return this.taskService.createTaskQuery().singleResult().getId();

  }

  /**
   * Gets the succeeding exclusive gateway of the current task.
   *
   * @param taskId the ID of the current task
   * @param modelInstance the BPMN model instance
   * @return the succeeding exclusive gateway element
   */
  private ExclusiveGateway getExclusiveGateway(String taskId, BpmnModelInstance modelInstance) {

    UserTask userTask = (UserTask) modelInstance.getModelElementById(taskId);
    return (ExclusiveGateway) userTask.getSucceedingNodes().singleResult();
  }

  /**
   * Removes line breaks inside the string.
   *
   * @param text the text to remove line breaks from
   * @return the stripped text
   */
  private String stripLineBreaks(String text) {

    return text.trim().replaceAll("\n", " ");
  }

  /**
   * Store recorded Data in Database
   */

  public void saveTaskFormData() {

    // TODO get all relevant input data here
  }
}
