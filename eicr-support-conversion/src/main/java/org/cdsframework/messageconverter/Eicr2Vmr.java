package org.cdsframework.messageconverter;

import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants;
import org.cdsframework.messageconverter.eicr.convert.utils.SystemUserLookup;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrEncounter2Vmr;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrHealthCareFacilityAddress2Vmr;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrMedication2Vmr;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrProblem2Vmr;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrRecordTarget2Vmr;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrResult2Vmr;
import org.cdsframework.messageconverter.enumeration.EicrSectionType;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSContext;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.CDSINPUT_TEMPLATE_ID;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrPlanOfCare2Vmr;
import org.cdsframework.messageconverter.eicr.convert.vmr.EicrSocialHistory2Vmr;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
public class Eicr2Vmr {

    private final static LogUtils logger = LogUtils.getLogger(Eicr2Vmr.class);

    public static ClinicalDocument load(byte[] payload) throws EicrException {
        try {
            return CDAUtil.load(new ByteArrayInputStream(payload));

        } catch (Exception e) {
            throw new EicrException("Error converting payload of type byte[] to ClinicalDocument", e);

        }
    }

    public static ClinicalDocument load(InputStream payload) throws EicrException {
        try {
            return CDAUtil.load(payload);

        } catch (Exception e) {
            throw new EicrException("Error converting payload of type InputStream to ClinicalDocument", e);

        }
    }

    /**
     * Convert a clinical document to a CDS input instance.
     *
     * @param clinicalDocument
     * @return
     */
    public static CDSInput getCdsInputFromClinicalDocument(ClinicalDocument clinicalDocument) {
        CDSInput cdsInput = CdsInputWrapper.getCdsInputWrapper().getCdsObject();
        getCdsInputFromClinicalDocument(clinicalDocument, cdsInput);
        return cdsInput;
    }

    /**
     * Convert a clinical document to a CDS input instance.
     *
     * @param clinicalDocument
     * @param cdsInput
     */
    public static void getCdsInputFromClinicalDocument(ClinicalDocument clinicalDocument, CDSInput cdsInput) {

        final String METHODNAME = "getCdsInputFromClinicalDocument ";
        logger.info("starting getCdsInputFromClinicalDocument()...");
        try {

            TemplateIdSetter.setTemplateId(cdsInput.getTemplateId(), CDSINPUT_TEMPLATE_ID);

            CdsInputWrapper input = CdsInputWrapper.getCdsInputWrapper(cdsInput);

            cdsInput.getCdsContext().setCdsInformationRecipientPreferredLanguage(null);

            setSystemUserType(cdsInput.getCdsContext(), clinicalDocument);

            // patient demographics processing
            EicrRecordTarget2Vmr.addRecordTarget(clinicalDocument.getRecordTargets(), input);
            EicrHealthCareFacilityAddress2Vmr.addHealthCareFacilityAddress2Vmr(clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility(), input);

            for (Section section : clinicalDocument.getAllSections()) {
                EicrSectionType cdaSectionType = EicrSectionType.getConsolCdaSectionTypeFromCode(section.getCode().getCode());
                switch (cdaSectionType) {
                    case ENCOUNTERS:
                        logger.debug("processing ENCOUNTERS section...");
                        EicrEncounter2Vmr.addEncounters(section.getEntries(), input);
                        break;
                    case MEDICATIONS_ADMINISTERED:
                        logger.debug("processing MEDICATIONS_ADMINISTERED section...");
                        EicrMedication2Vmr.addMedications(section.getEntries(), input);
                        break;
                    case PROBLEM:
                        logger.debug("processing PROBLEM section...");
                        EicrProblem2Vmr.addProblems(section.getEntries(), input);
                        break;
                    case PLAN_OF_CARE:
                        logger.debug("processing PLAN_OF_CARE section...");
                        EicrPlanOfCare2Vmr.addPlansOfCare(section.getEntries(), input);
                        break;
                    case RESULTS:
                        logger.debug("processing RESULTS section...");
                        EicrResult2Vmr.addResults(section.getEntries(), input);
                        break;
                case SOCIAL_HISTORY:
                    logger.debug("processing SOCIAL_HISTORY section...");
                    EicrSocialHistory2Vmr.addSocialHistory(section.getEntries(), input);
                    break;
                    default:
                        break;
                }
            }

            TemplateIdSetter.setTemplateId(cdsInput.getVmrInput().getTemplateId(), CdaConstants.VMR_TEMPLATE_ID);

            logger.info("ending getCdsInputFromClinicalDocument()...");
        } catch (EicrException e) {
            logger.error(e);
        }
    }

    private static void setSystemUserType(CDSContext cdsContext, ClinicalDocument clinicalDocument) {

        final String _METHODNAME = "setSystemUserType(): ";
        CD systemUserType = new CD();
        systemUserType.setCodeSystem(CdaConstants.SYSTEMUSERTYPE_CODESYSTEM);

        if (clinicalDocument != null && clinicalDocument.getComponentOf() != null
                && clinicalDocument.getComponentOf().getEncompassingEncounter() != null
                && clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation() != null
                && clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility() != null
                && clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility().getCode() != null) {

            String lVmrSystemUserCode = SystemUserLookup.getVmrSystemUserType(clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility().getCode().getCode());
            if (lVmrSystemUserCode == null || lVmrSystemUserCode.isEmpty()) {
                logger.warn(_METHODNAME + "healthcare facility code provided by eICR not mapped; defaulting to PROVIDER_FACILITY");
                lVmrSystemUserCode = CdaConstants.PROVIDER_FACILITY;
            }
            systemUserType.setCode(lVmrSystemUserCode);
        } else {
            logger.warn(_METHODNAME + "healthcare facility code not provided in eICR; defaulting to PROVIDER_FACILITY");
            systemUserType.setCode(CdaConstants.PROVIDER_FACILITY);
        }

        cdsContext.setCdsSystemUserType(systemUserType);
    }
}
