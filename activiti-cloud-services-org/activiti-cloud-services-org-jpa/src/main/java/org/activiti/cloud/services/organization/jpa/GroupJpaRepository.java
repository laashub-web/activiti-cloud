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

package org.activiti.cloud.services.organization.jpa;

import java.util.Optional;

import org.activiti.cloud.organization.core.model.Group;
import org.activiti.cloud.organization.core.repository.GroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * JPA Repository for {@link Group} entity
 */
@RepositoryRestResource(path = "groups",
        collectionResourceRel = "groups",
        itemResourceRel = "groups")
public interface GroupJpaRepository extends JpaRepository<Group, String>,
                                            GroupRepository {

    Page<Group> findAllByParentId(String parentId,
                                  Pageable pageable);

    Page<Group> findAllByParentIdIsNull(Pageable page);

    @Override
    default Page<Group> getGroups(String parentId,
                                  Pageable pageable) {
        return findAllByParentId(parentId,
                                 pageable);
    }

    @Override
    default Page<Group> getTopLevelGroups(Pageable page) {
        return findAllByParentIdIsNull(page);
    }

    @Override
    default Optional<Group> findGroupById(String groupId) {
        return findById(groupId);
    }

    @Override
    default Group createGroup(Group group) {
        return save(group);
    }

    @Override
    default Group updateGroup(Group group) {
        return save(group);
    }

    @Override
    default void deleteGroup(Group group) {
        delete(group);
    }

}
