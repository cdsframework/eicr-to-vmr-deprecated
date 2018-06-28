package org.cdsframework.messageconverter.eicr.convert.utils;

import org.apache.commons.lang3.StringUtils;
import org.cdsframework.messageconverter.eicr.convert.enumeration.TimeStampType;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.util.LogUtils;
import org.opencds.vmr.v1_0.schema.IVLTS;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.PIVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.SXCM_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * copied and updated from cda-support-conversion
 *
 * @author sdn
 */
public class IntervalUtils {

    private final static LogUtils logger = LogUtils.getLogger(IntervalUtils.class);

    /**
     * Get an initialized instance of IVL_TS.
     *
     * @return an initialized instance of IVL_TS.
     */
    static public IVL_TS getIvlTs() {

        IVL_TS ivl_ts = DatatypesFactory.eINSTANCE.createIVL_TS();
        ivl_ts.setLow(DatatypesFactory.eINSTANCE.createIVXB_TS());
        ivl_ts.setCenter(DatatypesFactory.eINSTANCE.createEIVL_TS());
        ivl_ts.setHigh(DatatypesFactory.eINSTANCE.createIVXB_TS());
        ivl_ts.setWidth(DatatypesFactory.eINSTANCE.createIVL_PQ());
        ivl_ts.setValue(null);
        return ivl_ts;
    }

    /**
     * Determines whether or not the supplied TS instance is an instance of the
     * supplied class name.
     *
     * @param ts
     * @param className
     * @return whether or not the supplied TS instance is an instance of the
     * supplied class name.
     */
    public static boolean isTsInstanceOf(TS ts, String className) {

        final String METHODNAME = "isTsInstanceOf ";
//        long start = System.nanoTime();
        boolean result = false;
        if (ts != null) {
            String simpleName = ts.getClass().getSimpleName();
            if (simpleName.equalsIgnoreCase(className)) {
                result = true;
            }
        } else {
            logger.debug(METHODNAME, "ts is null!");
        }
//        logger.logDuration(METHODNAME, start);
        return result;
    }

    /**
     * Returns the TS type of a TS clinicalStatement.
     *
     * @param ts
     * @return
     */
    public static String getTsType(TS ts) {
        final String METHODNAME = "getTsType ";
        String result = getTimeStampType(ts).name();
        return result;
    }

    /**
     * Returns the TS type of a TS clinicalStatement.
     *
     * @param ts
     * @return
     */
    public static TimeStampType getTimeStampType(TS ts) {
        final String METHODNAME = "getTimeStampType ";
//        long start = System.nanoTime();
        TimeStampType result;
        if (ts != null) {
            result = TimeStampType.valueOfClassName(ts.getClass().getSimpleName());
        } else {
            logger.debug(METHODNAME, "ts is null!");
            result = TimeStampType.Unknown;
        }
//        logger.logDuration(METHODNAME, start);
        return result;
    }

    /**
     * Determines whether or not the TS type is supported.
     *
     * @param ts
     * @return
     */
    public static boolean isTsTypeSupported(TS ts) {
        final String METHODNAME = "isTsTypeSupported ";
//        long start = System.nanoTime();
        boolean result = false;
        if (ts != null) {
            TimeStampType timeStampType = TimeStampType.valueOfClassName(ts.getClass().getSimpleName());
            if (timeStampType != null) {
                result = true;
            }
        } else {
            logger.debug(METHODNAME, "ts is null!");
        }
//        logger.logDuration(METHODNAME, start);
        return result;
    }

    /**
     * Attempts to return a single valid effective time from a list of ts
     * instance.
     *
     * @param tsList
     * @return
     */
    public static SXCM_TS getTsSingleValue(List<SXCM_TS> tsList) {
        final String METHODNAME = "getTsSingleValue ";
        SXCM_TS result = null;
        List<SXCM_TS> validTss = new ArrayList<SXCM_TS>();
        for (SXCM_TS item : tsList) {
            TimeStampType timeStampType = getTimeStampType(item);

            if (timeStampType == null) {
                continue;

            }

            switch (timeStampType) {
                case Interval:
                    validTss.add(item);
                    break;
                case SetComponent:
                    validTss.add(item);
                    break;
                case Period:
                    logger.debug(METHODNAME, "skipping period based ts...");
                    break;
                case SingleValue:
                    validTss.add(item);
                    break;
                case Unknown:
                    logger.warn(METHODNAME, "skipping unknown ts: ", item);
                    break;
            }
        }
        if (validTss.size() == 1) {
            result = validTss.get(0);
        } else {
            logger.error(METHODNAME, "could not refine list to 1: ", tsList);
        }
        return result;
    }

    /**
     * Returns the TS element represented as a string.
     *
     * @param ts
     * @return
     */
    public static String getTsStringValue(SXCM_TS ts) {
        final String METHODNAME = "getTsStringValue ";
        String result = "";
        if (ts != null) {
            TimeStampType timeStampType = getTimeStampType(ts);
            switch (timeStampType) {
                case Interval:
                    IVL_TS ivlts = (IVL_TS) ts;
                    if (ivlts.getValue() != null) {
                        result = ivlts.getValue();
                    } else if (ivlts.getCenter() != null) {
                        result = ivlts.getCenter().getValue();
                    } else if (ivlts.getLow() != null) {
                        result = ivlts.getLow().getValue();
                    } else if (ivlts.getHigh() != null) {
                        result = ivlts.getHigh().getValue();
                    } else {
                        logger.warn(METHODNAME, "no administrative date detected in IVL_TS");
                    }
                    break;
                case Period:
                    PIVL_TS pivlts = (PIVL_TS) ts;
                    if (pivlts.getPeriod() != null) {
                        result = pivlts.getPeriod().getValue() + pivlts.getPeriod().getUnit();
                    } else {
                        logger.warn(METHODNAME, "no administrative date detected in PIVL_TS");
                    }
                    break;
                case SetComponent:
                    SXCM_TS sxcmts = (SXCM_TS) ts;
                    if (sxcmts.getValue() != null) {
                        result = sxcmts.getValue();
                    } else {
                        logger.warn(METHODNAME, "no administrative date detected in SXCM_TS");
                    }
                    break;
                case SingleValue:
                    TS its = (TS) ts;
                    if (its.getValue() != null) {
                        result = its.getValue();
                    } else {
                        logger.warn(METHODNAME, "no administrative date detected in TS");
                    }
                    break;
                case Unknown:
                    logger.warn(METHODNAME, "skipping unknown ts: ", ts);
                    break;
            }
        } else {
            logger.error(METHODNAME, "ts is null!");
        }
        return result;
    }

    /**
     * Returns the TS element low value represented as a string.
     *
     * @param ts
     * @return
     */
    public static String getTsLowStringValue(SXCM_TS ts) {
        final String METHODNAME = "getTsLowStringValue ";
        String result = "";
        if (ts != null) {
            TimeStampType timeStampType = getTimeStampType(ts);
            if (timeStampType == TimeStampType.Interval) {
                IVL_TS ivlts = (IVL_TS) ts;
                if (ivlts.getLow() != null) {
                    result = ivlts.getLow().getValue();
                } else {
                    logger.debug(METHODNAME, "no low value detected in TS");
                }
            } else if (timeStampType == TimeStampType.SetComponent) {
                SXCM_TS sxcmts = (SXCM_TS) ts;
                if (sxcmts.getValue() != null) {
                    result = sxcmts.getValue();
                } else {
                    logger.debug(METHODNAME, "no value detected in TS");
                }
            } else {
                logger.warn(METHODNAME, "skipping unknown ts: ", ts);
            }
        } else {
            logger.error(METHODNAME, "ts is null!");
        }
        return result;
    }

    /**
     * Returns the TS element high value represented as a string.
     *
     * @param ts
     * @return
     */
    public static String getTsHighStringValue(SXCM_TS ts) {
        final String METHODNAME = "getTsHighStringValue ";
        String result = "";
        if (ts != null) {
            TimeStampType timeStampType = getTimeStampType(ts);
            if (timeStampType == TimeStampType.Interval) {
                IVL_TS ivlts = (IVL_TS) ts;
                if (ivlts.getHigh() != null) {
                    result = ivlts.getHigh().getValue();
                } else {
                    logger.debug(METHODNAME, "no high value detected in TS");
                }
            } else if (timeStampType == TimeStampType.SetComponent) {
                SXCM_TS sxcmts = (SXCM_TS) ts;
                if (sxcmts.getValue() != null) {
                    result = sxcmts.getValue();
                } else {
                    logger.debug(METHODNAME, "no value detected in TS");
                }
            } else {
                logger.warn(METHODNAME, "skipping unknown ts: ", ts);
            }
        } else {
            logger.error(METHODNAME, "ts is null!");
        }
        return result;
    }

    /**
     * Returns a string array containing the low/high values of the first
     * SXCM_TS instance in the provided list.
     *
     * @param tsList
     * @return
     */
    public static String[] getSingleEventTimeElements(List<SXCM_TS> tsList) {
        SXCM_TS ts = getTsSingleValue(tsList);
        String lowTime = getLowEventDate(ts).trim();
        String highTime = getHighEventDate(ts).trim();
        if (StringUtils.isEmpty(lowTime) && StringUtils.isEmpty(highTime)) {
            highTime = getEventDate(ts);
            lowTime = highTime;
        }
        String[] result = new String[]{lowTime, highTime};
        return result;
    }

    /**
     * Returns a string array containing the low/high values of a SXCM_TS.
     *
     * @param ts
     * @return
     */
    public static String[] getEventTimeElements(SXCM_TS ts) throws EicrException {
        final String METHODNAME = "getEventTimeElements ";

        logger.debug(METHODNAME, "TimeStampType=", getTimeStampType(ts).getClassName());

        String lowEventDate = getLowEventDate(ts);
        String highEventDate = getHighEventDate(ts);

        if (StringUtils.isEmpty(lowEventDate) && StringUtils.isEmpty(highEventDate)) {
            highEventDate = getEventDate(ts);
            lowEventDate = highEventDate;

        } else if (StringUtils.isEmpty(highEventDate)) {
            highEventDate = lowEventDate;

        }

        if (logger.isDebugEnabled()) {
            logger.warn(METHODNAME, "------------------");
            logger.warn(METHODNAME, "initial lowEventDate=", lowEventDate);
            logger.warn(METHODNAME, "initial highEventDate=", highEventDate);
            logger.warn(METHODNAME, "isDateOfYYYYMMDD(lowEventDate)=", isDateOfYYYYMMDD(lowEventDate));
            logger.warn(METHODNAME, "isDateOfYYYYMMDD(highEventDate)=", isDateOfYYYYMMDD(highEventDate));
        }

        if (!isDateOfYYYYMMDD(lowEventDate)) {
            try {
                lowEventDate = IntervalUtils.convertDate("yyyyMMddHHmmZ", "yyyyMMdd", lowEventDate);

            } catch (ParseException e) {
                logger.error(e);
                throw new EicrException("Error converting lowtime", e);

            }
        }

        if (!isDateOfYYYYMMDD(highEventDate)) {
            try {
                highEventDate = IntervalUtils.convertDate("yyyyMMddHHmmZ", "yyyyMMdd", highEventDate);

            } catch (ParseException e) {
                logger.error(e);
                throw new EicrException("Error converting hightime", e);

            }
        }
        if (logger.isDebugEnabled()) {
            logger.warn(METHODNAME, "final lowEventDate=", lowEventDate);
            logger.warn(METHODNAME, "final highEventDate=", highEventDate);
            logger.warn(METHODNAME, "------------------");
        }

        String[] result = new String[]{lowEventDate, highEventDate};

        return result;
    }

    private static boolean isDateOfYYYYMMDD(String dateStr) {

        if (dateStr == null) {
            return false;

        }

        Pattern pattern = Pattern.compile("((19|20)\\d\\d)(0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])");

        return pattern.matcher(dateStr).matches();

    }

    private static String convertDate(String format1, String format2, String dateStr) throws ParseException {

        String result = "";

        if (dateStr == null || "".equals(dateStr)) {
            return "";

        }

        SimpleDateFormat sdfFormat1 = new SimpleDateFormat(format1);
        SimpleDateFormat sdfFormat2 = new SimpleDateFormat(format2);

        try {
            Date date = sdfFormat1.parse(dateStr);
            result = sdfFormat2.format(date);
        } catch (ParseException e) {
            if (dateStr.length() >= 8 && isDateOfYYYYMMDD(dateStr.substring(0, 8))) {
                return dateStr.substring(0, 8);
            } else {
                throw e;
            }
        }
        return result;

    }

    /**
     * Returns the value value from a SXCM_TS.
     *
     * @param ts
     * @return
     */
    public static String getEventDate(SXCM_TS ts) {
        String result = "";
        if (ts != null) {
            result = IntervalUtils.getTsStringValue(ts);
        }
        return result;
    }

    /**
     * Returns the low value from a SXCM_TS.
     *
     * @param ts
     * @return
     */
    public static String getLowEventDate(SXCM_TS ts) {
        String result = "";
        if (ts != null) {
            result = IntervalUtils.getTsLowStringValue(ts);
        }
        return result;
    }

    /**
     * Returns the high value from a SXCM_TS.
     *
     * @param ts
     * @return
     */
    public static String getHighEventDate(SXCM_TS ts) {
        String result = "";
        if (ts != null) {
            result = IntervalUtils.getTsHighStringValue(ts);
        }
        return result;
    }

    public static IVLTS convertTime(IVL_TS ivl_ts) {
        IVLTS ivlts = new IVLTS();

        if (ivl_ts.getValue() != null) {
            ivlts.setLow(ivl_ts.getValue());
            ivlts.setHigh(ivl_ts.getValue());

        } else {
            ivlts.setLow(ivl_ts.getLow() != null ? ivl_ts.getLow().getValue() : null);
            ivlts.setHigh(ivl_ts.getHigh() != null ? ivl_ts.getHigh().getValue() : null);

        }

        return ivlts;

    }

    public static boolean isEmpty(IVL_TS effectiveTime) {
        return (StringUtils.isBlank(effectiveTime.getValue())
                && (effectiveTime.getHigh() == null || StringUtils.isBlank(effectiveTime.getHigh().getValue())
                && (effectiveTime.getLow() == null || StringUtils.isBlank(effectiveTime.getLow().getValue()))));

    }
}
