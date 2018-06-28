package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.opencds.vmr.v1_0.schema.AddressPartType;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.hl7.datatypes.AD;
import org.openhealthtools.mdht.uml.hl7.datatypes.ADXP;
import org.openhealthtools.mdht.uml.hl7.datatypes.TEL;
import org.openhealthtools.mdht.uml.hl7.vocab.PostalAddressUse;
import org.openhealthtools.mdht.uml.hl7.vocab.TelecommunicationAddressUse;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author HLN Consulting, LLC
 */
public class ConversionHelper {

    private static final Logger logger = Logger.getLogger(ConversionHelper.class);

    public static void copyFromToTelecom(PatientRole patientRole, EvaluatedPerson patient) {

        logger.debug("starting copyFromToTelecom()...");

        if (patientRole.getTelecoms()!=null) {

            EList<TEL> tels = patientRole.getTelecoms();
            Iterator<TEL> telsIterator = tels.iterator();

            List<org.opencds.vmr.v1_0.schema.TEL> telsTo = patient.getDemographics().getTelecom();

            while (telsIterator.hasNext()) {
                TEL telFrom = telsIterator.next();

                org.opencds.vmr.v1_0.schema.TEL telTo = new org.opencds.vmr.v1_0.schema.TEL();

                copyFromToTel(telFrom, telTo);

                telsTo.add(telTo);

            }
        }
    }

    public static void copyFromToTel(TEL telFrom, org.opencds.vmr.v1_0.schema.TEL telTo) {

        logger.debug("starting copyFromToTel()...");

        telTo.setValue(telFrom.getValue());

        for (TelecommunicationAddressUse use : telFrom.getUses()) {
            org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse useTo = org.opencds.vmr.v1_0.schema.TelecommunicationAddressUse.fromValue(use.getName());
            telTo.getUse().add(useTo);

        }

    }


    public static void copyFromTo(EList<ADXP> addressParts, org.opencds.vmr.v1_0.schema.AD addressTo) {

        logger.debug("starting copyFromTo()...");

        for(ADXP addressPart : addressParts) {
            org.opencds.vmr.v1_0.schema.ADXP adxp = new org.opencds.vmr.v1_0.schema.ADXP();
            adxp.setType(AddressPartType.valueOf(addressPart.getPartType().getName()));
            adxp.setValue(addressPart.getText());
            addressTo.getPart().add(adxp);

        }

    }


    public static void copyFromToAddressUses(EList<PostalAddressUse> uses, org.opencds.vmr.v1_0.schema.AD addressTo) {

        logger.debug("starting copyFromToAddressUses()...");

        for(PostalAddressUse postalAddressUseFrom : uses) {
            try {
                org.opencds.vmr.v1_0.schema.PostalAddressUse postalAddressUseTo = org.opencds.vmr.v1_0.schema.PostalAddressUse.valueOf(postalAddressUseFrom.getName());
                addressTo.getUse().add(postalAddressUseTo);

            } catch (IllegalArgumentException e) {
                logger.warn("Non translatable postal address use code: " + postalAddressUseFrom.getName());

            }
        }
    }

    public static void copyFromToPatientAddresses(PatientRole patientRole, EvaluatedPerson patient) {

        logger.debug("starting copyFromToPatientAddresses()...");

        if (patientRole.getAddrs()!=null) {

            EList<AD> addrs = patientRole.getAddrs();
            Iterator<AD> addressesIterator = addrs.iterator();

            List<org.opencds.vmr.v1_0.schema.AD> addressesTo = patient.getDemographics().getAddress();

            while (addressesIterator.hasNext()) {
                AD address = addressesIterator.next();

                org.opencds.vmr.v1_0.schema.AD addressTo = new org.opencds.vmr.v1_0.schema.AD();

                copyFromToAddressUses(address.getUses(), addressTo);
                copyFromTo(address.getStreetAddressLines(), addressTo);
                copyFromTo(address.getCities(), addressTo);
                copyFromTo(address.getStates(), addressTo);
                copyFromTo(address.getPostalCodes(), addressTo);
                copyFromTo(address.getCounties(), addressTo);
                copyFromTo(address.getCountries(), addressTo);

                addressesTo.add(addressTo);

            }
        }
    }

    public static void copyFromToAddresses(EList<AD> addrsFrom, List<org.opencds.vmr.v1_0.schema.AD> addressesTo) {

        logger.debug("starting copyFromToAddresses()...");

        if (addrsFrom!=null) {

            Iterator<AD> addressesIterator = addrsFrom.iterator();

            while (addressesIterator.hasNext()) {
                AD address = addressesIterator.next();

                org.opencds.vmr.v1_0.schema.AD addressTo = new org.opencds.vmr.v1_0.schema.AD();

                copyFromToAddressUses(address.getUses(), addressTo);
                copyFromTo(address.getStreetAddressLines(), addressTo);
                copyFromTo(address.getCities(), addressTo);
                copyFromTo(address.getStates(), addressTo);
                copyFromTo(address.getPostalCodes(), addressTo);
                copyFromTo(address.getCounties(), addressTo);
                copyFromTo(address.getCountries(), addressTo);

                addressesTo.add(addressTo);

            }
        }
    }

    public static String randomizeId() {
        UUID uid = UUID.fromString("ec8a6ff8-ed4b-4f7e-82c3-e98e58b45de7");
        return uid.randomUUID().toString();

    }
}
