/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.service;

import java.lang.annotation.Annotation;
import java.util.logging.Logger;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.TransactionScoped;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.jboss.as.quickstarts.kitchensink.model.Member;

@Transactional
public class MemberRegistration {


    @Inject
    BeanManager beanManager;

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Member> memberEventSrc;

    @Transactional
    public void register(Member member) throws Exception {
        if (isContextActive(TransactionScoped.class)) {
            System.out.println("******************************* Active");
        } else {
            System.out.println("******************************* Not active");
        }
        log.info("Registering " + member.getName());
        em.persist(member);
        memberEventSrc.fire(member);
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
