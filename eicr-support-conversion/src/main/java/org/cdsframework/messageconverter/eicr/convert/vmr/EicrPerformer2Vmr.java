package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdsInputWrapperWrapper;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.opencds.vmr.v1_0.schema.EN;
import org.opencds.vmr.v1_0.schema.ENXP;
import org.opencds.vmr.v1_0.schema.RelatedEntity;
import org.openhealthtools.mdht.uml.cda.AssignedEntity;
import org.openhealthtools.mdht.uml.cda.Organization;
import org.openhealthtools.mdht.uml.cda.Performer2;
import org.openhealthtools.mdht.uml.cda.Person;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.ON;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;

import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 * @author HLN Consulting, LLC
 */
public class EicrPerformer2Vmr {

    private static final Logger logger = Logger.getLogger(EicrPerformer2Vmr.class);

    /**
     * Add the supplied performers.
     *
     * @param idRoot
     * @param performers
     * @param relatedEntities
     * @throws EicrException
     */
    public static void addPerformers(
            String idRoot,
            List<Performer2> performers,
            List<RelatedEntity> relatedEntities)
            throws EicrException {

        logger.debug("starting addPerformers()...");

        if (performers != null) {
            if (!performers.isEmpty()) {
                for (Performer2 performer : performers) {
                    if (performer != null) {

                        AssignedEntity assignedEntity = performer.getAssignedEntity();

                        if (assignedEntity != null) {
                            if (assignedEntity.getCode() != null && assignedEntity.getCode().getCode() != null) {

                                RelatedEntity result = new RelatedEntity();
                                relatedEntities.add(result);

                                RelatedEntity.Person person = new RelatedEntity.Person();
                                result.setPerson(person);

                                String[] idElements = IdentifierUtils.getIdElements(assignedEntity.getIds(), idRoot);

                                person.setId(CdsObjectFactory.getII(idElements[0], idElements[1]));

                                CD code = CodeUtils.getCode(assignedEntity.getCode());

                                person.setEntityType(CdsInputWrapperWrapper.getCD(
                                        code.getCode(),
                                        code.getCodeSystem(),
                                        code.getDisplayName(),
                                        code.getCodeSystemName()));
                            }

                            if (assignedEntity.getRepresentedOrganizations() != null && !assignedEntity.getRepresentedOrganizations().isEmpty()) {
                                for (Organization organization : assignedEntity.getRepresentedOrganizations()) {

                                    RelatedEntity result = new RelatedEntity();
                                    relatedEntities.add(result);

                                    RelatedEntity.Organization org = new RelatedEntity.Organization();
                                    result.setOrganization(org);

                                    String[] idElements = IdentifierUtils.getIdElements(organization.getIds(), idRoot);

                                    org.setId(CdsObjectFactory.getII(idElements[0], idElements[1]));

                                    for (ON on : organization.getNames()) {
                                        ENXP enxp = new ENXP();
                                        enxp.setValue(on.getText());
                                        EN en = new EN();
                                        en.getPart().add(enxp);
                                        org.getName().add(en);
                                    }
                                }
                            }

                            if (assignedEntity.getAssignedPerson() != null) {

                                RelatedEntity result = new RelatedEntity();
                                relatedEntities.add(result);

                                RelatedEntity.Person person = new RelatedEntity.Person();
                                result.setPerson(person);

                                Person assignedPerson = assignedEntity.getAssignedPerson();

                                for (PN pn : assignedPerson.getNames()) {

                                    EN en = new EN();
                                    for (org.openhealthtools.mdht.uml.hl7.datatypes.ENXP suffixPart : pn.getPrefixes()) {
                                        ENXP enxp = new ENXP();
                                        enxp.setValue(suffixPart.getText());
                                        en.getPart().add(enxp);
                                    }
                                    for (org.openhealthtools.mdht.uml.hl7.datatypes.ENXP givenPart : pn.getGivens()) {
                                        ENXP enxp = new ENXP();
                                        enxp.setValue(givenPart.getText());
                                        en.getPart().add(enxp);
                                    }
                                    for (org.openhealthtools.mdht.uml.hl7.datatypes.ENXP familyPart : pn.getFamilies()) {
                                        ENXP enxp = new ENXP();
                                        enxp.setValue(familyPart.getText());
                                        en.getPart().add(enxp);
                                    }
                                    for (org.openhealthtools.mdht.uml.hl7.datatypes.ENXP suffixPart : pn.getSuffixes()) {
                                        ENXP enxp = new ENXP();
                                        enxp.setValue(suffixPart.getText());
                                        en.getPart().add(enxp);
                                    }
                                    person.getName().add(en);
                                }

                            }
                        }

                    } else {
                        logger.debug("performer is empty!");
                    }
                }
            } else {
                logger.debug("performers is empty!");
            }
        } else {
            logger.error("performers is null!");
        }

    }
}
