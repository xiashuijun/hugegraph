// Copyright 2017 HugeGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.baidu.hugegraph.graphdb.internal;

import com.google.common.collect.ImmutableList;
import com.baidu.hugegraph.core.HugeGraphRelation;
import com.baidu.hugegraph.core.HugeGraphVertexQuery;
import com.baidu.hugegraph.graphdb.query.condition.Condition;

/**
*/
public enum RelationCategory implements Condition<HugeGraphRelation> {

    EDGE, PROPERTY, RELATION;

    public boolean isProper() {
        switch(this) {
            case EDGE:
            case PROPERTY:
                return true;
            case RELATION:
                return false;
            default: throw new AssertionError("Unrecognized type: " + this);
        }
    }

    public Iterable<HugeGraphRelation> executeQuery(HugeGraphVertexQuery query) {
        switch (this) {
            case EDGE: return (Iterable)query.edges();
            case PROPERTY: return (Iterable)query.properties();
            case RELATION: return query.relations();
            default: throw new AssertionError();
        }
    }

    /*
    ########### CONDITION DEFINITION #################
     */

    @Override
    public Type getType() {
        return Type.LITERAL;
    }

    @Override
    public Iterable getChildren() {
        return ImmutableList.of();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int numChildren() {
        return 0;
    }

    @Override
    public boolean evaluate(HugeGraphRelation relation) {
        switch(this) {
            case EDGE:
                return relation.isEdge();
            case PROPERTY:
                return relation.isProperty();
            case RELATION:
                return true;
            default: throw new AssertionError("Unrecognized type: " + this);
        }
    }
}
