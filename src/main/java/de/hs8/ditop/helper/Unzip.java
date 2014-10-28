package de.hs8.ditop.helper;

/**
 * Created by hen on 10/28/14.
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {
    final static int BUFFER = 2048;

    final public static void unzipFile(final String fileName, final String path) {
        try {
            BufferedOutputStream dest = null;
            final FileInputStream fis = new FileInputStream(fileName);
            final ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(fis));
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
//                System.out.println("Extracting: " + entry);
                if (entry.getName().contains("__MACOSX"))
                    continue;
                if (entry.isDirectory()) {
                    System.out.println("is dir");
                    final File dir = new File(path + entry.getName());
                    dir.mkdirs();
                } else {

                    int count;
                    final byte data[] = new byte[BUFFER];
                    // write the files to the disk

                    final FileOutputStream fos = new FileOutputStream(path
                            + entry.getName());
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}