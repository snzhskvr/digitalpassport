package com.digitalpassport.service;

import com.digitalpassport.BaseIntegrationTest;
import com.digitalpassport.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleServiceTest extends BaseIntegrationTest {

    @Autowired
    private RoleService roleService;

    @Test
    void testGetAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        assertEquals(3, roles.size());
        assertTrue(roles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName())));
        assertTrue(roles.stream().anyMatch(r -> "ROLE_EDITOR".equals(r.getName())));
        assertTrue(roles.stream().anyMatch(r -> "ROLE_READER".equals(r.getName())));
    }
}
