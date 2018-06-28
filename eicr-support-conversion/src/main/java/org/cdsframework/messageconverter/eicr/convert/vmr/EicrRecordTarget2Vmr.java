package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.openhealthtools.mdht.uml.cda.Patient;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.RecordTarget;
import org.openhealthtools.mdht.uml.hl7.datatypes.ENXP;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;

import java.util.List;

/**
 * @author HLN Consulting, LLC
 */
public class EicrRecordTarget2Vmr {

    private static final Logger logger = Logger.getLogger(EicrRecordTarget2Vmr.class);

    /**
     * Adds a record target to a CDS input instance.
     *
     * @param recordTargets
     * @param input
     * @throws EicrException
     */
    public static void addRecordTarget(List<RecordTarget> recordTargets, CdsInputWrapper input) throws EicrException {

        logger.debug("starting addRecordTarget()...");

        if (recordTargets.size() == 1) {
            RecordTarget recordTarget = recordTargets.get(0);
            PatientRole patientRole = recordTarget.getPatientRole();
            Patient patient = patientRole.getPatient();

            // patient ID
            if (patientRole.getIds().size() > 0) {
                try {
                    String [] idElement = IdentifierUtils.getIdElements(patientRole.getIds());
                    input.setPatientId(idElement[1], idElement[0]);

                } catch (CdsException e) {
                    throw new EicrException("Error adding 'PatientId' to CdsInput message", e);

                }

                if (patientRole.getIds().size() > 1) {
                    logger.warn("There is more than one patientRole IDs in this CDA! Using first one.");
                }
            } else {
                throw new EicrException("There are no patientRole IDs in this CDA!");
            }

            // set addresses at patientRole level
            ConversionHelper.copyFromToPatientAddresses(patientRole, input.getCdsObject().getVmrInput().getPatient());
            ConversionHelper.copyFromToTelecom(patientRole, input.getCdsObject().getVmrInput().getPatient());

            try {
                // set patient name - not working...
                if (false && patient != null && patient.getNames() != null) {
                    if (!patient.getNames().isEmpty()) {
                        PN patientName = patient.getNames().get(0);
                        if (patientName != null) {
                            String givenNameValue = null;
                            String familyNameValue = null;
                            List<ENXP> givens = patientName.getGivens();
                            if (givens != null && !givens.isEmpty()) {
                                ENXP givenName = givens.get(0);
                                if (givenName != null) {
                                    givenNameValue = givenName.getText();
                                }
                            }
                            List<ENXP> families = patientName.getFamilies();
                            if (families != null && !families.isEmpty()) {
                                ENXP familyName = families.get(0);
                                if (familyName != null) {
                                    familyNameValue = familyName.getText();
                                }
                            }
                            input.setPatientName(givenNameValue, familyNameValue);
                        }

                    }
                }
                // set gender
                if (patient != null && patient.getAdministrativeGenderCode() != null) {
                    input.setPatientGender(patient.getAdministrativeGenderCode().getCode(), CdaConstants.GENDER_CODE_SYSTEM);
                }

                // set DOB
                if (patient != null && patient.getBirthTime() != null) {
                    input.setPatientBirthTime(patient.getBirthTime().getValue());
                }

                TemplateIdSetter.setTemplateId(input.getCdsObject().getVmrInput().getPatient().getTemplateId(), CdaConstants.PATIENT_DETAIL_TEMPLATE_ID);

            } catch (CdsException e) {
                throw new EicrException("Error setting demographic information in CdsInput message");

            }
        } else if (recordTargets.isEmpty()) {
            logger.error("There are no recordTargets in this CDA!");
        } else {
            logger.error("There are mulitple recordTargets in this CDA!");
        }
    }
}
