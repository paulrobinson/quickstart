package org.jboss.as.quickstarts.wsat.recovery;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author paul.robinson@redhat.com, 2012-07-30
 */
public class RecoveryListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        RecoveryModule.register();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        RecoveryModule.unregister();
    }
}
