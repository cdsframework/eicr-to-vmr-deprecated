package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.SystemUserLookup;
import org.eclipse.emf.common.util.EList;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.RelatedEntity;
import org.openhealthtools.mdht.uml.cda.HealthCareFacility;
import org.openhealthtools.mdht.uml.hl7.datatypes.AD;

import java.util.List;
import org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants;

import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.FACILITY_ENTITY_TYPE_CODE_SYSTEM;
import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.RELATEDENTITY_FACILITY_TEMPLATEID_ROOT;

/**
 * @author HLN Consulting, LLC
 */
public class EicrHealthCareFacilityAddress2Vmr {

    private static final Logger logger = Logger.getLogger(EicrHealthCareFacilityAddress2Vmr.class);

    public static void addHealthCareFacilityAddress2Vmr(HealthCareFacility healthCareFacility, CdsInputWrapper cdsInputWrapper) {

        logger.debug("starting addHealthCareFacilityAddress2Vmr()...");

        if (healthCareFacility.getLocation() == null) {
            logger.error("healthCareFacility.getLocation()==null. Cannot add facility to vMR");
            return;

        }

        List<RelatedEntity> relatedEntities = cdsInputWrapper.getCdsObject().getVmrInput().getPatient().getRelatedEntity();
        relatedEntities.add(createRelatedEntity(healthCareFacility));

    }

    private static RelatedEntity createRelatedEntity(HealthCareFacility healthCareFacility) {

        if (logger.isDebugEnabled()) {
            logger.debug("starting createRelatedEntity()...");
        }
        RelatedEntity relatedEntity = new RelatedEntity();
        RelatedEntity.Facility facility = new RelatedEntity.Facility();

        String entityTypeCode = healthCareFacility.getCode() == null ? "" : healthCareFacility.getCode().getCode();
        String entityType = SystemUserLookup.getEntityType(SystemUserLookup.getVmrSystemUserType(entityTypeCode));

        CD cd = new CD();
        if (entityType == null || entityType.isEmpty()) {
            cd.setCode(CdaConstants.PROVIDER_FACILITY_ABBR);
        } else {
            cd.setCode(entityType);
        }
        cd.setCodeSystem(FACILITY_ENTITY_TYPE_CODE_SYSTEM);
        facility.setEntityType(cd);

        facility.setId(IdentifierUtils.getII(IdentifierUtils.getIdElements(healthCareFacility.getIds())));

        II templateIdII = new II();
        templateIdII.setRoot(RELATEDENTITY_FACILITY_TEMPLATEID_ROOT);
        facility.getTemplateId().add(templateIdII);

        relatedEntity.setFacility(facility);

        // copy the addresses over
        EList<AD> addrs = healthCareFacility.getLocation().getAddrs();
        List<org.opencds.vmr.v1_0.schema.AD> addressesTo = facility.getAddress();
        ConversionHelper.copyFromToAddresses(addrs, addressesTo);

        return relatedEntity;

    }
}
