package org.cdsframework.messageconverter.eicr.convert.utils;

import org.cdsframework.util.LogUtils;
import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.ClinicalStatement;
import org.openhealthtools.mdht.uml.cda.Encounter;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.Organizer;
import org.openhealthtools.mdht.uml.cda.Procedure;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.cda.Supply;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * copied from cda-support-conversion
 *
 * @author sdn
 */
public class ClinicalStatementUtils {

    private final static LogUtils logger = LogUtils.getLogger(ClinicalStatementUtils.class);

    /**
     * Return an clinical statement title.
     *
     * @param clinicalStatement
     * @return
     */
    public static String getTitle(ClinicalStatement clinicalStatement) {
        final String METHODNAME = "getTitle ";
        String result = "";
        List<II> templateIds = getTemplateIds(clinicalStatement);
        if (templateIds != null) {
            for (II templateId : templateIds) {
                if (templateId != null) {
                    if (templateId.getAssigningAuthorityName() != null
                            && !templateId.getAssigningAuthorityName().trim().isEmpty()) {
                        result = templateId.getAssigningAuthorityName().trim();
                        break;
                    }
                }
            }
        } else {
            result = "Unknown Clinical Statement Title";
        }
        String id = IdentifierUtils.getId(getIds(clinicalStatement));
        if (id != null) {
            result += " (id:" + id + ")";
        }
        return result;
    }

    /**
     * Returns the ids from a clinical statement.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static List<II> getIds(ClinicalStatement clinicalStatement) {

        final String METHODNAME = "getIds ";
        List<II> ids = null;
        if (clinicalStatement != null) {
            if (clinicalStatement instanceof Act) {
                ids = ((Act) clinicalStatement).getIds();
            } else if (clinicalStatement instanceof Observation) {
                ids = ((Observation) clinicalStatement).getIds();
            } else if (clinicalStatement instanceof SubstanceAdministration) {
                ids = ((SubstanceAdministration) clinicalStatement).getIds();
            } else if (clinicalStatement instanceof Procedure) {
                ids = ((Procedure) clinicalStatement).getIds();
            } else if (clinicalStatement instanceof Organizer) {
                ids = ((Organizer) clinicalStatement).getIds();
            } else if (clinicalStatement instanceof Encounter) {
                ids = ((Encounter) clinicalStatement).getIds();
            } else if (clinicalStatement instanceof Supply) {
                ids = ((Supply) clinicalStatement).getIds();
            } else {
                throw new IllegalArgumentException(logger.error(METHODNAME, "unsupported ClinicalStatement: ", clinicalStatement));
            }
        } else {
            throw new IllegalArgumentException(logger.error(METHODNAME, "clinicalStatement was null!"));
        }
        return ids;
    }

    /**
     * Returns the template ids from a clinical statement.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static List<II> getTemplateIds(ClinicalStatement clinicalStatement) {

        final String METHODNAME = "getTemplateIds ";
        List<II> templateIds = null;
        if (clinicalStatement != null) {
            if (clinicalStatement instanceof Act) {
                templateIds = ((Act) clinicalStatement).getTemplateIds();
            } else if (clinicalStatement instanceof Observation) {
                templateIds = ((Observation) clinicalStatement).getTemplateIds();
            } else if (clinicalStatement instanceof SubstanceAdministration) {
                templateIds = ((SubstanceAdministration) clinicalStatement).getTemplateIds();
            } else if (clinicalStatement instanceof Procedure) {
                templateIds = ((Procedure) clinicalStatement).getTemplateIds();
            } else if (clinicalStatement instanceof Organizer) {
                templateIds = ((Organizer) clinicalStatement).getTemplateIds();
            } else if (clinicalStatement instanceof Encounter) {
                templateIds = ((Encounter) clinicalStatement).getTemplateIds();
            } else if (clinicalStatement instanceof Supply) {
                templateIds = ((Supply) clinicalStatement).getTemplateIds();
            } else {
                throw new IllegalArgumentException(logger.error(METHODNAME, "unsupported ClinicalStatement: ", clinicalStatement));
            }
        } else {
            throw new IllegalArgumentException(logger.error(METHODNAME, "clinicalStatement was null!"));
        }
        return templateIds;
    }

    /**
     * Returns the entry relationships from a clinical statement.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static List<EntryRelationship> getEntryRelationships(ClinicalStatement clinicalStatement) {

        final String METHODNAME = "getEntryRelationships ";
        List<EntryRelationship> entryRelationships = null;
        if (clinicalStatement != null) {
            if (clinicalStatement instanceof Act) {
                entryRelationships = ((Act) clinicalStatement).getEntryRelationships();
            } else if (clinicalStatement instanceof Observation) {
                entryRelationships = ((Observation) clinicalStatement).getEntryRelationships();
            } else if (clinicalStatement instanceof SubstanceAdministration) {
                entryRelationships = ((SubstanceAdministration) clinicalStatement).getEntryRelationships();
            } else if (clinicalStatement instanceof Procedure) {
                entryRelationships = ((Procedure) clinicalStatement).getEntryRelationships();
            } else if (clinicalStatement instanceof Encounter) {
                entryRelationships = ((Encounter) clinicalStatement).getEntryRelationships();
            } else if (clinicalStatement instanceof Supply) {
                entryRelationships = ((Supply) clinicalStatement).getEntryRelationships();
            } else if (clinicalStatement instanceof Organizer) {
                entryRelationships = new ArrayList<EntryRelationship>();
            } else {
                throw new IllegalArgumentException(logger.error(METHODNAME, "unsupported ClinicalStatement: ", clinicalStatement));
            }
        } else {
            throw new IllegalArgumentException(logger.error(METHODNAME, "clinicalStatement was null!"));
        }
        return entryRelationships;
    }

    /**
     * Returns the effective times from a clinical statement.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static List<TS> getEffectiveTimes(ClinicalStatement clinicalStatement) {
        final String METHODNAME = "getEffectiveTimes ";
        List<TS> effectiveTimes = new ArrayList<TS>();
        if (clinicalStatement != null) {
            if (clinicalStatement instanceof SubstanceAdministration) {
                SubstanceAdministration item = (SubstanceAdministration) clinicalStatement;
                effectiveTimes.addAll(item.getEffectiveTimes());
            } else if (clinicalStatement instanceof Procedure) {
                Procedure item = (Procedure) clinicalStatement;
                effectiveTimes.add(item.getEffectiveTime());
            } else if (clinicalStatement instanceof Act) {
                Act item = (Act) clinicalStatement;
                effectiveTimes.add(item.getEffectiveTime());
            } else if (clinicalStatement instanceof Observation) {
                Observation item = (Observation) clinicalStatement;
                effectiveTimes.add(item.getEffectiveTime());
            } else if (clinicalStatement instanceof Organizer) {
                Organizer item = (Organizer) clinicalStatement;
                effectiveTimes.add(item.getEffectiveTime());
            } else if (clinicalStatement instanceof Encounter) {
                Encounter item = (Encounter) clinicalStatement;
                effectiveTimes.add(item.getEffectiveTime());
            } else if (clinicalStatement instanceof Supply) {
                Supply item = (Supply) clinicalStatement;
                effectiveTimes.add(item.getExpectedUseTime());
            } else {
                logger.error(METHODNAME, "No supported element found: ", clinicalStatement.getClass());
            }
        } else {
            logger.debug(METHODNAME, "object is null!");
        }
        return effectiveTimes;
    }

    /**
     * Returns whether or not the supplied clinical statement is an Act.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static boolean isAct(ClinicalStatement clinicalStatement) {

        if (clinicalStatement != null) {
            return clinicalStatement instanceof Act;
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not the supplied clinical statement is an Encounter.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static boolean isEncounter(ClinicalStatement clinicalStatement) {

        if (clinicalStatement != null) {
            return clinicalStatement instanceof Encounter;
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not the supplied clinical statement is an Observation.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static boolean isObservation(ClinicalStatement clinicalStatement) {

        if (clinicalStatement != null) {
            return clinicalStatement instanceof Observation;
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not the supplied clinical statement is a Procedure.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static boolean isProcedure(ClinicalStatement clinicalStatement) {

        if (clinicalStatement != null) {
            return clinicalStatement instanceof Procedure;
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not the supplied clinical statement is an Organizer.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static boolean isOrganizer(ClinicalStatement clinicalStatement) {

        if (clinicalStatement != null) {
            return clinicalStatement instanceof Organizer;
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not the supplied clinical statement is a Supply.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static boolean isSupply(ClinicalStatement clinicalStatement) {

        if (clinicalStatement != null) {
            return clinicalStatement instanceof Supply;
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not the supplied clinical statement is a SubstanceAdministration.
     * 
     * @param clinicalStatement
     * @return 
     */
    public static boolean isSubstanceAdministration(ClinicalStatement clinicalStatement) {

        if (clinicalStatement != null) {
            return clinicalStatement instanceof SubstanceAdministration;
        } else {
            return false;
        }
    }

}
