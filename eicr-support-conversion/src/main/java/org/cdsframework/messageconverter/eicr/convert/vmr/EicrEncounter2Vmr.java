package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.eclipse.emf.common.util.EList;
import org.opencds.vmr.v1_0.schema.EncounterEvent;
import org.openhealthtools.mdht.uml.cda.Encounter;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;

import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.ENCOUNTER_TEMPLATE_ID;
import org.cdsframework.util.LogUtils;

/**
 * @author HLN Consulting, LLC
 */
public class EicrEncounter2Vmr {

    private static final LogUtils logger = LogUtils.getLogger(EicrEncounter2Vmr.class);

    public static void addEncounters(EList<Entry> entries, CdsInputWrapper input) throws EicrException {

        logger.debug("starting addEncounters()...");

        for (Entry entry : entries) {
            try {
                addEncounterAsEncounterEvent(entry.getEncounter(), input);
            } catch (Exception e) {
                logger.error(e);
            }

        }
    }

    /**
     * Adds encounter.
     *
     * @param encounter
     * @param input
     * @return
     * @throws EicrException
     */
    public static EncounterEvent addEncounterAsEncounterEvent(Encounter encounter, CdsInputWrapper input) throws EicrException {

        logger.debug("starting addEncounterAsEncounterEvent()...");

        EncounterEvent result = null;

        if (encounter != null) {

            String[] idElements = IdentifierUtils.getIdElements(encounter.getIds());

            CD code = CodeUtils.getCode(encounter.getCode());

            // never a status?
//                CD status = CodeUtils.getStatusCode(encounter.getStatusCode());
            String[] eventTimeElements = IntervalUtils.getEventTimeElements(encounter.getEffectiveTime());

            try {
                result = input.addEncounterEvent(
                        code.getCode(),
                        code.getDisplayName(),
                        code.getCodeSystem(),
                        code.getCodeSystemName(),
                        eventTimeElements[0],
                        eventTimeElements[1],
                        idElements[0],
                        idElements[1]);

                TemplateIdSetter.setTemplateId(result.getTemplateId(), ENCOUNTER_TEMPLATE_ID);

            } catch (CdsException e) {
                throw new EicrException("ErroR adding 'EncounterEvent' to CdsInput message", e);

            }

            EicrEntryRelationship2Vmr.addEntryRelationships(
                    idElements[0],
                    encounter.getEntryRelationships(),
                    result.getRelatedClinicalStatement(),
                    result.getRelatedEntity());

//            // performer
//            EicrPerformer2Vmr.addPerformers(
//                    idElements[0],
//                    idElements[1],
//                    encounter.getPerformers(),
//                    result.getRelatedEntity());
//
//            // participants
//            EicrParticipant2Vmr.addParticipants(
//                    idElements[0],
//                    idElements[1],
//                    encounter.getParticipants(),
//                    result.getRelatedEntity());
        } else {
            logger.debug("encounter is null!");
        }

        return result;
    }

}
