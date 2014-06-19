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
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;

public class FileService {

    private final Log log = LogFactory.getLog(FileService.class);

    private static String DEFAULT_CSV_STORAGE_DIRECTORY = "oaai/";

    private String getRootStorageDirectory() {
        String rootDir = ServerConfigurationService.getString("bodyPath@org.sakaiproject.content.api.ContentHostingService", "");
        rootDir = addTrailingSlash(rootDir);

        return rootDir;
    }

    private String getCsvStorageDirectory() {
        String csvDir = ServerConfigurationService.getString("oaai.storage.directory", DEFAULT_CSV_STORAGE_DIRECTORY);
        csvDir = addTrailingSlash(csvDir);

        return csvDir;
    }

    private String createCsvStoragePath() {
        String rootDirectory = getRootStorageDirectory();
        String csvDirectory = getCsvStorageDirectory();

        return rootDirectory + csvDirectory;
    }

    private void createNewRootDirectory() {
        File theDir = new File(createCsvStoragePath());

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            try{
                theDir.mkdir();
            } catch(Exception e){
                log.error("Cannot create new directory: " + e, e);
            }
        }
    }

    /**
     * Creates a new directory with the current date and time as the name
     * @return the path to the directory
     */
    public String createNewCsvDirectory() {
        String directoryName = oaaiService.createDatedDirectoryName();
        return createNewCsvDirectory(directoryName);
    }

    public String createNewCsvDirectory(String directoryName) {
        createNewRootDirectory();

        File theDir = new File(createCsvStoragePath() + directoryName);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            try{
                theDir.mkdir();
            } catch(Exception e){
                log.error("Cannot create new directory: " + e, e);
            }
        }

        String path = theDir.getPath();

        return path;
    }

    public File createNewFile(String name) {
        String directory = createNewCsvDirectory();

        return createNewFile(directory, name);
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

    private String addTrailingSlash(String path) {
        if (!StringUtils.endsWith(path, "/")) {
            path += "/";
        }

        return path;
    }

    private OaaiService oaaiService;
    public void setOaaiService(OaaiService oaaiService) {
        this.oaaiService = oaaiService;
    }

}
