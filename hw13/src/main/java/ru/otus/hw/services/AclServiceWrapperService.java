package ru.otus.hw.services;

import org.springframework.security.acls.model.Permission;

import java.io.Serializable;

public interface AclServiceWrapperService {
    void createPermission(Object object, Permission permission);

    void deletePermission(String type, Serializable identifier) ;

    void createAllPermission(Object object);
}
