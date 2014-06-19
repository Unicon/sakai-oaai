/*
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

$(document).ready(function() {
    var url = "/direct/sakai-oaai/";

    $(".csv").click(function(){
        if (this.id != "generate") {
            getData(this.id, function(data) {
                outputResponse(data);
            });
        } else {
            generateNewData(this.id, function(data) {
                outputResponse(data);
            });
        }
    });

    function getData(endpoint, callback) {
        var request = $.ajax({ 
            type: "GET",
            url:  url + endpoint,
            data: {}
         });

        request.success(function(data, status, jqXHR) {
            callback(data);
        });

        request.error(function(jqXHR, textStatus, errorThrown) {
            callback("Request failed: " + textStatus + ", error : " + errorThrown);
        });
    }

    function generateNewData(endpoint, callback) {
        var request = $.ajax({ 
            type: "POST",
            url:  url + endpoint
         });

        request.success(function(data, status, jqXHR) {
            callback(data);
        });

        request.error(function(jqXHR, textStatus, errorThrown) {
            callback("Request failed: " + textStatus + ", error : " + errorThrown);
        });
    }

    // temp function for testing
    function outputResponse(data) {
        $("#jsonResponse").html("Data: <br /><br>" + JSON.stringify(data, null, 2));
    }
});