package org.jboss.as.quickstarts.transactionscoped;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;


/**
 * @Author paul.robinson@redhat.com 05/12/2012
 */
@RunWith(Arquillian.class)
public class TransactionScopedTest {

    @Inject
    MemberRegistration memberRegistration;

    @Deployment
    public static WebArchive createTestArchive() {

        String[] deps = {
                "org.apache.deltaspike.modules:deltaspike-jpa-module-api",
                "org.apache.deltaspike.modules:deltaspike-jpa-module-impl",
                "org.apache.deltaspike.core:deltaspike-core-api",
                "org.apache.deltaspike.core:deltaspike-core-impl"};

        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve(Arrays.asList(deps)).withTransitivity().asFile();


        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, Member.class.getPackage().getName())
                .addAsResource("META-INF/test-persistence.xml",
                        "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                .addAsLibraries(libs)
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml");
    }

    @Test
    public void simpleTest() throws Exception {
        memberRegistration.register(new Member("Paul"));
    }
}
