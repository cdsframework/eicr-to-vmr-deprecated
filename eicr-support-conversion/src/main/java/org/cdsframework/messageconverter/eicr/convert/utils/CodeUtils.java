package org.cdsframework.messageconverter.eicr.convert.utils;

import org.cdsframework.util.LogUtils;
import org.openhealthtools.mdht.uml.hl7.datatypes.ANY;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * copied from cda-support-conversion
 *
 * @author sdn
 */
public class CodeUtils {

    private final static LogUtils logger = LogUtils.getLogger(CodeUtils.class);

    public static boolean isCode(Object cd) {
        return cd instanceof CD;
    }

    public static List<CD> getCdsFromAny(List<ANY> anys) {
        List<CD> result = new ArrayList<CD>();
        for (ANY any : anys) {
            if (any instanceof CD) {
                result.add((CD) any);
            }
        }
        return result;
    }

    public static CD getStatusCode(CD status) {

        if (status == null) {
            status = DatatypesFactory.eINSTANCE.createCD();
        } else {
            if (status.getCode() != null && status.getCodeSystem() == null) {
                status.setCodeSystem(CdaConstants.LOINC_CODE_SYSTEM_OID);
            }
        }

        return status;
    }

    public static CD getCode(CD code) {

        if (code == null) {
            code = DatatypesFactory.eINSTANCE.createCD();
        }

        return code;
    }
}
