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
package org.sakaiproject.oaai.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.oaai.dao.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class FileDownloadController {

    private final Log log = LogFactory.getLog(FileDownloadController.class);

    @RequestMapping(method = RequestMethod.POST)
    public void doDownload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String datedDirectory = request.getParameter("csvDirectory");
        String action = request.getParameter("action");

        String csvData = data.getCsvFile(datedDirectory, action);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename='"+ action + ".csv'");
        try {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(csvData.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch(Exception e) {
            log.error("Error sending CSV file to browser: " + e, e);
        }
    }

    private Data data;
    public void setData(Data data) {
        this.data = data;
    }

}
