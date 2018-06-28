package org.cdsframework.messageconverter.eicr.convert.vmr;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.IVLPQ;
import org.opencds.vmr.v1_0.schema.ObservationOrder;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.openhealthtools.mdht.uml.cda.Component4;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.Organizer;
import org.openhealthtools.mdht.uml.cda.ReferenceRange;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.CO;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_PQ;
import org.openhealthtools.mdht.uml.hl7.datatypes.PQ;
import org.openhealthtools.mdht.uml.hl7.datatypes.impl.STImpl;

/**
 * @author HLN Consulting, LLC
 */
public class EicrObservation2Vmr {

    private static final Logger logger = Logger.getLogger(EicrObservation2Vmr.class);

    /**
     * Adds an observation.
     *
     * @param organizer
     * @param observationOrder
     * @throws EicrException
     */
    public static void addObservationsAsRelatedClinicalStatements(Organizer organizer, ObservationOrder observationOrder) throws EicrException {

        logger.debug("starting addObservationsAsRelatedClinicalStatements()...");

        List<RelatedClinicalStatement> relatedClinicalStatements = observationOrder.getRelatedClinicalStatement();

        for (Component4 component : organizer.getComponents()) {

            if (component.getObservation() == null) {
                continue;

            }

            Observation observation = component.getObservation();

            RelatedClinicalStatement rcs = CdsObjectFactory.getRelatedClinicalStatement("PERT");
            relatedClinicalStatements.add(rcs);

            ObservationResult observationResult = createObservationResult(observation);

            rcs.setObservationResult(observationResult);

        }
    }

    private static ObservationResult createObservationResult(Observation observation) {
        String[] idElements = IdentifierUtils.getIdElements(observation.getIds());

        CD code = CodeUtils.getCode(observation.getCode());
        CD status = CodeUtils.getStatusCode(observation.getStatusCode());

        ObservationResult observationResult = CdsObjectFactory.getObservationResult(idElements[1], idElements[0]);

        createObservationValues(observation, observationResult);

        createInterpretations(observation, observationResult);

        if (!IntervalUtils.isEmpty(observation.getEffectiveTime())) {
            observationResult.setObservationEventTime(IntervalUtils.convertTime(observation.getEffectiveTime()));

        }

        observationResult.setObservationFocus(
                CdsInputWrapperWrapper.getCD(
                        code.getCode(),
                        code.getCodeSystem(),
                        code.getDisplayName(),
                        code.getCodeSystemName()));

        TemplateIdSetter.setTemplateId(observationResult.getTemplateId(), CdaConstants.LAB_RESULTS_TEMPLATE_ID);

        return observationResult;
    }

    /**
     * Adds the supplied Organizer.
     *
     * @param observation
     * @param input
     * @throws EicrException
     */
    public static void addObservationAsObservationResult(
            Observation observation,
            CdsInputWrapper input)
            throws EicrException {

        logger.debug("starting addObservationAsObservationResult()...");

        if (observation != null) {

            String[] idElements = IdentifierUtils.getIdElements(observation.getIds());

            CD code = CodeUtils.getCode(observation.getCode());
            CD status = CodeUtils.getStatusCode(observation.getStatusCode());

            ObservationResult observationResult = CdsObjectFactory.getObservationResult(idElements[1], idElements[0]);

            try {
                input.addObservationResult(observationResult);

            } catch (CdsException e) {
                throw new EicrException("Error adding 'ObservationResult' to CdsInput message", e);

            }

            createObservationValues(observation, observationResult);

            createInterpretations(observation, observationResult);

            if (!IntervalUtils.isEmpty(observation.getEffectiveTime())) {
                observationResult.setObservationEventTime(IntervalUtils.convertTime(observation.getEffectiveTime()));

            }

            observationResult.setObservationFocus(
                    CdsInputWrapperWrapper.getCD(
                            code.getCode(),
                            code.getCodeSystem(),
                            code.getDisplayName(),
                            code.getCodeSystemName()));

//            Subject subject = organizer.getSubject();
//            if (subject != null) {
//                RelatedSubject relatedSubject = subject.getRelatedSubject();
//                if (relatedSubject != null) {
//                    RelatedEntity RelatedEntity = new RelatedEntity();
//                    observationResult.getRelatedEntity().add(RelatedEntity);
//                    if (relatedSubject.getClassCode() == x_DocumentSubject.PRS) {
//                        CD relation = relatedSubject.getCode();
//                        RelatedEntity.setTargetRole(CdsInputWrapperWrapper.getCD(
//                                relation.getCode(),
//                                relation.getCodeSystem(),
//                                relation.getDisplayName(),
//                                relation.getCodeSystemName()));
//                        RelatedEntity.Person person = new RelatedEntity.Person();
//                        RelatedEntity.setPerson(person);
//                    }
//                }
//            }
        } else {
            logger.debug("observation is null!");
        }
    }

    private static void createObservationValueRefRange(Observation observation, ObservationResult observationResult) {
        for (ReferenceRange referenceRange : observation.getReferenceRanges()) {
            Object value = referenceRange.getObservationRange().getValue();

            if (value instanceof IVL_PQ) {
                IVL_PQ ivl_pq = (IVL_PQ) value;

                ObservationResult.ObservationValue observationValue = observationResult.getObservationValue();
                if (observationValue == null) {
                    observationValue = new ObservationResult.ObservationValue();

                }

                IVLPQ ivlpq = observationValue.getPhysicalQuantityRange();
                if (ivlpq == null) {
                    ivlpq = new IVLPQ();

                }

                ivlpq.setLowValue(ivl_pq.getLow().getValue().doubleValue());
                ivlpq.setLowUnit(ivl_pq.getLow().getUnit());
                ivlpq.setHighValue(ivl_pq.getHigh().getValue().doubleValue());
                ivlpq.setHighUnit(ivl_pq.getHigh().getUnit());

                observationValue.setPhysicalQuantityRange(ivlpq);

//                ST st = new ST();
//                st.setValue(observation.getText().getCDATA());
//                observationValue.setText(st);
            } else if (value instanceof CO) {
                ObservationResult.ObservationValue observationValue = observationResult.getObservationValue();
                if (observationValue == null) {
                    observationValue = new ObservationResult.ObservationValue();

                }

                observationValue.setConcept(CdsInputWrapperWrapper.getCD(
                        ((CO) value).getCode(),
                        ((CO) value).getCodeSystem(),
                        ((CO) value).getDisplayName(),
                        ((CO) value).getCodeSystemName()));

            }
        }
    }

    private static void createObservationValues(Observation observation, ObservationResult observationResult) {
        for (org.openhealthtools.mdht.uml.hl7.datatypes.ANY any : observation.getValues()) {

            ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();

            // TODO This is a fix for RCK-678 until a better solution comes about
            if (any instanceof STImpl && "positive".equalsIgnoreCase(((STImpl) any).getText())) {
                observationValue.setConcept(CdsInputWrapperWrapper.getCD(
                        "10828004",
                        "2.16.840.1.113883.6.96",
                        "Positive",
                        "SNOMED-CT"));

                observationResult.setObservationValue(observationValue);

            } else if (any instanceof CD) {
                observationValue.setConcept(CdsInputWrapperWrapper.getCD(
                        ((CD) any).getCode(),
                        ((CD) any).getCodeSystem(),
                        ((CD) any).getDisplayName(),
                        ((CD) any).getCodeSystemName()));

                observationResult.setObservationValue(observationValue);

            } else if (any instanceof CO) {
                observationValue.setConcept(CdsInputWrapperWrapper.getCD(
                        ((CO) any).getCode(),
                        ((CO) any).getCodeSystem(),
                        ((CO) any).getDisplayName(),
                        ((CO) any).getCodeSystemName()));

                observationResult.setObservationValue(observationValue);

            } else if (any instanceof IVL_PQ) {

                IVLPQ ivlpq = observationValue.getPhysicalQuantityRange();
                if (ivlpq == null) {
                    ivlpq = new IVLPQ();

                }

                ivlpq.setLowValue(((IVL_PQ) any).getLow().getValue().doubleValue());
                ivlpq.setLowUnit(((IVL_PQ) any).getLow().getUnit());
                ivlpq.setHighValue(((IVL_PQ) any).getHigh().getValue().doubleValue());
                ivlpq.setHighUnit(((IVL_PQ) any).getHigh().getUnit());

                observationValue.setPhysicalQuantityRange(ivlpq);

                observationResult.setObservationValue(observationValue);

            } else if (any instanceof PQ) {
                observationValue.setPhysicalQuantity(CdsObjectFactory.getPQ(
                        ((PQ) any).getValue().toPlainString(),
                        ((PQ) any).getUnit()));

                observationResult.setObservationValue(observationValue);

            } else {
                logger.info("New type of ObservationValue: " + any.getClass());

            }
        }
    }

    private static void createInterpretations(Observation observation, ObservationResult observationResult) {
        if (observation.getInterpretationCodes().size() > 0) {

            for (CE ce : observation.getInterpretationCodes()) {

                org.opencds.vmr.v1_0.schema.CD cd = CdsInputWrapperWrapper.getCD(
                        ce.getCode(),
                        ce.getCodeSystem(),
                        ce.getCodeSystemName(),
                        ce.getDisplayName());

                if (StringUtils.isBlank(cd.getCode())) {
                    cd.setCode("999");

                    logger.error("Code is blank for");
                }

                if (StringUtils.isBlank(cd.getCodeSystem())) {
                    cd.setCodeSystem("1.1.1.1");

                }

                observationResult.getInterpretation().add(cd);

            }
        }
    }

    /**
     * Adds the supplied Observation.
     *
     * @param observation
     * @param input
     * @throws
     * org.cdsframework.messageconverter.eicr.convert.exception.EicrException
     */
    public static void addObservationAsObservationOrder(
            Observation observation,
            CdsInputWrapper input)
            throws EicrException {

        logger.debug("starting addObservationAsObservationOrder()...");

        if (input != null) {
            EvaluatedPerson.ClinicalStatements clinicalStatements = input.getInstanceClinicalStatements();

            if (observation != null) {
                EvaluatedPerson.ClinicalStatements.ObservationOrders observationOrders = clinicalStatements.getObservationOrders();

                if (observationOrders == null) {
                    observationOrders = new EvaluatedPerson.ClinicalStatements.ObservationOrders();
                    clinicalStatements.setObservationOrders(observationOrders);

                }

                ObservationOrder observationOrder = createObservationOrder(observation);

                observationOrders.getObservationOrder().add(observationOrder);

            } else {
                logger.debug("observation is null!");
            }
        } else {
            logger.error("input is null!");
        }
    }

    private static ObservationOrder createObservationOrder(Observation observation) throws EicrException {

        String[] idElements = IdentifierUtils.getIdElements(observation.getIds());

        ObservationOrder observationOrder = CdsObjectFactory.getObservationOrder(idElements[1], idElements[0]);

        CD code = CodeUtils.getCode(observation.getCode());

        observationOrder.setObservationFocus(CdsInputWrapperWrapper.getCD(
                code.getCode(),
                code.getCodeSystem(),
                code.getDisplayName(),
                code.getCodeSystemName()));

        if (observation.getEffectiveTime() != null) {
            observationOrder.setOrderEventTime(IntervalUtils.convertTime(observation.getEffectiveTime()));
        }

        return observationOrder;

    }
}
