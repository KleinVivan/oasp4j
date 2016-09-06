package io.oasp.gastronomy.restaurant.general;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 */

@Component
public class ExecuteServiceTask implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {

    System.out.println("processDefinitionId=" + execution.getProcessDefinitionId() + ", activityId="
        + execution.getCurrentActivityId() + ", activityName='"
        + execution.getCurrentActivityName().replaceAll("\n", " ") + "'" + ", processInstanceId="
        + execution.getProcessInstanceId() + ", businessKey=" + execution.getProcessBusinessKey() + ", executionId="
        + execution.getId() + ", modelName=" + execution.getBpmnModelInstance().getModel().getModelName()
        + ", elementId" + execution.getBpmnModelElementInstance().getId() + " \n\n");

  }

}
