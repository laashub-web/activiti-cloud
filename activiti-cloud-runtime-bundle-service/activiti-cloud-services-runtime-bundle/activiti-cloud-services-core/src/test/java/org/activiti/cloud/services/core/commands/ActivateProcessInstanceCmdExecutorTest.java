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
package org.activiti.cloud.services.core.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.activiti.api.process.model.payloads.ResumeProcessPayload;
import org.activiti.api.process.runtime.ProcessAdminRuntime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class ActivateProcessInstanceCmdExecutorTest {

    @InjectMocks
    private ResumeProcessInstanceCmdExecutor activateProcessInstanceCmdExecutor;

    @Mock
    private ProcessAdminRuntime processAdminRuntime;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void resumeProcessInstanceCmdExecutorTest() {
        ResumeProcessPayload resumeProcessPayload = new ResumeProcessPayload("x");

        assertThat(activateProcessInstanceCmdExecutor.getHandledType()).isEqualTo(ResumeProcessPayload.class.getName());

        activateProcessInstanceCmdExecutor.execute(resumeProcessPayload);

        verify(processAdminRuntime).resume(resumeProcessPayload);

    }
}
