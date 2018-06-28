package org.cdsframework.messageconverter.eicr.convert.utils;

import org.apache.commons.lang3.StringUtils;
import org.cdsframework.base.BaseCdsObject;
import org.cdsframework.util.LogUtils;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;

import java.util.List;
import java.util.UUID;
import org.cdsframework.cds.util.CdsObjectFactory;

/**
 *
 * copied from cda-support-conversion
 *
 * @author sdn
 */
public class IdentifierUtils {
    
    private final static LogUtils logger = LogUtils.getLogger(IdentifierUtils.class);

    /**
     * Returns the root attribute of an ID datatype list. Logs errors if the list isn't the size of 1.
     *
     * @param ids
     * @return
     */
    public static String getId(List<II> ids) {
        final String METHODNAME = "getId ";
        String result = null;
        II ii = getSingleII(ids);
        if (ii != null) {
            result = ii.getRoot();
            if (ii.getExtension() != null && !ii.getExtension().trim().isEmpty()) {
                result += "-" + ii.getExtension().trim();
            }
        } else {
            logger.error(METHODNAME, "ii is null!");
        }
        return result;
    }

    /**
     * Returns the single ID from a II datatype list. Logs warning if the list isn't the size of 1.
     *
     * @param ids
     * @return
     */
    public static II getSingleII(List<II> ids) {
        final String METHODNAME = "getSingleII ";
        II result = null;
        if (ids != null) {
            if (ids.isEmpty()) {
                logger.debug(METHODNAME, "id list is zero!");
            } else if (ids.size() > 1) {
                logger.warn(METHODNAME, "id list is greater than 1!");
                for (II item : ids) {
                    logger.info(METHODNAME, "ID: ", item.getRoot(), " - ", item.getExtension(), " - ", item.getAssigningAuthorityName());
                    result = ids.get(0);
                    break;
                }
            } else {
                result = ids.get(0);
            }
        } else {
            logger.error(METHODNAME, "ids is null!");
        }
        return result;
    }

    /**
     * Determines whether or not the supplied template ID list contains the supplied template ID.
     *
     * @param templateIds
     * @param templateId
     * @return
     */
    public static boolean containsSuppliedTemplateId(List<II> templateIds, String templateId) {
        final String METHODNAME = "containsSuppliedTemplateId ";
//        long start = System.nanoTime();
        boolean result = false;
        if (templateId != null) {
            if (templateIds != null) {
                for (II ii : templateIds) {
                    if (ii != null) {
                        if (ii.getRoot() != null) {
                            if (ii.getRoot().equalsIgnoreCase(templateId)) {
                                result = true;
                                break;
                            }
                        } else {
                            logger.error(METHODNAME, "ii.getRoot() is null!");
                        }
                    } else {
                        logger.error(METHODNAME, "ii is null!");
                    }
                }
            } else {
                logger.debug(METHODNAME, "null or empty template ID list");
            }
        } else {
            logger.error(METHODNAME, "templateId is null!");
        }
//        logger.logDuration(METHODNAME, start);
        return result;
    }

    /**
     * Returns a string array of the root an extension values from an II. If the root value is null a random UUID is supplied.
     *
     * @param id
     * @return
     */
    public static String[] getId(II id) {
        final String METHODNAME = "getId ";
        
        String root = id.getRoot();
        String extension = id.getExtension();
        logger.debug(METHODNAME, "id root: ", root);
        logger.debug(METHODNAME, "id ext: ", extension);
        

        if (StringUtils.isEmpty(root)) {
            root = UUID.randomUUID().toString();
        }
        
        if (id.getExtension() != null
                && id.getRoot() != null
                && !id.getRoot().contains(".")) {
            logger.warn(METHODNAME,
                    "dropping extension - opencds requires an OID in the root if the extension has a value: ",
                    root,
                    " - ",
                    extension);
            extension = null;
        }

	/*
	 * The following code re-assigns all ids to new random UUIDs, which I thought
	 * would help -- because doing it on the CCD before sending it to cda-ws-web
	 * does help -- but instead, this breaks redaction entirely, so I must be 
	 * misunderstanding.  Commenting out for now:
	 */

	   /* Cannot trust id in incoming CCDs to be correct and unique,
	    * so we are going to re-assign _all_ of them ourselves
	    * instead of only fixing _some_ of them as was done in the
	    * commented-out code above.
            */
 	   /*
	   root = UUID.randomUUID().toString();
	   extension = null;
	   */

        return new String[]{root, extension};
    }

    public static String [] getId() {
        return new String [] {UUID.randomUUID().toString(), null};

    }

    /**
     * Returns the first set of id elements from the supplied id list. If one doesn't exist use the supplied alt values. If the alt
     * value isn't supplied then generate an ID and add it to the list.
     *
     * @param ids
     * @return
     */
    public static String[] getIdElements(List<II> ids) {

        final String METHODNAME = "getIdElements ";

        return getIdElements(ids, null);
    }

    public static String[] getIdElements(List<II> ids, String rootId) {

        final String METHODNAME = "getIdElements ";

        II id = getSingleII(ids);

        return rootId==null ? getIdElements(id) : getIdElements(id, rootId);

    }

    public static String[] getIdElements(II id) {

        final String METHODNAME = "getIdElements ";

        return getIdElements(id, null);

    }

    public static String[] getIdElements(II id, String rootId) {

        final String METHODNAME = "getIdElements ";

        String root = (id!=null && id.getRoot()!=null) ? id.getRoot() : (rootId==null ? UUID.randomUUID().toString() : rootId); ;
        String extension = UUID.randomUUID().toString();

        return new String[] {root, extension};

    }

    public static org.opencds.vmr.v1_0.schema.II getII(String [] rootExtension) {
        return CdsObjectFactory.getII(rootExtension[0], rootExtension[1]);

    }
}
