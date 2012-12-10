package org.jboss.as.quickstarts.transactionscoped;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

/**
 * @Author paul.robinson@redhat.com 07/12/2012
 */
public class MemberRegistration {

    //@Inject
    //private EntityManager em;

    @Inject
    BeanManager beanManager;

    @Transactional
    public void register(Member member) throws Exception {
        //em.persist(member);
        if (isContextActive(Transactional.class)) {
            System.out.println("******************************* Active");
        } else {
            System.out.println("******************************* Not active");
        }
    }

    boolean isContextActive(Class<? extends Annotation> scope) {
        try {
            beanManager.getContext(scope);
        } catch (ContextNotActiveException e) {
            return false;
        }
        return true;
    }

}