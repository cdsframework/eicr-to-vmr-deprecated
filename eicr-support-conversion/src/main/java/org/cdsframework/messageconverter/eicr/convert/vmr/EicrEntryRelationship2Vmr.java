package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.EntryRelationshipUtils;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.RelatedEntity;
import org.openhealthtools.mdht.uml.cda.ClinicalStatement;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;

import java.util.List;

import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.DIAGNOSIS;

/**
 * @author HLN Consulting, LLC
 */
public class EicrEntryRelationship2Vmr {

    private static final Logger logger = Logger.getLogger(EicrEntryRelationship2Vmr.class);

    /**
     * Add entry relationships.
     *
     * @param idRoot
     * @param entryRelationships
     * @param relatedClinicalStatements
     * @param relatedEntities
     * @throws EicrException
     */
    public static void addEntryRelationships(
            String idRoot,
            List<EntryRelationship> entryRelationships,
            List<RelatedClinicalStatement> relatedClinicalStatements,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addEntryRelationships()...");

        if (entryRelationships != null) {
            for (EntryRelationship entryRelationship : entryRelationships) {
                if (entryRelationship.getObservation() != null) {
                    EicrProblem2Vmr.addProblemAsRelatedClinicalStatement(idRoot,
                            entryRelationship.getObservation(),
                            relatedClinicalStatements);
                }

                if (entryRelationship.getAct() != null) {
                    EicrAct2Vmr.addActAsRelatedClinicalStatement(idRoot,
                            entryRelationship.getAct(),
                            relatedClinicalStatements);
                }

                if (entryRelationship.getSupply()!= null) {
                    EicrSupply2Vmr.addSupplyAsRelatedClinicalStatement(idRoot,
                            entryRelationship.getSupply(),
                            relatedClinicalStatements,
                            relatedEntities);
                }

                if (entryRelationship.getProcedure()!= null) {
                    EicrProcedure2Vmr.addProcedureAsRelatedClinicalStatement(idRoot,
                            entryRelationship.getProcedure(),
                            relatedClinicalStatements);
                }

                if (entryRelationship.getObservation() == null
                        && entryRelationship.getAct() == null
                        && entryRelationship.getProcedure()== null
                        && entryRelationship.getSupply() == null) {
                    ClinicalStatement clinicalStatement = EntryRelationshipUtils.getClinicalStatement(entryRelationship);
                    logger.info("UNSUPPORTED ENTRY RELATIONSHIP FOUND: " + clinicalStatement.toString());
                }
            }
        } else {
            logger.error("entryRelationships is null!");
        }
    }

}


