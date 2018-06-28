/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.util.LogUtils;
import org.eclipse.emf.common.util.EList;
import org.openhealthtools.mdht.uml.cda.Entry;

/**
 *
 * @author sdn
 */
public class EicrPlanOfCare2Vmr {

    private static final LogUtils logger = LogUtils.getLogger(EicrPlanOfCare2Vmr.class);

    public static void addPlansOfCare(EList<Entry> entries, CdsInputWrapper input) throws EicrException {

        logger.debug("starting addPlansOfCare()...");

        for (Entry entry : entries) {
            try {
                EicrObservation2Vmr.addObservationAsObservationOrder(entry.getObservation(), input);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }
}
