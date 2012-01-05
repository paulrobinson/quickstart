/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
package org.jboss.as.quickstarts.wsba.participantcompletion.simple;

import com.arjuna.wst.BusinessAgreementWithParticipantCompletionParticipant;
import com.arjuna.wst.FaultedException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.WrongStateException;
import com.arjuna.wst11.ConfirmCompletedParticipant;

import java.io.Serializable;

/**
 * An adapter class that exposes the SetManager as a WS-BA participant using the
 * 'Participant Completion' protocol.
 * 
 * The Set Service only allows a single item to be added to the set in any given
 * transaction. So, this means it can complete at the end of the addValueToSet
 * call, rather than having to wait for the coordinator to tell it to do so.
 * Hence it uses a participant which implements the 'participant completion'
 * protocol.
 * 
 * @author Paul Robinson (paul.robinson@redhat.com)
 */
public class SetParticipantBA
		implements
			BusinessAgreementWithParticipantCompletionParticipant,
			ConfirmCompletedParticipant,
			Serializable {

	private static final long serialVersionUID = 1L;

	private String value;

	/**
	 * Participant instances are related to business method calls in a one to
	 * one manner.
	 * 
	 * @param value
	 *            the value to remove from the set during compensation
	 */
	public SetParticipantBA(String value) {
		this.value = value;
	}

	/**
	 * The transaction has completed successfully. The participant previously
	 * informed the coordinator that it was ready to complete.
	 * 
	 * @throws WrongStateException
	 *             never in this implementation.
	 * @throws SystemException
	 *             never in this implementation.
	 */

	public void close() throws WrongStateException, SystemException {
		// nothing to do here as the item has already been added to the set
		System.out.println("SetParticipantBA.close");
	}

	/**
	 * The transaction has canceled, and the participant should undo any work.
	 * The participant cannot have informed the coordinator that it has
	 * completed.
	 * 
	 * @throws WrongStateException
	 *             never in this implementation.
	 * @throws SystemException
	 *             never in this implementation.
	 */

	public void cancel() throws WrongStateException, SystemException {
		System.out.println("SetParticipantBA.cancel");

		// Compensate work
		MockSetManager.rollback(value);
	}

	/**
	 * The transaction has cancelled. The participant previously informed the
	 * coordinator that it had finished work but could compensate later if
	 * required, and it is now requested to do so.
	 * 
	 * @throws WrongStateException
	 *             never in this implementation.
	 * @throws SystemException
	 *             if unable to perform the compensating transaction.
	 */

	public void compensate() throws FaultedException, WrongStateException,
			SystemException {
		System.out.println("SetParticipantBA.compensate");

		// Compensate work done by the service
		MockSetManager.rollback(value);
	}

	public String status() {
		return null;
	}

	public void unknown() throws SystemException {
	}

	public void error() throws SystemException {
		System.out.println("SetParticipantBA.error");

		// Compensate work done by the service
		MockSetManager.rollback(value);
	}

	/**
	 * method called to perform commit or rollback of prepared changes to the
	 * underlying manager state after the participant recovery record has been
	 * written
	 * 
	 * @param confirmed
	 *            true if the log record has been written and changes should be
	 *            rolled forward and false if it has not been written and
	 *            changes should be rolled back
	 */
	public void confirmCompleted(boolean confirmed) {
		System.out.println("SetParticipantBA.confirmCompleted('" + confirmed + "')");
		if (confirmed) {
			MockSetManager.commit();
		} else {
			MockSetManager.rollback(value);
		}
	}
}
