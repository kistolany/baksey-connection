package com.microservices.common_service.helpers;

import java.util.List;

public interface CrudService <T, ID> {
    T create(T entity);
    T update(T entity, ID id);
    T delete(T entity);
    T findById(ID id);
    List<T> findAll();
}
