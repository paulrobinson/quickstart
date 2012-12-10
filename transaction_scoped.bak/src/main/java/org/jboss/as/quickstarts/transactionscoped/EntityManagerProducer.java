package org.jboss.as.quickstarts.transactionscoped;

import org.apache.deltaspike.jpa.api.transaction.TransactionScoped;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @Author paul.robinson@redhat.com 07/12/2012
 */
public class EntityManagerProducer {

    @PersistenceContext
    private EntityManager entityManager;

    @Produces
    @TransactionScoped
    protected EntityManager createEntityManager() {
        return this.entityManager;
    }

    protected void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}