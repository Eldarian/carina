/*******************************************************************************
 * Copyright 2020-2022 Zebrunner Inc (https://www.zebrunner.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/package com.qaprosoft.carina.core.foundation.jenkins;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.path.xml.XmlPath;

public class JenkinsClient {
    private static final Logger LOGGER = Logger.getLogger(JenkinsClient.class);

    private static final String JOB = "%s/job/%s/%s/console";
    private static final String JOB_API = "%s/job/%s/api/xml?depth=1";

    private String jenkinsURL;

    public JenkinsClient(String jenkinsURL) {
        setJenkinsURL(jenkinsURL);
    }

    public String getCurrentJobURL(String job) {
        String url = null;
        try {
            URL obj = new URL(String.format(JOB_API, jenkinsURL, job));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            try {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } catch (Exception e) {
                LOGGER.debug("Error during FileWriter append.", e;
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    LOGGER.debug("Error during FileWriter close.", e);
                }

            }

            XmlPath xmlPath = new XmlPath(response.toString());
            if (xmlPath.getBoolean("freeStyleProject.lastBuild.building")) {
                url = String.format(JOB, jenkinsURL, job, xmlPath.getString("freeStyleProject.lastBuild.number").trim());
            }
        } catch (Exception e) {
            url = "";
            LOGGER.error(e.getMessage());
        }
        return url;
    }

    public String getJenkinsURL() {
        return jenkinsURL;
    }

    public void setJenkinsURL(String jenkinsURL) {
        this.jenkinsURL = !StringUtils.isEmpty(jenkinsURL) ? StringUtils.removeEnd(jenkinsURL, "/") : jenkinsURL;
    }
}
