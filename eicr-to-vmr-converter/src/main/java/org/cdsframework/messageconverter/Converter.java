package org.cdsframework.messageconverter;


import org.cdsframework.messageconverter.exception.ConversionException;

/**
 * Hello world!
 *
 */
public interface Converter {

    String convert(String payload) throws ConversionException;

}
