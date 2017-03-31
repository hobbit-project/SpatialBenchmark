/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hobbit.spatialbenchmark.platformConnection.DataGenerator;

public class FileUtil {

    /**
     * A recursive method for collecting a list of all files with a given file
     * extension. Will scan all sub-folders recursively.
     *
     * @param startFolder - where to start from
     * @param collectedFilesList - collected list of <File>
     * @param fileExtFilter
     * @param recurseFolders
     * @throws java.io.IOException
     */
    public static void collectFilesList(String startFolder, List<File> collectedFilesList, String fileExtFilter, boolean recurseFolders) throws IOException {
        File file = new File(startFolder);
        File[] filesList = file.listFiles();

        for (File f : filesList) {
            if (f.isDirectory() && recurseFolders) {
                collectFilesList(f.getAbsolutePath(), collectedFilesList, fileExtFilter, recurseFolders);
            } else //no filter
            {
                if (fileExtFilter.isEmpty() || fileExtFilter.equals("*")) {
                    collectedFilesList.add(f);
                } else if (fileExtFilter.equalsIgnoreCase(getFileExtension(f))) {
                    collectedFilesList.add(f);
                }
            }
        }
    }

    private static String getFileExtension(File f) {
        String fileName = f.getName();
        String fileExtension = fileName;

        int lastPos = fileName.lastIndexOf('.');

        if (lastPos > 0 && lastPos < (fileName.length() - 1)) {
            fileExtension = fileName.substring(lastPos + 1).toLowerCase();
        }

        return fileExtension;
    }

    public static byte[] loadByteData(String datasetsFolder) {
        File file = new File(datasetsFolder);
        File[] filesList = file.listFiles();
        for (File f : filesList) {
            try {
                byte[] data = Files.readAllBytes(f.toPath());
                return data;
            } catch (IOException ex) {
                Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }
}
