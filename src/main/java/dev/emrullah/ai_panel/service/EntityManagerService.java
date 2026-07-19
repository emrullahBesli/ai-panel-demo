package dev.emrullah.ai_panel.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EntityManagerService {

    private final EntityManager entityManager;

    public EntityManagerService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Object executeJpaQuery(String jpql) {

        TypedQuery<?> query = entityManager.createQuery(jpql, Object.class);

        if (isSingleResultQuery(jpql)) {
            return query.getSingleResult();
        }

        return query.getResultList();
    }

    private boolean isSingleResultQuery(String jpql) {
        return jpql.contains("count(")
                || jpql.contains("sum(")
                || jpql.contains("avg(")
                || jpql.contains("min(")
                || jpql.contains("max(");
    }
}
