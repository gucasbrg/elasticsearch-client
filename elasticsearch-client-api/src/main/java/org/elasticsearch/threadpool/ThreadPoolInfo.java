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
package org.elasticsearch.threadpool;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.unit.SizeValue;
import org.elasticsearch.common.unit.TimeValue;

/**
 */
public class ThreadPoolInfo implements Streamable, Iterable<ThreadPoolInfo.Info>, ToXContent {

    private List<Info> infos;

    ThreadPoolInfo() {
    }

    public ThreadPoolInfo(List<Info> infos) {
        this.infos = infos;
    }

    @Override
    public Iterator<Info> iterator() {
        return infos.iterator();
    }

    public static ThreadPoolInfo readThreadPoolInfo(StreamInput in) throws IOException {
        ThreadPoolInfo info = new ThreadPoolInfo();
        info.readFrom(in);
        return info;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        int size = in.readVInt();
        infos = new ArrayList<Info>(size);
        for (int i = 0; i < size; i++) {
            Info info = new Info();
            info.readFrom(in);
            infos.add(info);
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeVInt(infos.size());
        for (Info info : infos) {
            info.writeTo(out);
        }
    }

    static final class Fields {

        static final XContentBuilderString THREAD_POOL = new XContentBuilderString("thread_pool");
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(Fields.THREAD_POOL);
        for (Info info : infos) {
            info.toXContent(builder, params);
        }
        builder.endObject();
        return builder;
    }

    public static class Info implements Streamable, ToXContent {

        private String name;
        private String type;
        private int min;
        private int max;
        private TimeValue keepAlive;
        private SizeValue capacity;

        Info() {
        }

        public Info(String name, String type) {
            this(name, type, -1);
        }

        public Info(String name, String type, int size) {
            this(name, type, size, size, null, null);
        }

        public Info(String name, String type, int min, int max, @Nullable TimeValue keepAlive, @Nullable SizeValue capacity) {
            this.name = name;
            this.type = type;
            this.min = min;
            this.max = max;
            this.keepAlive = keepAlive;
            this.capacity = capacity;
        }

        public String name() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public String type() {
            return this.type;
        }

        public String getType() {
            return this.type;
        }

        public int min() {
            return this.min;
        }

        public int getMin() {
            return this.min;
        }

        public int max() {
            return this.max;
        }

        public int getMax() {
            return this.max;
        }

        @Nullable
        public TimeValue keepAlive() {
            return this.keepAlive;
        }

        @Nullable
        public TimeValue getKeepAlive() {
            return this.keepAlive;
        }

        @Nullable
        public SizeValue capacity() {
            return this.capacity;
        }

        @Nullable
        public SizeValue getCapacity() {
            return this.capacity;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            name = in.readString();
            type = in.readString();
            min = in.readInt();
            max = in.readInt();
            if (in.readBoolean()) {
                keepAlive = TimeValue.readTimeValue(in);
            }
            if (in.readBoolean()) {
                capacity = SizeValue.readSizeValue(in);
            }
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            out.writeString(name);
            out.writeString(type);
            out.writeInt(min);
            out.writeInt(max);
            if (keepAlive == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                keepAlive.writeTo(out);
            }
            if (capacity == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                capacity.writeTo(out);
            }
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject(name, XContentBuilder.FieldCaseConversion.NONE);
            builder.field(Fields.TYPE, type);
            if (min != -1) {
                builder.field(Fields.MIN, min);
            }
            if (max != -1) {
                builder.field(Fields.MAX, max);
            }
            if (keepAlive != null) {
                builder.field(Fields.KEEP_ALIVE, keepAlive.toString());
            }
            if (capacity != null) {
                builder.field(Fields.CAPACITY, capacity.toString());
            }
            builder.endObject();
            return builder;
        }

        static final class Fields {

            static final XContentBuilderString TYPE = new XContentBuilderString("type");
            static final XContentBuilderString MIN = new XContentBuilderString("min");
            static final XContentBuilderString MAX = new XContentBuilderString("max");
            static final XContentBuilderString KEEP_ALIVE = new XContentBuilderString("keep_alive");
            static final XContentBuilderString CAPACITY = new XContentBuilderString("capacity");
        }
    }
}
