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
package org.jboss.as.quickstarts.wsat.recovery;

import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.mw.wst11.UserTransactionFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.quickstarts.wsat.recovery.jaxws.RestaurantServiceAT;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple set of tests for the RestaurantServiceAT
 * 
 * @author paul.robinson@redhat.com, 2012-01-04
 */
@RunWith(Arquillian.class)
public class ClientTest {

    private static final String ManifestMF = "Manifest-Version: 1.0\n"
          + "Dependencies: org.jboss.xts,org.jboss.modules,org.jboss.msc,org.jboss.jts\n";
   

    @Inject
    @ClientStub
    private RestaurantServiceAT client;

    /**
     * Create the deployment archive to be deployed by Arquillian.
     * 
     * @return a WebArchive representing the required deployment
     */
    @Deployment
    public static WebArchive createTestArchive() {

        return ShrinkWrap.create(WebArchive.class, "wsat-recovery.war")
                .addPackages(true, RestaurantServiceATImpl.class.getPackage())
                .addAsResource("context-handlers.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                .setManifest(new StringAsset(ManifestMF));
    }

    /**
     * Test the simple scenario where a booking is made and then committed.
     * 
     * @throws Exception if something goes wrong.
     */
    @Test
    public void testCommit() throws Exception {

        System.out
                .println("\n\nStarting 'testCommit'. This test invokes a WS within an AT. The AT is later committed, which causes the back-end resource(s) to be committed.");
        System.out.println("[CLIENT] Creating a new WS-AT User Transaction");
        UserTransaction ut = UserTransactionFactory.userTransaction();
        try {
            System.out
                    .println("[CLIENT] Beginning Atomic Transaction (All calls to Web services that support WS-AT wil be included in this transaction)");
            ut.begin();
            System.out.println("[CLIENT] invoking makeBooking() on WS");
            client.makeBooking("Paul");
            System.out.println("[CLIENT] committing Atomic Transaction (This will cause the AT to complete successfully)");
            ut.commit();

        } finally {
            rollbackIfActive(ut);
        }
    }

    /**
     * Utility method for rolling back a transaction if it is currently active.
     * 
     * @param ut The User Business Activity to cancel.
     */
    private void rollbackIfActive(UserTransaction ut) {
        try {
            ut.rollback();
        } catch (Throwable th2) {
            // do nothing, not active
        }
    }
}
