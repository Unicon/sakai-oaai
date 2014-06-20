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

package org.sakaiproject.oaai.provider;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.oaai.dao.Data;
import org.sakaiproject.oaai.service.OaaiService;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class OaaiDataExtractorProvider extends AbstractEntityProvider implements EntityProvider, Outputable, Describeable, ActionsExecutable {

    private final Log log = LogFactory.getLog(OaaiDataExtractorProvider.class);

    public static String PREFIX = "sakai-oaai";
    public String getEntityPrefix() {
        return PREFIX;
    }

    private static String ENCODING_UTF8 = "UTF-8";
    private static String MIMETYPE_CSV = "text/csv";
    
    public void init() {
        log.info("INIT Sakai OAAI Data Extractor");
    }

    /**
     * GET /direct/sakai-oaai/courses
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="courses", viewKey=EntityView.VIEW_LIST)
    public ActionReturn coursesCsv(EntityView view) {
        if (!oaaiService.isAdminSession()){
            throw new SecurityException("User not allowed to access courses .csv.", null);
        }

        return new ActionReturn(ENCODING_UTF8, Formats.JSON, "{\"courses\": \"course1\"}");
    }

    /**
     * GET /direct/sakai-oaai/grades
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="grades", viewKey=EntityView.VIEW_LIST)
    public ActionReturn gradesCsv(EntityView view) {
        if (!oaaiService.isAdminSession()){
            throw new SecurityException("User not allowed to access grades .csv.", null);
        }

        return new ActionReturn(ENCODING_UTF8, Formats.JSON, "{\"grades\": \"grade1\"}");
    }

    /**
     * GET /direct/sakai-oaai/students
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="students", viewKey=EntityView.VIEW_LIST)
    public ActionReturn studentsCsv(EntityView view) {
        if (!oaaiService.isAdminSession()){
            throw new SecurityException("User not allowed to access students .csv.", null);
        }

        return new ActionReturn(ENCODING_UTF8, Formats.JSON, "{\"students\": \"students1\"}");
    }

    /**
     * GET /direct/sakai-oaai/usage
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="usage", viewKey=EntityView.VIEW_LIST)
    public ActionReturn usageCsv(EntityView view) {
        if (!oaaiService.isAdminSession()){
            throw new SecurityException("User not allowed to access usage .csv.", null);
        }

        return new ActionReturn(ENCODING_UTF8, Formats.TXT, "");
    }

    /**
     * POST /direct/sakai-oaai/generate
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="generate", viewKey=EntityView.VIEW_NEW)
    public ActionReturn generateNewData(EntityView view) {
        if (!oaaiService.isAdminSession()){
            throw new SecurityException("User not allowed to generate new CSVs.", null);
        }

        String searchTerm = view.getPathSegment(2);
        if (searchTerm == null) {
            searchTerm = "";
        }

        String directory = oaaiService.createDatedDirectoryName();

        boolean usageCsvCreated = data.prepareUsageCsv(searchTerm, directory);

        return new ActionReturn(ENCODING_UTF8, Formats.TXT, "Usage CSV created successfully: " + Boolean.toString(usageCsvCreated));
    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.JSON};
    }

    public String[] getHandledInputFormats() {
        return new String[] {Formats.JSON, "form"};
    }

    private OaaiService oaaiService;
    public void setOaaiService(OaaiService oaaiService) {
        this.oaaiService = oaaiService;
    }

    private Data data;
    public void setData(Data data) {
        this.data = data;
    }
}
