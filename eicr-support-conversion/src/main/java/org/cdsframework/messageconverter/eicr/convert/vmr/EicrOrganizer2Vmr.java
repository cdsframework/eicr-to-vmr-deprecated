/**
 * Copyright (c) 2014, The Board of Trustees of the University of Illinois
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.ObservationOrder;
import org.openhealthtools.mdht.uml.cda.Organizer;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;

/**
 * @author HLN Consulting, LLC
 */
public class EicrOrganizer2Vmr {

    private final static Logger logger = Logger.getLogger(EicrOrganizer2Vmr.class);

    /**
     * Adds the supplied Organizer.
     *
     * @param organizer
     * @param clinicalStatements
     * @throws CdsException
     */
    public static void addOrganizerAsObservationOrder(
            Organizer organizer,
            EvaluatedPerson.ClinicalStatements clinicalStatements)
            throws EicrException {

        logger.debug("starting addOrganizerAsObservationOrder()...");

        if (organizer != null) {
            EvaluatedPerson.ClinicalStatements.ObservationOrders observationOrders = clinicalStatements.getObservationOrders();

            if (observationOrders == null) {
                observationOrders = new EvaluatedPerson.ClinicalStatements.ObservationOrders();
                clinicalStatements.setObservationOrders(observationOrders);

            }

            ObservationOrder observationOrder = createObservationOrder(organizer);

            observationOrders.getObservationOrder().add(observationOrder);

        } else {
            logger.debug("organizer is null!");

        }
    }

    private static ObservationOrder createObservationOrder(Organizer organizer) throws EicrException {

        String[] idElements = IdentifierUtils.getIdElements(organizer.getIds());
        ObservationOrder observationOrder = CdsObjectFactory.getObservationOrder(idElements[1], idElements[0]);

        CD code = CodeUtils.getCode(organizer.getCode());

        II ii = new II();
        ii.setRoot(idElements[0]);
        ii.setExtension(idElements[1]);

        observationOrder.setId(ii);
        observationOrder.setObservationFocus(CdsInputWrapperWrapper.getCD(
                code.getCode(),
                code.getCodeSystem(),
                code.getDisplayName(),
                code.getCodeSystemName()));

        if (organizer.getEffectiveTime() != null) {
            observationOrder.setOrderEventTime(IntervalUtils.convertTime(organizer.getEffectiveTime()));

        }

        EicrObservation2Vmr.addObservationsAsRelatedClinicalStatements(organizer, observationOrder);

        return observationOrder;

    }
}
