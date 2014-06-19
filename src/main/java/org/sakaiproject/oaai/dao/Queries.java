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

public class Queries {

    public String getSqlEvents() {
        String sql = "SELECT " +
                "ss.TITLE," +
                "sn.SESSION_USER," +
                "sn.SESSION_START," +
                "sn.SESSION_END," +
                "se.EVENT_ID," +
                "se.EVENT_DATE," +
                "se.EVENT," +
                "se.REF," +
                "COALESCE(" +
                    "se.CONTEXT," +
                    "SUBSTR(se.REF, LOCATE('/',se.REF, 2) + 1, INSTR(se.REF,'-p') - LOCATE('/',se.REF, 2) - 1)" +
                ") CONTEXT," +
                "se.SESSION_ID," +
                "se.EVENT_CODE " +
            "FROM " +
                "SAKAI_EVENT se " +
            "LEFT JOIN " +
                "SAKAI_SESSION sn " +
                    "ON se.SESSION_ID = sn.SESSION_ID " +
            "LEFT JOIN " +
                "SAKAI_SITE ss " +
                    "ON se.CONTEXT = ss.SITE_ID " +
            "WHERE " +
                "ss.TITLE LIKE ?";

        return sql;
    }

}
