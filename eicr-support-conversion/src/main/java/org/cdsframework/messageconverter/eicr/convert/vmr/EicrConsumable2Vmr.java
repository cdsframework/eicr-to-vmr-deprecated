package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.RelatedEntity;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.openhealthtools.mdht.uml.cda.Consumable;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;

import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 * @author HLN Consulting, LLC
 */
public class EicrConsumable2Vmr {

    private final static Logger logger = Logger.getLogger(EicrConsumable2Vmr.class);

    /**
     * Adds a consumable to an substance administration event.
     *
     * @param consumable
     * @param substanceAdministrationEvent
     */
    public static void addConsumableToSubstanceAdministrationEvent(Consumable consumable, SubstanceAdministrationEvent substanceAdministrationEvent) {

        logger.debug("starting addConsumableToSubstanceAdministrationEvent()...");

        if (consumable != null) {
            if (substanceAdministrationEvent != null) {

                AdministrableSubstance administrableSubstance;
                CD code;
                if (consumable.getManufacturedProduct() != null
                        && consumable.getManufacturedProduct().getManufacturedMaterial() != null
                        && consumable.getManufacturedProduct().getManufacturedMaterial().getCode() != null) {
                    String[] idElements = IdentifierUtils.getIdElements(consumable.getManufacturedProduct().getIds());
                    administrableSubstance = CdsObjectFactory.getAdministrableSubstance(idElements[0], idElements[1]);
                    code = consumable.getManufacturedProduct().getManufacturedMaterial().getCode();
                } else {
                    administrableSubstance = CdsObjectFactory.getAdministrableSubstance();
                    code = DatatypesFactory.eINSTANCE.createCE();
                }
                substanceAdministrationEvent.setSubstance(administrableSubstance);

                // set the code
                administrableSubstance.setSubstanceCode(CdsInputWrapperWrapper.getCD(
                        code.getCode(),
                        code.getCodeSystem(),
                        code.getDisplayName(),
                        code.getCodeSystemName()));

                TemplateIdSetter.setTemplateId(administrableSubstance.getTemplateId(), CdaConstants.SUBSTANCE_CODE_TEMPLATE_ID);

                // add code translations
                for (CD translation : code.getTranslations()) {
                    addSubstanceCodeAsRelatedEntity(translation, substanceAdministrationEvent.getRelatedEntity());
                }

            } else {
                logger.error("substanceAdministrationEvent is null!");
            }
        } else {
            logger.error("consumable is null!");
        }
    }

    /**
     * Adds a substance translation as a related entity in a role (substance).
     *
     * @param translation
     * @param relatedEntities
     * @return
     */
    public static RelatedEntity addSubstanceCodeAsRelatedEntity(
            CD translation,
            List<RelatedEntity> relatedEntities) {

        logger.debug("starting addSubstanceCodeAsRelatedEntity()...");

        RelatedEntity result = null;

        if (translation != null) {
            if (relatedEntities != null) {

                result = new RelatedEntity();
                relatedEntities.add(result);
                RelatedEntity.AdministrableSubstance administrableSubstance = new RelatedEntity.AdministrableSubstance();
                result.setAdministrableSubstance(administrableSubstance);

                administrableSubstance.setSubstanceCode(CdsInputWrapperWrapper.getCD(
                        translation.getCode(),
                        translation.getCodeSystem(),
                        translation.getDisplayName(),
                        translation.getCodeSystemName()));

            } else {
                logger.error("substanceAdministrationEvent is null!");
            }
        } else {
            logger.error("consumable is null!");
        }

        return result;
    }
}
