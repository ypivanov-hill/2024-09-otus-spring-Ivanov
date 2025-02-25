package ru.otus.hw.security;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

public interface AclMethodSecurityExpressionOperations extends MethodSecurityExpressionOperations {

    boolean isAdministrator(Object targetId, Class<?> targetClass);

    boolean isAdministrator(Object target);

    boolean canRead(Object targetId, Class<?> targetClass);

    boolean canWrite(Object targetId, Class<?> targetClass);

    boolean canDelete(Object targetId, Class<?> targetClass);
}
