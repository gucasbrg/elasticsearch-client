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

package org.elasticsearch.index.query;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 *
 */
public class ScriptFilterBuilder extends BaseFilterBuilder {

    public static final String NAME = "script";

    private final String script;

    private Map<String, Object> params;

    private String lang;

    private Boolean cache;
    private String cacheKey;

    private String filterName;

    public ScriptFilterBuilder(String script) {
        this.script = script;
    }

    public ScriptFilterBuilder addParam(String name, Object value) {
        if (params == null) {
            params = newHashMap();
        }
        params.put(name, value);
        return this;
    }

    public ScriptFilterBuilder params(Map<String, Object> params) {
        if (this.params == null) {
            this.params = params;
        } else {
            this.params.putAll(params);
        }
        return this;
    }

    /**
     * Sets the script language.
     */
    public ScriptFilterBuilder lang(String lang) {
        this.lang = lang;
        return this;
    }

    /**
     * Sets the filter name for the filter that can be used when searching for matched_filters per hit.
     */
    public ScriptFilterBuilder filterName(String filterName) {
        this.filterName = filterName;
        return this;
    }

    /**
     * Should the filter be cached or not. Defaults to <tt>false</tt>.
     */
    public ScriptFilterBuilder cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public ScriptFilterBuilder cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(NAME);
        builder.field("script", script);
        if (this.params != null) {
            builder.field("params", this.params);
        }
        if (this.lang != null) {
            builder.field("lang", lang);
        }
        if (filterName != null) {
            builder.field("_name", filterName);
        }
        if (cache != null) {
            builder.field("_cache", cache);
        }
        if (cacheKey != null) {
            builder.field("_cache_key", cacheKey);
        }
        builder.endObject();
    }
}