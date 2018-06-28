package org.cdsframework.messageconverter;

import org.apache.log4j.Logger;
import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.exception.ConversionException;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * @author HLN Consulting, LLC
 */
@Service(value = "eICRtoVMRConverter")
public class EICRtoVMRConverter implements Converter {

    private static final Logger logger = Logger.getLogger(EICRtoVMRConverter.class);

    public String convert(String payload) throws ConversionException {

        logger.debug("starting convert()...");

        try {
            ClinicalDocument clinicalDocument = CDAUtil.load(new ByteArrayInputStream(payload.getBytes()));
            CDSInput cdsInput = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);
            return CdsObjectAssist.cdsObjectToString(cdsInput, CDSInput.class);

        } catch (EicrException e) {
            throw new ConversionException("There was an error converting an eICR message to vMR", e);

        } catch (Exception e) {
            throw new ConversionException("There was an error loading the eICR message payload", e);

        }
    }
}
