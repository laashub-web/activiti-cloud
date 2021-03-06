/*
 * Copyright 2017-2020 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cloud.services.query.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedModelAssembler;
import org.activiti.cloud.api.task.model.CloudTask;
import org.activiti.cloud.services.query.app.repository.EntityFinder;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.model.QTaskEntity;
import org.activiti.cloud.services.query.model.TaskCandidateGroup;
import org.activiti.cloud.services.query.model.TaskCandidateUser;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.activiti.cloud.services.query.rest.assembler.TaskRepresentationModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@RestController
@RequestMapping(
        value = "/admin/v1/tasks",
        produces = {
                MediaTypes.HAL_JSON_VALUE,
                MediaType.APPLICATION_JSON_VALUE
        })
public class TaskAdminController {

    private final TaskRepository taskRepository;

    private TaskRepresentationModelAssembler taskRepresentationModelAssembler;

    private AlfrescoPagedModelAssembler<TaskEntity> pagedCollectionModelAssembler;

    private EntityFinder entityFinder;

    @Autowired
    public TaskAdminController(TaskRepository taskRepository,
                               TaskRepresentationModelAssembler taskRepresentationModelAssembler,
                               AlfrescoPagedModelAssembler<TaskEntity> pagedCollectionModelAssembler,
                               EntityFinder entityFinder) {
        this.taskRepository = taskRepository;
        this.taskRepresentationModelAssembler = taskRepresentationModelAssembler;
        this.pagedCollectionModelAssembler = pagedCollectionModelAssembler;
        this.entityFinder = entityFinder;
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedModel<EntityModel<CloudTask>> findAll(@RequestParam(name = "rootTasksOnly", defaultValue = "false") Boolean rootTasksOnly,
                                                       @RequestParam(name = "standalone", defaultValue = "false") Boolean standalone,
                                                       @QuerydslPredicate(root = TaskEntity.class) Predicate predicate,
                                                       Pageable pageable) {
        Predicate extendedPredicate = Optional.ofNullable(predicate)
                                              .orElseGet(BooleanBuilder::new);
        if (rootTasksOnly) {
            BooleanExpression parentTaskNull = QTaskEntity.taskEntity.parentTaskId.isNull();
            extendedPredicate= extendedPredicate !=null ? parentTaskNull.and(extendedPredicate) : parentTaskNull;
        }
        if (standalone) {
            BooleanExpression processInstanceIdNull = QTaskEntity.taskEntity.processInstanceId.isNull();
            extendedPredicate= extendedPredicate !=null ? processInstanceIdNull.and(extendedPredicate) : processInstanceIdNull;
        }

        Page<TaskEntity> page = taskRepository.findAll(extendedPredicate,
                                                       pageable);

        return pagedCollectionModelAssembler.toModel(pageable,
                                                  page,
                                                  taskRepresentationModelAssembler);
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public EntityModel<CloudTask> findById(@PathVariable String taskId) {

        TaskEntity taskEntity = entityFinder.findById(taskRepository,
                                                      taskId,
                                                      "Unable to find taskEntity for the given id:'" + taskId + "'");

        return taskRepresentationModelAssembler.toModel(taskEntity);
    }

    @RequestMapping(value = "/{taskId}/candidate-users", method = RequestMethod.GET)
    public List<String> getTaskCandidateUsers(@PathVariable String taskId) {
        TaskEntity taskEntity = entityFinder.findById(taskRepository,
                                                      taskId,
                                                      "Unable to find taskEntity for the given id:'" + taskId + "'");

        return taskEntity.getTaskCandidateUsers()!=null ?
                                      taskEntity.getTaskCandidateUsers().stream().map(TaskCandidateUser::getUserId).collect(Collectors.toList()) :
                                      null;
    }

    @RequestMapping(value = "/{taskId}/candidate-groups", method = RequestMethod.GET)
    public List<String> getTaskCandidateGroups(@PathVariable String taskId) {
        TaskEntity taskEntity = entityFinder.findById(taskRepository,
                                                      taskId,
                                                      "Unable to find taskEntity for the given id:'" + taskId + "'");

        return taskEntity.getTaskCandidateGroups()!=null ?
                                       taskEntity.getTaskCandidateGroups().stream().map(TaskCandidateGroup::getGroupId).collect(Collectors.toList()) :
                                       null;
    }

}
