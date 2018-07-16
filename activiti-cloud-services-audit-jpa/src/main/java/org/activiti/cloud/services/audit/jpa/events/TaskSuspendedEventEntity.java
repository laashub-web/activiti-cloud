/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.services.audit.jpa.events;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.activiti.runtime.api.event.TaskRuntimeEvent;
import org.activiti.runtime.api.model.Task;

@Entity
@DiscriminatorValue(value = TaskSuspendedEventEntity.TASK_SUSPENDED_EVENT)
public class TaskSuspendedEventEntity extends TaskAuditEventEntity {

    protected static final String TASK_SUSPENDED_EVENT = "TaskSuspendedEvent";

    public TaskSuspendedEventEntity() {
    }

    public TaskSuspendedEventEntity(String eventId,
                                    Long timestamp) {
        super(eventId,
              timestamp,
              TaskRuntimeEvent.TaskEvents.TASK_SUSPENDED.name());
    }

    public TaskSuspendedEventEntity(String eventId,
                                    Long timestamp,
                                    String appName,
                                    String appVersion,
                                    String serviceName,
                                    String serviceFullName,
                                    String serviceType,
                                    String serviceVersion,
                                    Task task) {
        super(eventId,
              timestamp,
              TaskRuntimeEvent.TaskEvents.TASK_SUSPENDED.name(),
              appName,
              appVersion,
              serviceName,
              serviceFullName,
              serviceType,
              serviceVersion,
              task);
    }
}
