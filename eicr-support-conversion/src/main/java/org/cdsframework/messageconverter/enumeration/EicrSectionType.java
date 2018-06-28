package org.cdsframework.messageconverter.enumeration;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author HLN Consulting, LLC
 */
public enum EicrSectionType {

    ENCOUNTERS(Arrays.asList(new String[]{"46240-8"})),
    PROBLEM(Arrays.asList(new String[]{"11450-4"})),
    SOCIAL_HISTORY(Arrays.asList(new String[]{"29762-2"})),
    RESULTS(Arrays.asList(new String[]{"30954-2"})),
    HISTORY_OF_PRESENT_ILLNESS(Arrays.asList(new String[]{"10164-2"})),
    REASON_FOR_VISIT(Arrays.asList(new String[]{"29299-5"})),
    MEDICATIONS_ADMINISTERED(Arrays.asList(new String[]{"29549-3"})),
    ADVANCE_DIRECTIVE(Arrays.asList(new String[]{"42348-3"})),
    ALLERGIES(Arrays.asList(new String[]{"48765-2"})),
    FAMILY_HISTORY(Arrays.asList(new String[]{"10157-6"})),
    FUNCTIONAL_STATUS(Arrays.asList(new String[]{"47420-5"})),
    IMMUNIZATIONS(Arrays.asList(new String[]{"11369-6"})),
    MEDICAL_EQUIPMENT(Arrays.asList(new String[]{"46264-8"})),
    MEDICATIONS(Arrays.asList(new String[]{"10160-0"})),
    PAYERS(Arrays.asList(new String[]{"48768-6"})),
    PLAN_OF_CARE(Arrays.asList(new String[]{"18776-5"})),
    PROCEDURES(Arrays.asList(new String[]{"47519-4"})),
    VITAL_SIGNS(Arrays.asList(new String[]{"8716-3"})),
    UNSUPPORTED(Arrays.asList(new String[]{}));

    private List<String> supportedCodes;

    private EicrSectionType(List<String> supportedCodes) {
        this.supportedCodes = supportedCodes;
    }

    /**
     * Get the value of supportedCodes
     *
     * @return the value of supportedCodes
     */
    public List<String> getSupportedCodes() {
        return supportedCodes;
    }

    /**
     * Set the value of supportedCodes
     *
     * @param supportedCodes new value of supportedCodes
     */
    public void setSupportedCodes(List<String> supportedCodes) {
        this.supportedCodes = supportedCodes;
    }

    public boolean isCodeSupported(String code) {
        boolean result = false;
        if (code != null) {
            for (String item : this.getSupportedCodes()) {
                if (item.equalsIgnoreCase(code)) {
                    result = true;
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("code is null!");
        }
        return result;
    }

    public static EicrSectionType getConsolCdaSectionTypeFromCode(String code) {
        EicrSectionType result = UNSUPPORTED;
        for (EicrSectionType item : EicrSectionType.values()) {
            if (item.isCodeSupported(code)) {
                result = item;
                break;
            }
        }
        return result;
    }
}
