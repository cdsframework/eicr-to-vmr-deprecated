package org.cdsframework.messageconverter.eicr.convert.utils;

import org.opencds.vmr.v1_0.schema.II;

import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 * @author HLN Consulting, LLC
 */
public class TemplateIdSetter {

    public static void setTemplateId(List<II> templateIds, String templateId) {

        templateIds.clear();
        templateIds.add(CdsObjectFactory.getII(templateId));

    }
}
