package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.processmanagement.logic.api.Processmanagement;

/**
 * @author vmuschter
 */
@Component
abstract class ProcessmanagementImpl implements Processmanagement {

  @Inject
  protected ProcessEngine processEngine;

  @Override
  public ProcessInstance startProcess(String processKeyName, String businessKey, Map<String, Object> variables) {

    ProcessInstance processInstance =
        this.processEngine.getRuntimeService().startProcessInstanceByKey(processKeyName, businessKey, variables);

    return processInstance;
  }

  @Override
  public ProcessInstance getProcess(String processInstanceId) {

    ProcessInstance processInstance = this.processEngine.getRuntimeService().createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();

    return processInstance;
  }

  @Override
  public void stopProcess(String processId, String deleteReason) {

    this.processEngine.getRuntimeService().deleteProcessInstance(processId, deleteReason);

  }

  @Override
  public void setAssigneeToCurrentTask(ProcessInstance processInstance, String assignee) {

    Task task = this.processEngine.getTaskService().createTaskQuery()
        .processInstanceId(processInstance.getProcessInstanceId()).singleResult();

    this.processEngine.getTaskService().setAssignee(task.getId(), assignee);
  }

  @Override
  public void completeCurrentTask(ProcessInstance processInstance, Map<String, Object> variables) {

    Task task = this.processEngine.getTaskService().createTaskQuery()
        .processInstanceId(processInstance.getProcessInstanceId()).active().singleResult();
    this.processEngine.getTaskService().complete(task.getId(), variables);
  }

}
