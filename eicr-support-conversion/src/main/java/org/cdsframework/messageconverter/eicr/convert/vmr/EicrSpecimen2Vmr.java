package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.opencds.vmr.v1_0.schema.RelatedEntity;
import org.openhealthtools.mdht.uml.cda.PlayingEntity;
import org.openhealthtools.mdht.uml.cda.Specimen;
import org.openhealthtools.mdht.uml.cda.SpecimenRole;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassSpecimen;

import java.util.Collections;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 * @author HLN Consulting, LLC
 */
public class EicrSpecimen2Vmr {

    private static final Logger logger = Logger.getLogger(EicrSpecimen2Vmr.class);
    /**
     * Add the supplied participants.
     *
     * @param idRoot
     * @param specimens
     * @param relatedEntities
     * @throws EicrException
     */
    public static void addSpecimens(
            String idRoot,
            List<Specimen> specimens,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addSpecimens()...");

        if (specimens != null) {
            if (!specimens.isEmpty()) {
                for (Specimen specimen : specimens) {
                    if (specimen != null) {

                        RelatedEntity result = addSpecimenRole(
                                idRoot,
                                specimen.getTypeCode(),
                                specimen.getSpecimenRole(),
                                relatedEntities);

                    } else {
                        logger.warn("specimen is empty!");
                    }
                }
            } else {
                logger.debug("specimens is empty!");
            }
        } else {
            logger.debug("specimens is null!");
        }
    }

    /**
     * Add the supplied specimen role.
     *
     * @param idRoot
     * @param participationType
     * @param specimenRole
     * @param relatedEntities
     * @return
     * @throws EicrException
     */
    public static RelatedEntity addSpecimenRole(
            String idRoot,
            ParticipationType participationType,
            SpecimenRole specimenRole,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addSpecimenRole()...");

        RelatedEntity result = null;

        if (specimenRole != null) {
            if (specimenRole.getSpecimenPlayingEntity()!= null) {
                result = addSpecimenPlayingEntity(
                        idRoot,
                        participationType,
                        specimenRole.getClassCode(),
                        specimenRole.getSpecimenPlayingEntity(),
                        relatedEntities);
            }

            if (result != null) {
                if (participationType == ParticipationType.SPC) {

                    RelatedEntity.Specimen specimen = result.getSpecimen();
                    if (specimen != null) {
                        String[] idElements = IdentifierUtils.getIdElements(specimenRole.getIds(), idRoot);
                        specimen.setId(CdsObjectFactory.getII(idElements[0], idElements[1]));
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
     * Add the supplied specimen playing entity.
     *
     * @param idRoot
     * @param participationType
     * @param roleClassSpecimen
     * @param specimenPlayingEntity
     * @param relatedEntities
     * @return
     * @throws EicrException
     */
    public static RelatedEntity addSpecimenPlayingEntity(
            String idRoot,
            ParticipationType participationType,
            RoleClassSpecimen roleClassSpecimen,
            PlayingEntity specimenPlayingEntity,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addSpecimenPlayingEntity()...");

        RelatedEntity result = new RelatedEntity();
        relatedEntities.add(result);

        if (specimenPlayingEntity != null) {

            if (participationType == ParticipationType.SPC
                    && roleClassSpecimen == RoleClassSpecimen.SPEC) {
                RelatedEntity.Specimen specimen = new RelatedEntity.Specimen();
                result.setSpecimen(specimen);

                String [] idElements = IdentifierUtils.getIdElements(Collections.emptyList(), idRoot);
                specimen.setId(CdsObjectFactory.getII(idElements[0], idElements[1]));

                CD code = CodeUtils.getCode(specimenPlayingEntity.getCode());
                specimen.setEntityType(CdsInputWrapperWrapper.getCD(
                        code.getCode(),
                        code.getCodeSystem(),
                        code.getDisplayName(),
                        code.getCodeSystemName()));
            } else {
                logger.warn("specimenPlayingEntity type not converted: " +participationType + " - " + roleClassSpecimen + " - " + specimenPlayingEntity.getClassCode());
            }

        } else {
            logger.debug("specimenPlayingEntity is null!");
        }

        return result;
    }

}
