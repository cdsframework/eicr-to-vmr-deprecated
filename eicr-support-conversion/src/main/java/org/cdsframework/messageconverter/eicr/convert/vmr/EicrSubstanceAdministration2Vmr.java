package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.opencds.vmr.v1_0.schema.IVLPQ;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.PQ;

import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.SUBSTANCE_ADMIN_TEMPLATE_ID;

/**
 * @author HLN Consulting, LLC
 */
public class EicrSubstanceAdministration2Vmr {

    private final static Logger logger = Logger.getLogger(EicrSubstanceAdministration2Vmr.class);

    /**
     * Adds substance administration.
     *
     * @param substanceAdministration
     * @param input
     * @return
     * @throws EicrException
     */
    public static SubstanceAdministrationEvent addSubstanceAdministrationAsSubstanceAdministrationEvent(
            SubstanceAdministration substanceAdministration,
            CdsInputWrapper input)
            throws EicrException {

        // TODO: add as undelivered event if negationind = true ???

        SubstanceAdministrationEvent result = null;

        if (substanceAdministration != null) {

            String[] idElements = IdentifierUtils.getIdElements(substanceAdministration.getIds());

            String[] eventTimeElements = IntervalUtils.getEventTimeElements(IntervalUtils.getTsSingleValue(substanceAdministration.getEffectiveTimes()));

            try {
                result = input.addSubstanceAdministrationEvent(
                        eventTimeElements[0],
                        eventTimeElements[1],
                        idElements[0],
                        idElements[1]);

                TemplateIdSetter.setTemplateId(result.getTemplateId(), SUBSTANCE_ADMIN_TEMPLATE_ID);

            } catch (CdsException e) {
                throw new EicrException("Error setting 'SubstanceAdministrationEvent' on CdsInput message", e);

            }

            // substance
            EicrConsumable2Vmr.addConsumableToSubstanceAdministrationEvent(substanceAdministration.getConsumable(), result);

            // status
            CD status = CodeUtils.getStatusCode(substanceAdministration.getStatusCode());
            if (status != null) {
                result.setSubstanceAdministrationGeneralPurpose(CdsInputWrapperWrapper.getCD(
                        status.getCode(),
                        status.getCodeSystem(),
                        status.getDisplayName(),
                        status.getCodeSystemName()));

                TemplateIdSetter.setTemplateId(result.getTemplateId(), CdaConstants.SUBSTANCE_ADMIN_GENERALPURPOSE_TEMPLATE_ID);

            }

            // dose quantity
            PQ doseQuantity = null;
            try {
                doseQuantity = substanceAdministration.getDoseQuantity();

            } catch (Exception e) {
                logger.error("An error occurred aquiring doseQuantity - ignoring!", e);

            }

            if (doseQuantity != null && doseQuantity.getUnit() != null && doseQuantity.getValue() != null) {
                IVLPQ vmrPq = new IVLPQ();
                vmrPq.setHighUnit(doseQuantity.getUnit());
                vmrPq.setHighValue(doseQuantity.getValue().doubleValue());
                vmrPq.setLowValue(vmrPq.getHighValue());
                vmrPq.setLowUnit(doseQuantity.getUnit());

                result.setDoseQuantity(vmrPq);

            } else {
                logger.warn("doseQuantity: " + doseQuantity);
                if (doseQuantity != null) {
                    logger.warn("doseQuantity.getUnit(): " + doseQuantity.getUnit());
                    logger.warn("doseQuantity.getValue(): " + doseQuantity.getValue());
                }
            }

            // delivery route
            CD routeCode = substanceAdministration.getRouteCode();
            if (routeCode != null) {
                result.setDeliveryRoute(CdsInputWrapperWrapper.getCD(
                        routeCode.getCode(),
                        routeCode.getCodeSystem(),
                        routeCode.getDisplayName(),
                        routeCode.getCodeSystemName()));
            }

            EicrPerformer2Vmr.addPerformers(
                    idElements[0],
                    substanceAdministration.getPerformers(),
                    result.getRelatedEntity());

            EicrParticipant2Vmr.addParticipants(
                    idElements[0],
                    substanceAdministration.getParticipants(),
                    result.getRelatedEntity());

            EicrEntryRelationship2Vmr.addEntryRelationships(
                    idElements[0],
                    substanceAdministration.getEntryRelationships(),
                    result.getRelatedClinicalStatement(),
                    result.getRelatedEntity());

        } else {
            logger.error("substanceAdministration is null!");
        }
        return result;
    }

}
