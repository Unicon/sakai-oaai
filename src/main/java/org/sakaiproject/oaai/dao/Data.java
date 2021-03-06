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
package org.sakaiproject.oaai.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.oaai.Constants;
import org.sakaiproject.oaai.service.CsvService;
import org.sakaiproject.oaai.service.FileService;

public class Data extends Db {

    private final Log log = LogFactory.getLog(Data.class);

    /**
     * Create the usage.csv file, store it on file system
     * 
     * @param searchTerm the site title search term to use
     * @param directory the name of the date-specific directory to store the .csv file
     * @return true, if creation and storage successful
     */
    public boolean prepareUsageCsv(String searchTerm, String directory) {
        String csvData = "";
        String sql = queries.getSqlEvents();
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            preparedStatement = createPreparedStatement(preparedStatement, sql);
            preparedStatement.setString(1, "%" + searchTerm + "%");

            ResultSet results = executePreparedStatement(preparedStatement);

            ResultSetMetaData metadata = results.getMetaData();
            int numberOfColumns = metadata.getColumnCount();

            // header row
            List<String> header = new ArrayList<String>();
            for (int i = 1;i <=numberOfColumns;i++) {
                header.add(metadata.getColumnName(i));
            }
            csvData += csvService.setAsCsvRow(header);

            // data rows
            while (results.next()) {
                List<String> row = new ArrayList<String>();
                for (int i = 1;i <=numberOfColumns;i++) {
                    row.add(results.getString(i));
                }
                csvData += csvService.setAsCsvRow(row);
            }

            success = fileService.saveStringToFile(csvData, directory, Constants.CSV_FILE_USAGE);
        } catch (Exception e) {
            log.error("Error preparing usage csv: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return success;
    }

    public String getCsvData(String datedDirectory, String fileName) {
        String csvData = fileService.readFileIntoString(datedDirectory, fileName);

        return csvData;
    }

    public Map<String, String> getDirectoryListing() {
        List<String> directoryNames = fileService.parseDirectory();
        Map<String, String> directories = new HashMap<String, String>(directoryNames.size());
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_FILE_NAME, Locale.ENGLISH);
        SimpleDateFormat dropdownDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DROPDOWN, Locale.ENGLISH);

        for (String directoryName : directoryNames) {
            try {
                Date date = fileNameDateFormat.parse(directoryName);
                String formattedDate = dropdownDateFormat.format(date);
                directories.put(directoryName, formattedDate);
            } catch (Exception e) {
                log.error("Error parsing directory name: " + e, e);
                directories.put(directoryName, directoryName);
            }
        }

        return directories;
    }

    private CsvService csvService;
    public void setCsvService(CsvService csvService) {
        this.csvService = csvService;
    }

    private FileService fileService;
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private Queries queries;
    public void setQueries(Queries queries) {
        this.queries = queries;
    }

}
