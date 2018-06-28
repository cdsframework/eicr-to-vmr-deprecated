package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.opencds.vmr.v1_0.schema.BodySite;
import org.opencds.vmr.v1_0.schema.ProcedureEvent;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.openhealthtools.mdht.uml.cda.Procedure;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;

import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 * @author HLN Consulting, LLC
 */
public class EicrProcedure2Vmr {

    private static final Logger logger = Logger.getLogger(EicrProcedure2Vmr.class);

    /**
     * Add a procedure as a related clinical statement with a procedure event on it.
     * 
     * @param idRoot
     * @param procedure
     * @param relatedClinicalStatements
     * @return
     * @throws EicrException
     */
    public static RelatedClinicalStatement addProcedureAsRelatedClinicalStatement(
            String idRoot,
            Procedure procedure,
            List<RelatedClinicalStatement> relatedClinicalStatements) throws EicrException {

        logger.debug("starting addProcedureAsRelatedClinicalStatement()...");
        
        RelatedClinicalStatement result = null;

        if (procedure != null) {

            String[] idElements = IdentifierUtils.getIdElements(procedure.getIds(), idRoot);

            result = CdsObjectFactory.getRelatedClinicalStatement("PERT");
            relatedClinicalStatements.add(result);

            // procedure code
            CD code = CodeUtils.getCode(procedure.getCode());

            // procedure date
            String[] eventTimeElements = IntervalUtils.getEventTimeElements(procedure.getEffectiveTime());

            // status code
            CD status = CodeUtils.getStatusCode(procedure.getStatusCode());

            // create event
            ProcedureEvent procedureEvent = null;
            try {
                procedureEvent = CdsObjectFactory.getProcedureEvent(
                        code.getCode(),
                        code.getDisplayName(),
                        code.getCodeSystem(),
                        code.getCodeSystemName(),
                        status.getCode(),
                        status.getDisplayName(),
                        status.getCodeSystem(),
                        status.getCodeSystemName(),
                        eventTimeElements[0],
                        eventTimeElements[1],
                        idElements[0],
                        idElements[1]);
            } catch (CdsException e) {
                throw new EicrException("Error adding 'ProcedureEvent' to CdsInputMessage", e);

            }
            result.setProcedureEvent(procedureEvent);

            //target sites
            addTargetSiteCodesToProcedureEvent(procedure.getTargetSiteCodes(), procedureEvent);

            EicrSpecimen2Vmr.addSpecimens(
                    idElements[0],
                    procedure.getSpecimens(),
                    procedureEvent.getRelatedEntity());

            EicrPerformer2Vmr.addPerformers(
                    idElements[0],
                    procedure.getPerformers(),
                    procedureEvent.getRelatedEntity());

            EicrParticipant2Vmr.addParticipants(
                    idElements[0],
                    procedure.getParticipants(),
                    procedureEvent.getRelatedEntity());

            EicrEntryRelationship2Vmr.addEntryRelationships(
                    idRoot,
                    procedure.getEntryRelationships(),
                    relatedClinicalStatements,
                    procedureEvent.getRelatedEntity());

        } else {
            logger.debug("procedure is null!");
        }
        return result;
    }

    /**
     * Add the first target site code as the target body site on the procedure event.
     * 
     * @param targetSiteCodes
     * @param result 
     */
    public static void addTargetSiteCodesToProcedureEvent(List<CD> targetSiteCodes, ProcedureEvent result) {

        logger.debug("starting addTargetSiteCodesToProcedureEvent()...");

        if (result != null) {
            if (targetSiteCodes != null) {
                if (!targetSiteCodes.isEmpty()) {
                    if (targetSiteCodes.size() > 1) {
                        logger.error("targetSiteCodes size is greater than 1 - only targetSiteCode value will be converted!");
                    }

                    CD code = CodeUtils.getCode(targetSiteCodes.get(0));
                    BodySite bodySite = new BodySite();
                    bodySite.setBodySiteCode(CdsInputWrapperWrapper.getCD(code.getCode(), code.getCodeSystem(), code.getDisplayName(), code.getCodeSystemName()));
                    result.setTargetBodySite(bodySite);

                } else {
                    logger.debug("values is empty!");
                }
            } else {
                logger.error("targetSiteCodes is null!");
            }
        } else {
            logger.error("result is null!");
        }
    }

}
