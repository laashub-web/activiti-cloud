package org.activiti.cloud.services.rest.api.resources;

import org.activiti.runtime.api.model.CloudVariableInstance;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class VariableInstanceResource extends Resource<CloudVariableInstance> {
    public VariableInstanceResource(CloudVariableInstance content,
                                    Link... links) {
        super(content,
                links);
    }
}
