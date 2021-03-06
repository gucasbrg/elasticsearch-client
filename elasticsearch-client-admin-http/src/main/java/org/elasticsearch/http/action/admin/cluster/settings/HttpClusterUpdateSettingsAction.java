/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.http.action.admin.cluster.settings;

import org.elasticsearch.ElasticSearchGenerationException;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse;
import org.elasticsearch.action.support.HttpAction;
import org.elasticsearch.action.support.HttpRequest;
import org.elasticsearch.action.support.HttpResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.XContentHelper;

import java.io.IOException;
import java.util.Map;

public class HttpClusterUpdateSettingsAction extends HttpAction<ClusterUpdateSettingsRequest, ClusterUpdateSettingsResponse> {

    public static final String NAME = "cluster_update_settings";
    private static final String METHOD = "PUT";
    private static final String ENDPOINT = "_cluster/settings";

    @Override
    protected HttpRequest toRequest(ClusterUpdateSettingsRequest request) throws IOException {
        HttpRequest httpRequest = new HttpRequest(METHOD, ENDPOINT);
        if (request.persistentSettings() != null) {
            httpRequest.body(toBody(request.persistentSettings(), "persistent"));
        }
        if (request.transientSettings() != null) {
            httpRequest.body(toBody(request.transientSettings(), "transient"));
        }
        return httpRequest;
    }

    @Override
    protected ClusterUpdateSettingsResponse toResponse(HttpResponse response) throws IOException {
        Map<String, Object> map = XContentHelper.convertToMap(response.getBody(), false).v2();
        logger.info("response = {}", map);
        return null;
    }

    private String toBody(Settings settings, String mode) throws IOException {
        XContentBuilder builder = XContentFactory.contentBuilder(XContentType.JSON);
        builder.startObject().startObject(mode);
        for (Map.Entry<String, String> me : settings.getAsMap().entrySet()) {
            builder.field(me.getKey(), me.getValue());
        }
        builder.endObject().endObject();
        return builder.string();
    }
}
