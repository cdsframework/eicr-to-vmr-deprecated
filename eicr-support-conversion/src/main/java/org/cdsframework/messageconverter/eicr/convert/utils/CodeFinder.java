package org.cdsframework.messageconverter.eicr.convert.utils;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.cdsframework.messageconverter.eicr.convert.utils.CodeFinder.CodeSystem.PROBLEMSTATUS_CODESYSTEM;

/**
 * @author HLN Consulting, LLC
 */
public class CodeFinder {

    private static final Logger logger = Logger.getLogger(CodeFinder.class);



    public enum CodeSystem {
        PROBLEMSTATUS_CODESYSTEM;

    }

    public enum Element {
        PROBLEMSTATUS;

    }

    private static Map<CodeSystem, Map<Element, Map<String, String>>> codeMap = new HashMap<>();
    private static Map<CodeSystem, String> codeSystemMap = new HashMap<>();
    private static Map<CodeSystem, String> codeSystemNameMap = new HashMap<>();

    static {
        Map codeValueMap = new HashMap<>();
        codeValueMap.put("active", "55561003");
        codeValueMap.put("inactive", "73425007");
        codeValueMap.put("problem resolved", "413322009");

        Map elementValue = new HashMap<>();
        elementValue.put(Element.PROBLEMSTATUS, codeValueMap);

        codeMap.put(PROBLEMSTATUS_CODESYSTEM, elementValue);

        codeSystemMap.put(PROBLEMSTATUS_CODESYSTEM, CdaConstants.PROBLEM_STATUS_CODE_SYSTEM);

        codeSystemNameMap.put(PROBLEMSTATUS_CODESYSTEM, "SNOMED-CT");

    }

    public static String getCode(CodeSystem codeSystem, Element element, String status) {
        try {
            return codeMap.get(codeSystem).get(element).get(status.toLowerCase());

        } catch (NullPointerException e) {
            logger.info("code does not exist: " + codeSystem + ", " + element + ", " + status);

        }

        return null;

    }

    public static String getCodeSystem(CodeSystem codeSystem) {
        return codeSystemMap.get(codeSystem);

    }

    public static String getCodeSystemName(CodeSystem codeSystem) {
        return codeSystemNameMap.get(codeSystem);

    }
}
