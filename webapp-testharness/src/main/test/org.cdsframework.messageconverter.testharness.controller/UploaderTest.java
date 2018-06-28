package org.cdsframework.messageconverter.testharness.controller;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author HLN Consulting, LLC
 */
public class UploaderTest {

    @Test
    public void downloadResults() throws Exception {

        String [] filenames = {"sampleDocument1.xml", "sampleDocument2.xml"};
        ClassLoader classLoader = getClass().getClassLoader();

        String filename1 = classLoader.getResource(filenames[0]).getFile();
        String content1 = new String(Files.readAllBytes(Paths.get(filename1)));
//
//        String path1 = classLoader.getResource(filenames[0]).getPath().replace(filenames[0], "");
//        String path2 = classLoader.getResource(filenames[1]).getPath().replace(filenames[1], "");
//
//        File file1 = new File(path1 + "/" + filenames[0] + "_output.xml");
//        File file2 = new File(path2 + "/" + filenames[1] + "_output.xml");
//
//        File [] files = new File [] {file1, file2};
//        for (File file : files) {
//            BufferedWriter out = new BufferedWriter(new FileWriter(file));
//            out.write(content1);
//            out.close();
//
//        }

        OutputStream os = new FileOutputStream("myFile.zip");
        ZipOutputStream zos = new ZipOutputStream(os);

        byte[] buffer = new byte[1024];

        for (String filename : filenames) {
            ZipEntry ze= new ZipEntry(filename);
            zos.putNextEntry(ze);

            InputStream in = new ByteArrayInputStream(content1.getBytes());

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

        }

        zos.close();

        System.out.println();

    }
}