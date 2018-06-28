package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.openhealthtools.mdht.uml.cda.Entry;

import java.util.List;
import org.cdsframework.util.LogUtils;

/**
 * @author HLN Consulting, LLC
 */
public class EicrResult2Vmr {

    private final static LogUtils logger = LogUtils.getLogger(EicrResult2Vmr.class);

    /**
     * Adds EICR Result entries as vMR ObservationResults.
     *
     * @param entries
     * @param input
     * @throws EicrException
     */
    public static void addResults(List<Entry> entries, CdsInputWrapper input) throws EicrException {

        logger.debug("starting addResults()..");

        for (Entry entry : entries) {

            try {
                if (input.getCdsObject().getVmrInput() != null && input.getCdsObject().getVmrInput().getPatient() != null) {

                    EvaluatedPerson.ClinicalStatements clinicalStatements = input.getCdsObject().getVmrInput().getPatient().getClinicalStatements();

                    if (clinicalStatements == null) {
                        clinicalStatements = new EvaluatedPerson.ClinicalStatements();
                        input.getCdsObject().getVmrInput().getPatient().setClinicalStatements(clinicalStatements);

                    }

                    EicrOrganizer2Vmr.addOrganizerAsObservationOrder(entry.getOrganizer(), clinicalStatements);

                }
            } catch (Exception e) {
                logger.error(e);
            }

        }
    }
}
