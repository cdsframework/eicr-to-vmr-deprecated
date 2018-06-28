package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.ProblemTypeCodeLookup;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.Problem;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.hl7.datatypes.ANY;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;

import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.PROBLEM_STATUS_CODE_SYSTEM;

/**
 * @author HLN Consulting, LLC
 */
public class EicrAct2Vmr {

    private static final Logger logger = Logger.getLogger(EicrAct2Vmr.class);

    /**
     * Adds the supplied act.
     *
     * @param idRoot
     * @param act
     * @param relatedClinicalStatements
     * @return
     * @throws EicrException
     */
    public static RelatedClinicalStatement addActAsRelatedClinicalStatement(
            String idRoot,
            Act act,
            List<RelatedClinicalStatement> relatedClinicalStatements)
            throws EicrException {

        logger.debug("starting addActAsRelatedClinicalStatement()...");

        RelatedClinicalStatement result = null;

        if (act != null) {

            String[] idElements = IdentifierUtils.getIdElements(act.getIds(), idRoot);

            ObservationResult observationResult = CdsObjectFactory.getObservationResult(idElements[1], idElements[0]);

            EicrEntryRelationship2Vmr.addEntryRelationships(
                    idRoot,
                    act.getEntryRelationships(),
                    relatedClinicalStatements,
                    observationResult.getRelatedEntity());

        } else {
            logger.error("act is null!");

        }

        return result;
    }

    /**
     * Adds the supplied act as a problem to the CdsInput instance.
     *
     * @param act
     * @param input
     * @return
     * @throws EicrException
     */
    public static Problem addActAsProblem(
            Act act,
            CdsInputWrapper input)
            throws EicrException {

        logger.debug("starting addActAsProblem()...");

        Problem result = null;

        if (act != null) {

            CD status = CodeUtils.getStatusCode(act.getStatusCode());

            for (EntryRelationship entryRelationship : act.getEntryRelationships()) {

                if (entryRelationship.getObservation()!=null) {

                    String[] obsIdElements = IdentifierUtils.getIdElements(entryRelationship.getObservation().getIds());
                    String[] obsEventTimeElements = IntervalUtils.getEventTimeElements(entryRelationship.getObservation().getEffectiveTime());

                    for (ANY value : entryRelationship.getObservation().getValues()) {

                        if (value instanceof CD) {

                            CD cd = (CD) value;

                            try {
                                result = input.addProblem(
                                        cd.getCode(),
                                        cd.getDisplayName(),
                                        cd.getCodeSystem(),
                                        cd.getCodeSystemName(),
                                        status.getCode(),
                                        status.getDisplayName(),
                                        PROBLEM_STATUS_CODE_SYSTEM,
                                        status.getCodeSystemName(),
                                        obsEventTimeElements[0],
                                        obsEventTimeElements[1],
                                        obsIdElements[0],
                                        obsIdElements[1]);

                                TemplateIdSetter.setTemplateId(result.getTemplateId(), ProblemTypeCodeLookup.getOID(entryRelationship.getObservation().getCode().getCode()));

                            } catch (CdsException e) {
                                throw new EicrException("Error adding 'Problem' to CdsInput message", e);

                            }
                        } else {
                            logger.info("Found observation.value of type=" + value.getClass());

                        }
                    }
                }
            }

        } else {
            logger.debug("act is null!");
        }

        return result;

    }
}
