/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.baidu.hugegraph.HugeGraph;
import com.baidu.hugegraph.backend.id.Id;
import com.baidu.hugegraph.backend.id.IdGenerator;
import com.baidu.hugegraph.exception.NotSupportException;
import com.baidu.hugegraph.schema.builder.SchemaBuilder;
import com.baidu.hugegraph.type.HugeType;
import com.baidu.hugegraph.type.define.IndexType;
import com.baidu.hugegraph.util.E;

public class IndexLabel extends SchemaElement {

    private HugeType baseType;
    private Id baseValue;
    private IndexType indexType;
    private List<Id> indexFields;

    public IndexLabel(final HugeGraph graph, Id id, String name) {
        super(graph, id, name);
        this.indexType = IndexType.SECONDARY;
        this.indexFields = new ArrayList<>();
    }

    protected IndexLabel(long id, String name) {
        this(null, IdGenerator.of(id), name);
    }

    @Override
    public HugeType type() {
        return HugeType.INDEX_LABEL;
    }

    public HugeType baseType() {
        return this.baseType;
    }

    public void baseType(HugeType baseType) {
        this.baseType = baseType;
    }

    public Id baseValue() {
        return this.baseValue;
    }

    public void baseValue(Id id) {
        this.baseValue = id;
    }

    public IndexType indexType() {
        return this.indexType;
    }

    public void indexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public HugeType queryType() {
        switch (this.baseType) {
            case VERTEX_LABEL:
                return HugeType.VERTEX;
            case EDGE_LABEL:
                return HugeType.EDGE;
            default:
                throw new AssertionError(String.format(
                          "Query type of index label is either '%s' or '%s', " +
                          "but '%s' is used",
                          HugeType.VERTEX_LABEL, HugeType.EDGE_LABEL,
                          this.baseType));
        }
    }

    public List<Id> indexFields() {
        return Collections.unmodifiableList(this.indexFields);
    }

    public void indexFields(Id... ids) {
        this.indexFields.addAll(Arrays.asList(ids));
    }

    public void indexField(Id id) {
        this.indexFields.add(id);
    }

    public Id indexField() {
        E.checkState(this.indexType == IndexType.RANGE,
                     "Only support indexField() for range index label");
        E.checkState(this.indexFields.size() == 1,
                     "Range index label only has one index field, but got: " +
                     "%s", this.indexFields);
        return this.indexFields.get(0);
    }

    @Override
    public Map<String, Object> userdata() {
        throw new NotSupportException("user data for index label");
    }

    @Override
    public void userdata(String key, Object value) {
        throw new NotSupportException("user data for index label");
    }

    public static final IndexLabel VL_IL = new IndexLabel(-1, "~vli");
    public static final IndexLabel EL_IL = new IndexLabel(-2, "~eli");

    public static final IndexLabel PK_NAME_IL = new IndexLabel(-3, "~pkni");
    public static final IndexLabel VL_NAME_IL = new IndexLabel(-4, "~vlni");
    public static final IndexLabel EL_NAME_IL = new IndexLabel(-5, "~elni");
    public static final IndexLabel IL_NAME_IL = new IndexLabel(-6, "~ilni");

    public static IndexLabel label(HugeType type) {
        switch (type) {
            case VERTEX:
                return VL_IL;
            case EDGE:
            case EDGE_OUT:
            case EDGE_IN:
                return EL_IL;
            case PROPERTY_KEY:
                return PK_NAME_IL;
            case VERTEX_LABEL:
                return VL_NAME_IL;
            case EDGE_LABEL:
                return EL_NAME_IL;
            case INDEX_LABEL:
                return IL_NAME_IL;
            default:
                throw new AssertionError(String.format(
                          "No primitive index label for '%s'", type));
        }
    }

    public static IndexLabel label(HugeGraph graph, Id id) {
        // Primitive IndexLabel first
        if (id.asLong() < 0) {
            switch ((int) id.asLong()) {
                case -1:
                    return VL_IL;
                case -2:
                    return EL_IL;
                case -3:
                    return PK_NAME_IL;
                case -4:
                    return VL_NAME_IL;
                case -5:
                    return EL_NAME_IL;
                case -6:
                    return IL_NAME_IL;
                default:
                    throw new AssertionError(String.format(
                              "No primitive index label for '%s'", id));
            }
        }
        return graph.indexLabel(id);
    }

    public interface Builder extends SchemaBuilder<IndexLabel> {

        void rebuild();

        Builder onV(String baseValue);

        Builder onE(String baseValue);

        Builder by(String... fields);

        Builder secondary();

        Builder range();

        Builder search();

        Builder on(HugeType baseType, String baseValue);

        Builder indexType(IndexType indexType);
    }
}
