package org.cdsframework.messageconverter.eicr.convert.enumeration;

/**
 *
 *  copied from cda-support-conversion
 *
 * @author HLN Consulting, LLC
 */
public enum TimeStampType {

    Unknown(null, null),
    Interval("IVL_TSImpl", "IVL_TS"),
    Period("PIVL_TSImpl", "PIVL_TS"),
    SetComponent("SXCM_TSImpl","SXCM_TS"),
    SingleValue("TSImpl", "TS");
    private String className;
    private String interfaceName;

    private TimeStampType() {
        this.className = this.name();
    }

    private TimeStampType(String className, String interfaceName) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public static TimeStampType valueOfClassName(String className) {
        final String METHODNAME = "TimeStampType.valueOfClassName ";
        TimeStampType result = null;
        if (className != null) {
            for (TimeStampType item : TimeStampType.values()) {
                if (className.equalsIgnoreCase(item.getClassName())) {
                    result = item;
                    break;
                }
            }
        } else {
            System.out.println(METHODNAME + "className is null!");
        }
        if (result == null) {
            System.out.println(METHODNAME + "result is null! " + className);
        }
        return result;
    }

    public static TimeStampType valueOfInterfaceName(String interfaceName) {
        final String METHODNAME = "TimeStampType.valueOfInterfaceName ";
        TimeStampType result = null;
        if (interfaceName != null) {
            for (TimeStampType item : TimeStampType.values()) {
                if (interfaceName.equalsIgnoreCase(item.getInterfaceName())) {
                    result = item;
                    break;
                }
            }
        } else {
            System.out.println(METHODNAME + "interfaceName is null!");
        }
        if (result == null) {
            System.out.println(METHODNAME + "result is null! " + interfaceName);
        }
        return result;
    }
}
