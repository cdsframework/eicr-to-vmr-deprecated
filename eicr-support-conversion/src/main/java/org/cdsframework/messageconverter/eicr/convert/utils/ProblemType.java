package org.cdsframework.messageconverter.eicr.convert.utils;

/**
 * @author HLN Consulting, LLC
 */
public class ProblemType {

    private String code;
    private String displayName;
    private String codeSystem;
    private String codeSystemOID;
    private String vmrProblemType;
    private String resultingOID;

    public ProblemType(String code, String displayName, String codeSystem, String codeSystemOID, String vmrProblemType, String resultingOID) {
        this.code = code;
        this.displayName = displayName;
        this.codeSystem = codeSystem;
        this.codeSystemOID = codeSystemOID;
        this.vmrProblemType = vmrProblemType;
        this.resultingOID = resultingOID;

    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public String getCodeSystemOID() {
        return codeSystemOID;
    }

    public String getVmrProblemType() {
        return vmrProblemType;
    }

    public String getResultingOID() {
        return resultingOID;
    }
}
