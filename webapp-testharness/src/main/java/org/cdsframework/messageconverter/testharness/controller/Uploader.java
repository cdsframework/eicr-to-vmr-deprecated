package org.cdsframework.messageconverter.testharness.controller;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.cdsframework.messageconverter.EICRtoVMRConverter;
import org.cdsframework.messageconverter.exception.ConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.util.StringUtils.getFilename;

/**
 * @author HLN Consulting, LLC
 */
@Controller
public class Uploader {

    private static final Logger logger = Logger.getLogger(Uploader.class);
    private static final String CDSINPUT_FILENAME_SUFFIX = "_cdsInput";

    private XPathFactory xpathFactory = XPathFactory.newInstance();

    @Autowired
    private EICRtoVMRConverter eICRtoVMRConverter;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String forward() {
        return "upload";

    }

    @RequestMapping(path = "/randomizeIds", method = RequestMethod.GET)
    public String randomize() {
        return "randomize";

    }

    @RequestMapping(path = "/randomizing", method = RequestMethod.POST)
    public void randomizeIds(HttpServletResponse response, @RequestParam("file") MultipartFile[] files) throws Exception, ConversionException {

        logger.debug("starting randomizeIds()");

        String zipFilename = "randomizedIds" + getTimestamp() + ".zip";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(os);

        ZipEntry ze = new ZipEntry("/" + zipFilename + "/");
        zos.putNextEntry(ze);

        for (MultipartFile file : files) {

            String eicrInputFilename = getFilename(file.getOriginalFilename());
            StringWriter writer = new StringWriter();
            IOUtils.copy(file.getInputStream(), writer, "UTF-8");
            String eicrMessageStr = writer.toString();

            addToZip(eicrInputFilename, "_randomId", idRandomizer(eicrMessageStr), zos);

        }

        zos.close();

        downloadResults(response, zipFilename, os, zos);

    }


    @RequestMapping(path = "/uploading", method = RequestMethod.POST)
    public void uploading(HttpServletResponse response, @RequestParam("file") MultipartFile[] files, Model map) throws IOException, ConversionException {

        logger.debug("starting uploading()");

//        if (files.length==1 && files[0].getSize()==0) {
//            map.addAttribute("msg", "You must upload at least one eICR message");
//            return response.sendError(500, "");
//
//        }

        String zipFilename = "response_" + getTimestamp() + ".zip";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(os);

        ZipEntry ze = new ZipEntry("/" + zipFilename + "/");
        zos.putNextEntry(ze);

        for (MultipartFile file : files) {

            String eicrInputFilename = getFilename(file.getOriginalFilename());
            StringWriter writer = new StringWriter();
            IOUtils.copy(file.getInputStream(), writer, "UTF-8");
            String eicrMessageStr = writer.toString();

            addToZip(eicrInputFilename, CDSINPUT_FILENAME_SUFFIX, eICRtoVMRConverter.convert(eicrMessageStr), zos);

        }

        zos.close();

        downloadResults(response, zipFilename, os, zos);

    }

    private String getTimestamp() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        return sdf.format(new Date());

    }

    private static void addToZip(String filename, String filenameSuffix, String message, ZipOutputStream zos) throws IOException {

        byte[] buffer = new byte[1024];

        ZipEntry ze = new ZipEntry(createOutMessageFilename(filename, filenameSuffix));
        zos.putNextEntry(ze);

        InputStream in = new ByteArrayInputStream(message.getBytes());

        int len;
        while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }

        in.close();
        zos.closeEntry();

    }

    private static String createOutMessageFilename(String filename, String filenameSuffix) {

        if (filename==null || filename.split("\\.").length!=2) {
            logger.error("Filename error");
            return null;

        }

        String [] filenameParts = filename.split("\\.");
        return filenameParts[0] + filenameSuffix + "." + filenameParts[1];

    }

    private void downloadResults(HttpServletResponse response, String zipFilename, ByteArrayOutputStream bos, ZipOutputStream zipOutputStream) throws IOException {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", String.format("inline; filename=" + zipFilename));
        response.setContentLength(bos.size());
        response.getOutputStream().write(bos.toByteArray());

    }

    public String idRandomizer(String message) throws Exception {

        Document eicrDocument = getDocument(message);

        DTMNodeList eicrElementList = getElement(eicrDocument, "id");

        for (int i=0; i<eicrElementList.getLength(); i++) {
            Node idNode = eicrElementList.item(i);

            boolean foundExtension=false;

            for (int j=0; j<idNode.getAttributes().getLength(); j++) {
                if ("root".equals(idNode.getAttributes().item(j).getNodeName())) {
                    idNode.getAttributes().item(j).setNodeValue(randomizeId());

                }

                if ("extension".equals(idNode.getAttributes().item(j).getNodeName())) {
                    idNode.getAttributes().item(j).setNodeValue("EICR-to-VMR TESTING");
                    foundExtension=true;

                }
            }

            if (!foundExtension) {
                ((Element)idNode).setAttribute("extension", "EICR-to-VMR TESTING");

            }

        }

        return xmlToString(eicrDocument);

    }

    private void save(Document eicrDocument, String pathAndFilename) throws Exception {
        File output = new File(pathAndFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        fileOutputStream.write(xmlToString(eicrDocument).getBytes());

    }

    private String randomizeId() {
        UUID uid = UUID.fromString("ec8a6ff8-ed4b-4f7e-82c3-e98e58b45de7");
        return uid.randomUUID().toString();

    }

    public static String xmlToString(Node node) throws TransformerConfigurationException, TransformerException {
        Source source = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);

        return stringWriter.getBuffer().toString();

    }

    private DTMNodeList getElement(Document document, String elementName) throws Exception {
        XPathExpression expr = xpathFactory.newXPath().compile("//" + elementName);
        return (DTMNodeList)expr.evaluate(document, XPathConstants.NODESET);

    }

    private Document getDocument(String docAsString) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);

        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(docAsString.getBytes()));

    }
}
