/*
 * Copyright 2017 Alfresco, Inc. and/or its affiliates.
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

package org.activiti.cloud.services.rest.controllers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.activiti.cloud.services.core.pageable.SecurityAwareRepositoryService;
import org.activiti.cloud.services.events.ProcessEngineChannels;
import org.activiti.cloud.services.events.configuration.CloudEventsAutoConfiguration;
import org.activiti.cloud.services.events.configuration.RuntimeBundleProperties;
import org.activiti.cloud.services.rest.conf.ServicesRestAutoConfiguration;
import org.activiti.runtime.api.model.ProcessDefinition;
import org.activiti.runtime.api.model.impl.ProcessDefinitionImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.activiti.alfresco.rest.docs.AlfrescoDocumentation.pageRequestParameters;
import static org.activiti.alfresco.rest.docs.AlfrescoDocumentation.pagedResourcesResponseFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProcessDefinitionAdminControllerImpl.class)
@EnableSpringDataWebSupport
@AutoConfigureMockMvc(secure = false)
@AutoConfigureRestDocs(outputDir = "target/snippets")
@Import({RuntimeBundleProperties.class,
        CloudEventsAutoConfiguration.class,
        ServicesRestAutoConfiguration.class})
@ComponentScan(basePackages = {"org.activiti.cloud.services.rest.assemblers", "org.activiti.cloud.alfresco"})
public class ProcessDefinitionAdminControllerImplIT {

    private static final String DOCUMENTATION_IDENTIFIER = "process-definition";

    private static final String DOCUMENTATION_IDENTIFIER_ALFRESCO = "process-definition-alfresco";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityAwareRepositoryService securityAwareRepositoryService;

    @MockBean
    private ProcessEngineChannels processEngineChannels;

    @Before
    public void setUp() {
        assertThat(processEngineChannels).isNotNull();
    }

    @Test
    public void getProcessDefinitions() throws Exception {

        ProcessDefinitionImpl processDefinition = new ProcessDefinitionImpl();
        processDefinition.setId("procId");
        processDefinition.setName("my process");
        processDefinition.setDescription("this is my process");
        processDefinition.setVersion(1);
        List<org.activiti.runtime.api.model.ProcessDefinition> processDefinitionList = Collections.singletonList(processDefinition);
        Page<org.activiti.runtime.api.model.ProcessDefinition> processDefinitionPage = new PageImpl<>(processDefinitionList,
                                                                                                      PageRequest.of(0,
                                                                                                                     10),
                                                                                                      processDefinitionList.size());
        when(securityAwareRepositoryService.getAllProcessDefinitions(any())).thenReturn(processDefinitionPage);

        this.mockMvc.perform(get("/admin/v1/process-definitions").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(DOCUMENTATION_IDENTIFIER + "/list",
                                responseFields(subsectionWithPath("page").description("Pagination details."),
                                               subsectionWithPath("_links").description("The hypermedia links."),
                                               subsectionWithPath("_embedded").description("The process definitions.")),
                                links(halLinks(),
                                      linkWithRel("self").description("The current page."))
                ));
    }

    @Test
    public void getProcessDefinitionsShouldUseAlfrescoGuidelineWhenMediaTypeIsApplicationJson() throws Exception {
        //given
        String processDefId = UUID.randomUUID().toString();
        ProcessDefinitionImpl processDefinition = new ProcessDefinitionImpl();
        processDefinition.setId(processDefId);
        processDefinition.setName("my process");
        processDefinition.setDescription("This is my process");
        processDefinition.setVersion(1);

        List<ProcessDefinition> processDefinitionList = Collections.singletonList(processDefinition);
        PageRequest pageable = PageRequest.of(1,
                                              10);
        Page<ProcessDefinition> processDefinitionPage = new PageImpl<>(processDefinitionList,
                                                                       pageable,
                                                                       11);
        given(securityAwareRepositoryService.getAllProcessDefinitions(any())).willReturn(processDefinitionPage);

        //when
        MvcResult result = this.mockMvc.perform(get("/admin/v1/process-definitions?skipCount=10&maxItems=10").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(document(DOCUMENTATION_IDENTIFIER_ALFRESCO + "/list",
                                pageRequestParameters(),
                                pagedResourcesResponseFields()))
                .andReturn();

        //then
        String responseContent = result.getResponse().getContentAsString();
        assertThatJson(responseContent)
                .node("list.pagination.skipCount").isEqualTo(10)
                .node("list.pagination.maxItems").isEqualTo(10)
                .node("list.pagination.count").isEqualTo(1)
                .node("list.pagination.hasMoreItems").isEqualTo(false)
                .node("list.pagination.totalItems").isEqualTo(11);
        assertThatJson(responseContent)
                .node("list.entries[0].entry.id").isEqualTo(processDefId)
                .node("list.entries[0].entry.name").isEqualTo("my process")
                .node("list.entries[0].entry.description").isEqualTo("This is my process")
                .node("list.entries[0].entry.version").isEqualTo(1);
    }
}
