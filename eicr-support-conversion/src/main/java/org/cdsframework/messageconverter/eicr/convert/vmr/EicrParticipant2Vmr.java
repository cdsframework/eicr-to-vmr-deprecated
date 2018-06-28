package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.opencds.vmr.v1_0.schema.EN;
import org.opencds.vmr.v1_0.schema.RelatedEntity;
import org.openhealthtools.mdht.uml.cda.Device;
import org.openhealthtools.mdht.uml.cda.Participant2;
import org.openhealthtools.mdht.uml.cda.ParticipantRole;
import org.openhealthtools.mdht.uml.cda.PlayingEntity;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.ENXP;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.vocab.EntityClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassRoot;

import java.util.Collections;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 * @author HLN Consulting, LLC
 */
public class EicrParticipant2Vmr {

    private static final Logger logger = Logger.getLogger(EicrParticipant2Vmr.class);

    /**
     * Add the supplied participants.
     *
     * @param idRoot
     * @param participants
     * @param relatedEntities
     * @throws EicrException
     */
    public static void addParticipants(
            String idRoot,
            List<Participant2> participants,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addParticipants()...");

        if (participants != null) {
            if (!participants.isEmpty()) {
                for (Participant2 participant : participants) {
                    if (participant != null) {

                        RelatedEntity result = addParticipantRole(idRoot,
                                participant.getTypeCode(),
                                participant.getParticipantRole(),
                                relatedEntities);

                    } else {
                        logger.warn("participant is empty!");
                    }
                }
            } else {
                logger.debug("participants is empty!");
            }
        } else {
            logger.debug("participants is null!");
        }
    }

    /**
     * Add the supplied participant role.
     *
     * @param idRoot
     * @param participationType
     * @param participantRole
     * @param relatedEntities
     * @return
     * @throws EicrException
     */
    public static RelatedEntity addParticipantRole(
            String idRoot,
            ParticipationType participationType,
            ParticipantRole participantRole,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addParticipantRole()...");

        RelatedEntity result = null;

        if (participantRole != null) {
            if (participantRole.getPlayingEntity() != null) {
                result = addPlayingEntity(idRoot,
                        participationType,
                        participantRole.getClassCode(),
                        participantRole.getPlayingEntity(),
                        relatedEntities);

                if (result != null) {
                    if (participationType == ParticipationType.LOC
                            && participantRole.getClassCode() == RoleClassRoot.SDLOC) {
                        RelatedEntity.Facility facility = result.getFacility();
                        if (facility != null) {
                            CD code = CodeUtils.getCode(participantRole.getCode());
                            facility.setEntityType(CdsInputWrapperWrapper.getCD(
                                    code.getCode(),
                                    code.getCodeSystem(),
                                    code.getDisplayName(),
                                    code.getCodeSystemName()));
                        }
                    } else if (participationType == ParticipationType.VRF
                            || participationType == ParticipationType.CST
                            || participationType == ParticipationType.COV) {

                        RelatedEntity.Person person = result.getPerson();
                        if (person != null) {
                            String[] idElements = IdentifierUtils.getIdElements(participantRole.getIds(), idRoot);
                            person.setId(CdsObjectFactory.getII(idElements[0], idElements[1]));
                            CD code = CodeUtils.getCode(participantRole.getCode());
                            person.setEntityType(CdsInputWrapperWrapper.getCD(
                                    code.getCode(),
                                    code.getCodeSystem(),
                                    code.getDisplayName(),
                                    code.getCodeSystemName()));
                        }
                    }
                }
            }

            if (participantRole.getPlayingDevice() != null) {
                result = addPlayingDevice(idRoot,
                        participationType,
                        participantRole.getClassCode(),
                        participantRole.getPlayingDevice(),
                        relatedEntities);
                if (result != null) {
                    if (participationType == ParticipationType.PRD
                            || participationType == ParticipationType.DEV) {

                        RelatedEntity.Entity entity = result.getEntity();
                        if (entity != null) {
                            String[] idElements = IdentifierUtils.getIdElements(participantRole.getIds(), idRoot);
                            entity.setId(CdsObjectFactory.getII(idElements[0], idElements[1]));
                        }
                    }
                }
            }

        } else {
            result = new RelatedEntity();
            logger.debug("participantRole is null!");
        }

        return result;
    }

    /**
     * Add the supplied playing entity.
     *
     * @param idRoot
     * @param participationType
     * @param roleClassRoot
     * @param playingEntity
     * @param relatedEntities
     * @return
     * @throws EicrException
     */
    public static RelatedEntity addPlayingEntity(
            String idRoot,
            ParticipationType participationType,
            RoleClassRoot roleClassRoot,
            PlayingEntity playingEntity,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addPlayingEntity()...");

        RelatedEntity result = new RelatedEntity();
        relatedEntities.add(result);

        if (playingEntity != null) {

            if (participationType == ParticipationType.CSM
                    && roleClassRoot == RoleClassRoot.MANU
                    && playingEntity.getClassCode() == EntityClassRoot.MMAT) {
                RelatedEntity.AdministrableSubstance administrableSubstance = new RelatedEntity.AdministrableSubstance();
                result.setAdministrableSubstance(administrableSubstance);

                administrableSubstance.setId(IdentifierUtils.getII(IdentifierUtils.getIdElements(Collections.emptyList(), idRoot)));

                CD code = CodeUtils.getCode(playingEntity.getCode());
                administrableSubstance.setSubstanceCode(CdsInputWrapperWrapper.getCD(
                        code.getCode(),
                        code.getCodeSystem(),
                        code.getDisplayName(),
                        code.getCodeSystemName()));

                // add code translations
                for (CD translation : code.getTranslations()) {
                    EicrConsumable2Vmr.addSubstanceCodeAsRelatedEntity(translation, relatedEntities);
                }

            } else if (participationType == ParticipationType.LOC
                    && roleClassRoot == RoleClassRoot.SDLOC
                    && playingEntity.getClassCode() == EntityClassRoot.PLC) {
                RelatedEntity.Facility facility = new RelatedEntity.Facility();
                result.setFacility(facility);

                facility.setId(IdentifierUtils.getII(IdentifierUtils.getIdElements(Collections.emptyList(), idRoot)));

                for (PN pn : playingEntity.getNames()) {
                    EN en = new EN();
                    org.opencds.vmr.v1_0.schema.ENXP name = new org.opencds.vmr.v1_0.schema.ENXP();
                    name.setValue(pn.getText());
                    en.getPart().add(name);
                    facility.getName().add(en);
                }

            } else if (participationType == ParticipationType.VRF
                    || participationType == ParticipationType.CST
                    || participationType == ParticipationType.COV) {

                RelatedEntity.Person person = new RelatedEntity.Person();
                result.setPerson(person);

                for (PN pn : playingEntity.getNames()) {

                    // prefixes
                    for (ENXP prefix : pn.getPrefixes()) {
                        EN en = new EN();
                        org.opencds.vmr.v1_0.schema.ENXP part = new org.opencds.vmr.v1_0.schema.ENXP();
                        part.setValue(prefix.getText());
                        en.getPart().add(part);
                        person.getName().add(en);
                    }

                    // first names
                    for (ENXP given : pn.getGivens()) {
                        EN en = new EN();
                        org.opencds.vmr.v1_0.schema.ENXP part = new org.opencds.vmr.v1_0.schema.ENXP();
                        part.setValue(given.getText());
                        en.getPart().add(part);
                        person.getName().add(en);
                    }

                    // family names
                    for (ENXP family : pn.getFamilies()) {
                        EN en = new EN();
                        org.opencds.vmr.v1_0.schema.ENXP part = new org.opencds.vmr.v1_0.schema.ENXP();
                        part.setValue(family.getText());
                        en.getPart().add(part);
                        person.getName().add(en);
                    }

                    // suffixes names
                    for (ENXP suffix : pn.getSuffixes()) {
                        EN en = new EN();
                        org.opencds.vmr.v1_0.schema.ENXP part = new org.opencds.vmr.v1_0.schema.ENXP();
                        part.setValue(suffix.getText());
                        en.getPart().add(part);
                        person.getName().add(en);
                    }

                }

            } else {
                logger.warn("playingEntity type not converted: " + participationType + " - " + roleClassRoot + " - " + playingEntity.getClassCode());
            }

        } else {
            logger.debug("playingEntity is null!");
        }

        return result;
    }

    /**
     * Add the supplied playing device.
     *
     * @param idRoot
     * @param participationType
     * @param roleClassRoot
     * @param playingDevice
     * @param relatedEntities
     * @return
     * @throws EicrException
     */
    public static RelatedEntity addPlayingDevice(
            String idRoot,
            ParticipationType participationType,
            RoleClassRoot roleClassRoot,
            Device playingDevice,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addPlayingDevice()...");

        RelatedEntity result = new RelatedEntity();
        relatedEntities.add(result);

        if (playingDevice != null) {

            if (participationType == ParticipationType.PRD
                    || participationType == ParticipationType.DEV) {

                RelatedEntity.Entity entity = new RelatedEntity.Entity();
                result.setEntity(entity);

                entity.setId(IdentifierUtils.getII(IdentifierUtils.getIdElements(Collections.emptyList(), idRoot)));

                CD code = CodeUtils.getCode(playingDevice.getCode());
                entity.setEntityType(CdsInputWrapperWrapper.getCD(
                        code.getCode(),
                        code.getCodeSystem(),
                        code.getDisplayName(),
                        code.getCodeSystemName()));

            } else {
                logger.warn("playingDevice type not converted: " + participationType + " - " + roleClassRoot + " - " + playingDevice.getClassCode());
            }

        } else {
            logger.debug("playingDevice is null!");
        }

        return result;
    }

}
