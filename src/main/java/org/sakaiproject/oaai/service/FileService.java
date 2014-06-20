/**
 * Copyright 2008 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.sakaiproject.oaai.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;

public class FileService {

    private final Log log = LogFactory.getLog(FileService.class);

    private static String DEFAULT_CSV_STORAGE_DIRECTORY = "oaai/";

    private String createCsvStoragePath() {
        String rootDirectory = ServerConfigurationService.getString("bodyPath@org.sakaiproject.content.api.ContentHostingService", "");
        rootDirectory = addTrailingSlash(rootDirectory);

        String csvDirectory = ServerConfigurationService.getString("oaai.storage.directory", DEFAULT_CSV_STORAGE_DIRECTORY);
        csvDirectory = addTrailingSlash(csvDirectory);

        return rootDirectory + csvDirectory;
    }

    private void createNewRootDirectory() {
        File newDirectory = new File(createCsvStoragePath());

        createDirectory(newDirectory);
    }

    private String createNewCsvDirectory(String directoryName) {
        // create root directory, if needed
        createNewRootDirectory();

        File newDirectory = new File(createCsvStoragePath() + directoryName);

        createDirectory(newDirectory);

        String path = newDirectory.getPath();

        return path;
    }

    private void createDirectory(File newDirectory) {
        // if the directory does not exist, create it
        if (!newDirectory.exists()) {
            try{
                newDirectory.mkdir();
            } catch(Exception e){
                log.error("Cannot create new directory: " + e, e);
            }
        }
    }

    public File createNewFile(String datedDirectory, String name) {
        if (StringUtils.isBlank(datedDirectory)) {
            throw new NullArgumentException("File directory cannot be null or blank");
        }
        if (StringUtils.isBlank(name)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        File newFile = null;

        String csvDirectory = createNewCsvDirectory(datedDirectory);
        csvDirectory = addTrailingSlash(csvDirectory);

        try {
            newFile = new File(csvDirectory + name);
            newFile.createNewFile();
        } catch (Exception e) {
            log.error("Error creating file: " + e, e);
        }

        return newFile;
    }

    public boolean writeToCsvFile(File csvFile, String csvData) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
            bufferedWriter.write(csvData);
            bufferedWriter.close();

            return true;
        } catch (Exception e) {
            log.error("Error writing to CSV file: " + e, e);

            return false;
        }
    }

    public List<String> parseCsvDirectory() {
        List<String> csvFolders = new ArrayList<String>();
        File csvDirectory = new File(createCsvStoragePath());

        for (File subDirectory : csvDirectory.listFiles()) {
            if (subDirectory.isDirectory()) {
                csvFolders.add(subDirectory.getName());
            }
        }

        return csvFolders;
    }

    public File getFile(String datedDirectory, String fileName) {
        if (StringUtils.isBlank(datedDirectory)) {
            throw new NullArgumentException("File directory cannot be null or blank");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        datedDirectory = addTrailingSlash(datedDirectory);

        File file = new File(createCsvStoragePath() + datedDirectory + fileName);

        return file;
    }

    public String readFileIntoString(String datedDirectory, String fileName) {
        String fileString = "";

        try {
            File file = getFile(datedDirectory, fileName);

            InputStream inputStream = new FileInputStream(file);
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            fileString = writer.toString();
        } catch (Exception e) {
            log.error("Error reading file into string: " + e, e);
        }

        return fileString;
    }

    private String addTrailingSlash(String path) {
        if (!StringUtils.endsWith(path, "/")) {
            path += "/";
        }

        return path;
    }

}
