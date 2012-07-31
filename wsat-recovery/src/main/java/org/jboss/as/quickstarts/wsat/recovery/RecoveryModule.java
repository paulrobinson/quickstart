package org.jboss.as.quickstarts.wsat.recovery;

import org.jboss.jbossts.xts.recovery.participant.at.XTSATRecoveryModule;
import org.jboss.jbossts.xts.recovery.participant.at.XTSATRecoveryManager;
import com.arjuna.wst.Durable2PCParticipant;

import java.io.ObjectInputStream;

/**
 * @Author paul.robinson@redhat.com 25/07/2012
 */
public class RecoveryModule implements XTSATRecoveryModule
{
    /**
     * the singleton recovery module
     */
    private static RecoveryModule theRecoveryModule = null;


    /**
     * called during deployment of an xts-demo web service to ensure the recovery module for the
     * demo is installed whenever any of the services is active
     */
    public static void register()
    {
        if (theRecoveryModule == null) {
            System.out.println("[RECOVERY] Registering recovery module");
            theRecoveryModule = new RecoveryModule();
            XTSATRecoveryManager.getRecoveryManager().registerRecoveryModule(theRecoveryModule);
        }
    }

    /**
     * called during undeployment of the web service to ensure the recovery module for
     * the demo is deinstalled once none of the services is active
     */
    public static void unregister()
    {
        System.out.println("[RECOVERY] De-registering recovery module");
        XTSATRecoveryManager.getRecoveryManager().unregisterRecoveryModule(theRecoveryModule);
    }

    /**
     * called during recovery processing to allow an application to identify a participant id
     * belonging to one of its participants and recreate the participant by deserializing
     * it from the supplied object input stream. n.b. this is only appropriate in case the
     * participant was originally saved using serialization.
     *
     * @param id     the id used when the participant was created
     * @param stream a stream from which the application should deserialise the participant
     *               if it recognises that the id belongs to the module's application
     * @return a recovered durable participant if the id belongs to this application otherwise null
     * @throws Exception if an error occurs deserializing the durable participant
     */
    public Durable2PCParticipant deserialize(String id, ObjectInputStream stream) throws Exception
    {
        if (id.startsWith("restaurantServiceAT:")) {
            System.out.println("[RECOVERY] Attempting to deserialize RestaurantParticipant " + id);
            RestaurantParticipant participant = (RestaurantParticipant)stream.readObject();
            System.out.println("[RECOVERY] Deserialized RestaurantParticipantAT " + id);
            return participant;
        }
        else {
            System.out.println("[RECOVERY] Didn't deserialize participant as the id was unrecognized to this recovery module: " + id);
        }

        return null;
    }

    /**
     * called during recovery processing to allow an application to identify a participant id
     * belonging to one of its participants and use the saved recovery state to recreate the
     * participant. n.b. this is only appropriate in case the participant was originally saved
     * after being converted to a byte array using the PersistableATParticipant interface.
     *
     * @param id            the id used when the participant was created
     * @param recoveryState a byte array returned form the original participant via a call to
     *                      method getRecoveryState of interface PersistableATParticipant
     * @return null as the demo application does not use this recovery method
     * @throws Exception if the id belongs to this application
     */
    public Durable2PCParticipant recreate(String id, byte[] recoveryState) throws Exception {
        if (id.startsWith("restaurantServiceAT:")) {
            // this should not get called -- participants are saved and restored using serialization
            throw new Exception("[RECOVERY] invalid request to recreate() WS-AT participant " + id);
        }
        return null;
    }

    /**
     * participant recovery modules may need to perform special processing when the a recovery scan has
     * completed. in particular it is only after the first recovery scan has completed they can identify
     * whether locally prepared changes are accompanied by a recreated participant and roll back changes
     * for those with no such participant.
     */
    public void endScan()
    {
        MockRestaurantManager.endScan();
    }
}
