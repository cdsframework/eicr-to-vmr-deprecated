package org.cdsframework.messageconverter.eicr.convert.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.opencds.vmr.v1_0.schema.CD;

/**
 * @author Mark Curtis
 */
public class CdsInputWrapperWrapper {

    private static Logger logger = Logger.getLogger(CdsInputWrapperWrapper.class);

    public static CD getCD(String code, String codeSystem, String displayName, String codeSystemName) {

        CD cd = CdsObjectFactory.getCD(code, codeSystem, displayName, codeSystemName);

        if (StringUtils.isBlank(cd.getCode()) || StringUtils.isBlank(cd.getCodeSystem())) {
            cd.setCode("999");
            cd.setCodeSystem("1.1.1.1");

            logger.error((StringUtils.isBlank(cd.getCode()) ? "Code" : "CodeSystem") + " is blank");

        }

        return cd;

    }
}
