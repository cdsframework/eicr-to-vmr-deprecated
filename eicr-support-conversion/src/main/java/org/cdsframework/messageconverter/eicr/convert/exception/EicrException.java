package org.cdsframework.messageconverter.eicr.convert.exception;

/**
 * @author HLN Consulting, LLC
 */
public class EicrException extends Exception {

    public EicrException(String s, Exception e) {
        super(s, e);

    }

    public EicrException(String s) {
        super(s);

    }
}
