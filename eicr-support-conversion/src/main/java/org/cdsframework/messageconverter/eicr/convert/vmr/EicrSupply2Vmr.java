package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.RelatedEntity;
import org.openhealthtools.mdht.uml.cda.Supply;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.PQ;

import java.util.ArrayList;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 * @author HLN Consulting, LLC
 */
public class EicrSupply2Vmr {

    private static final Logger logger = Logger.getLogger(EicrSupply2Vmr.class);

    /**
     * Add a supply event as a related clinical statement.
     *
     * @param idRoot
     * @param supply
     * @param relatedClinicalStatements
     * @param relatedEntities
     * @return
     * @throws EicrException
     */
    public static RelatedClinicalStatement addSupplyAsRelatedClinicalStatement(
            String idRoot,
            Supply supply,
            List<RelatedClinicalStatement> relatedClinicalStatements,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("startingaddSupplyAsRelatedClinicalStatement()...");

        RelatedClinicalStatement result = null;

        if (supply != null) {

            String[] idElements = IdentifierUtils.getIdElements(supply.getIds(), idRoot);

            result = CdsObjectFactory.getRelatedClinicalStatement("PERT");
            relatedClinicalStatements.add(result);
            ObservationResult observationResult = CdsObjectFactory.getObservationResult(idElements[1], idElements[0]);
            result.setObservationResult(observationResult);

            // supply date
            String[] eventTimeElements = IntervalUtils.getEventTimeElements(IntervalUtils.getTsSingleValue(supply.getEffectiveTimes()));
            observationResult.setObservationEventTime(CdsObjectFactory.getIVLTS(eventTimeElements[0], eventTimeElements[1]));

            // supply status
            CD status = CodeUtils.getStatusCode(supply.getStatusCode());
            observationResult.getInterpretation().add(CdsInputWrapperWrapper.getCD(
                    status.getCode(),
                    status.getCodeSystem(),
                    status.getDisplayName(),
                    status.getCodeSystemName()));

            // supply quantity
            PQ quantity = supply.getQuantity();
            if (quantity != null) {
                org.opencds.vmr.v1_0.schema.PQ vmrPq = new org.opencds.vmr.v1_0.schema.PQ();
                vmrPq.setUnit(quantity.getUnit());
                vmrPq.setValue(quantity.getValue().doubleValue());

                ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
                observationValue.setPhysicalQuantity(vmrPq);

                observationResult.setObservationValue(observationValue);
            }

            // supply code
            CD code;
            List<CD> translations;
            if (supply.getProduct() != null
                    && supply.getProduct().getManufacturedProduct() != null
                    && supply.getProduct().getManufacturedProduct().getManufacturedMaterial() != null
                    && supply.getProduct().getManufacturedProduct().getManufacturedMaterial().getCode() != null) {
                translations = supply.getProduct().getManufacturedProduct().getManufacturedMaterial().getCode().getTranslations();
                code = CodeUtils.getCode(supply.getProduct().getManufacturedProduct().getManufacturedMaterial().getCode());
            } else {
                translations = new ArrayList<CD>();
                code = DatatypesFactory.eINSTANCE.createCD();
            }
            observationResult.setObservationFocus(
                    CdsInputWrapperWrapper.getCD(
                            status.getCode(),
                            status.getCodeSystem(),
                            status.getDisplayName(),
                            status.getCodeSystemName()));

            // supply code translations
            for (CD cd : translations) {
                observationResult.getInterpretation().add(
                        CdsInputWrapperWrapper.getCD(
                                cd.getCode(),
                                cd.getCodeSystem(),
                                cd.getDisplayName(),
                                cd.getCodeSystemName()));
            }

            // add any child level entry relationships
            EicrEntryRelationship2Vmr.addEntryRelationships(
                    idElements[0],
                    supply.getEntryRelationships(),
                    relatedClinicalStatements,
                    observationResult.getRelatedEntity());

        } else {
            logger.error("observation is null!");
        }

        return result;
    }

}
