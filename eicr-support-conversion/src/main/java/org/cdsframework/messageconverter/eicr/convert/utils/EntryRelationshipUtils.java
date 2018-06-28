package org.cdsframework.messageconverter.eicr.convert.utils;

import org.cdsframework.util.LogUtils;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalStatement;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.Organizer;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClassObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.ActMood;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActClassDocumentEntryOrganizer;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sdn
 */
public class EntryRelationshipUtils {

    final static public LogUtils logger = LogUtils.getLogger(EntryRelationshipUtils.class);

    /**
     * Returns the title of an entry relationship.
     * 
     * @param entryRelationship
     * @return 
     */
    public static String getTitle(EntryRelationship entryRelationship) {
        
        final String METHODNAME = "getTitle ";
        String result;
        ClinicalStatement clinicalStatement = getClinicalStatement(entryRelationship);
        if (clinicalStatement != null) {
            result = ClinicalStatementUtils.getTitle(clinicalStatement);
        } else {
            result = "Unknown Entry Relationship Title";
        }
        return result;
    }

    /**
     * Returns the id of an entry relationship.
     *
     * @param entryRelationship
     * @return
     */
    public static String getId(EntryRelationship entryRelationship) {
        final String METHODNAME = "getId ";
        String result = null;
        if (entryRelationship != null) {
            List<II> ids = getIds(entryRelationship);
            if (ids != null && !ids.isEmpty()) {
                result = IdentifierUtils.getId(ids);
            }
        } else {
            logger.error(METHODNAME, "entryRelationship is null!");
        }
        return result;
    }

    /**
     * Returns the list of ids from an entry relationship.
     *
     * @param entryRelationship
     * @return
     */
    public static List<II> getIds(EntryRelationship entryRelationship) {
        final String METHODNAME = "getIds ";
        List<II> ids;
        if (entryRelationship != null) {
            if (entryRelationship.getSubstanceAdministration() != null) {
                ids = entryRelationship.getSubstanceAdministration().getIds();
            } else if (entryRelationship.getProcedure() != null) {
                ids = entryRelationship.getProcedure().getIds();
            } else if (entryRelationship.getAct() != null) {
                ids = entryRelationship.getAct().getIds();
            } else if (entryRelationship.getObservation() != null) {
                ids = entryRelationship.getObservation().getIds();
            } else if (entryRelationship.getOrganizer() != null) {
                ids = entryRelationship.getOrganizer().getIds();
            } else if (entryRelationship.getEncounter() != null) {
                ids = entryRelationship.getEncounter().getIds();
            } else if (entryRelationship.getSupply() != null) {
                ids = entryRelationship.getSupply().getIds();
            } else {
                ids = new ArrayList<II>();
                logger.error(METHODNAME, "No supported entryRelationship sub element found.");
            }
        } else {
            ids = new ArrayList<II>();
            logger.debug(METHODNAME, "entryRelationship is null!");
        }
        return ids;
    }

    /**
     * Returns a clinical statement from an entry relationship.
     * 
     * @param entryRelationship
     * @return 
     */
    public static ClinicalStatement getClinicalStatement(EntryRelationship entryRelationship) {

        final String METHODNAME = "getClinicalStatement ";
        ClinicalStatement result = null;
        if (entryRelationship != null) {
            if (entryRelationship.getSubstanceAdministration() != null) {
                result = entryRelationship.getSubstanceAdministration();
            } else if (entryRelationship.getProcedure() != null) {
                result = entryRelationship.getProcedure();
            } else if (entryRelationship.getAct() != null) {
                result = entryRelationship.getAct();
            } else if (entryRelationship.getObservation() != null) {
                result = entryRelationship.getObservation();
            } else if (entryRelationship.getOrganizer() != null) {
                result = entryRelationship.getOrganizer();
            } else if (entryRelationship.getEncounter() != null) {
                result = entryRelationship.getEncounter();
            } else if (entryRelationship.getSupply() != null) {
                result = entryRelationship.getSupply();
            } else {
                logger.error(METHODNAME, "No supported entry relationship entry found.");
            }
        } else {
            logger.error(METHODNAME, "entryRelationship was null!");
        }
        return result;
    }

    /**
     * Add an Observation to an EntryRelationship.
     *
     * @param classCode
     * @param moodCode
     * @param negationInd
     * @param code
     * @param codeSystem
     * @param entryRelationship
     * @return
     */
    public static Observation addObservationToEntryRelationship(
            ActClassObservation classCode,
            x_ActMoodDocumentObservation moodCode,
            Boolean negationInd,
            String code,
            String codeSystem,
            EntryRelationship entryRelationship) {

        Observation observation = CDAFactory.eINSTANCE.createObservation();

        observation.setClassCode(classCode);
        observation.setMoodCode(moodCode);
        observation.setNegationInd(negationInd);

        // code
        if (code != null) {
            CE ce = DatatypesFactory.eINSTANCE.createCE(code, codeSystem, "", "");
            observation.setCode(ce);
        }

        entryRelationship.setObservation(observation);

        return observation;
    }

    /**
     * Add an Organizer to an EntryRelationship.
     *
     * @param classCode
     * @param moodCode
     * @param statusCode
     * @param entryRelationship
     * @return
     */
    public static Organizer addOrganizerToEntryRelationship(
            x_ActClassDocumentEntryOrganizer classCode,
            ActMood moodCode,
            String statusCode,
            EntryRelationship entryRelationship) {

        Organizer organizer = CDAFactory.eINSTANCE.createOrganizer();

        organizer.setClassCode(classCode);
        organizer.setMoodCode(moodCode);

        // status code
        if (statusCode != null) {
            organizer.setStatusCode(DatatypesFactory.eINSTANCE.createCS(statusCode));
        }

        entryRelationship.setOrganizer(organizer);

        return organizer;
    }

}
