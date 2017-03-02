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

package com.baidu.hugegraph.graphdb.types.indextype;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.baidu.hugegraph.core.PropertyKey;
import com.baidu.hugegraph.core.schema.HugeGraphSchemaType;
import com.baidu.hugegraph.graphdb.internal.ElementCategory;
import com.baidu.hugegraph.graphdb.types.IndexField;
import com.baidu.hugegraph.graphdb.types.IndexType;
import com.baidu.hugegraph.graphdb.types.SchemaSource;
import com.baidu.hugegraph.graphdb.types.TypeDefinitionCategory;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.Map;

/**
 */
public abstract class IndexTypeWrapper implements IndexType {

    protected final SchemaSource base;

    public IndexTypeWrapper(SchemaSource base) {
        Preconditions.checkNotNull(base);
        this.base = base;
    }

    public SchemaSource getSchemaBase() {
        return base;
    }

    @Override
    public ElementCategory getElement() {
        return base.getDefinition().getValue(TypeDefinitionCategory.ELEMENT_CATEGORY,ElementCategory.class);
    }

    @Override
    public int hashCode() {
        return base.hashCode();
    }

    @Override
    public boolean equals(Object oth) {
        if (this==oth) return true;
        else if (oth==null || !getClass().isInstance(oth)) return false;
        IndexTypeWrapper other = (IndexTypeWrapper)oth;
        return base.equals(other.base);
    }

    @Override
    public String toString() {
        return base.name();
    }

    @Override
    public String getName() {
        return base.name();
    }

    private volatile Map<PropertyKey,IndexField> fieldMap = null;

    @Override
    public IndexField getField(PropertyKey key) {
        Map<PropertyKey,IndexField> result = fieldMap;
        if (result==null) {
            ImmutableMap.Builder<PropertyKey,IndexField> b = ImmutableMap.builder();
            for (IndexField f : getFieldKeys()) b.put(f.getFieldKey(),f);
            result=b.build();
            fieldMap=result;
        }
        assert result!=null;
        return result.get(key);
    }

    private volatile boolean cachedTypeConstraint = false;
    private volatile HugeGraphSchemaType schemaTypeConstraint = null;

    @Override
    public boolean hasSchemaTypeConstraint() {
        return getSchemaTypeConstraint()!=null;
    }

    @Override
    public HugeGraphSchemaType getSchemaTypeConstraint() {
        HugeGraphSchemaType constraint;
        if (!cachedTypeConstraint) {
            Iterable<SchemaSource.Entry> related = base.getRelated(TypeDefinitionCategory.INDEX_SCHEMA_CONSTRAINT, Direction.OUT);
            if (Iterables.isEmpty(related)) {
                constraint=null;
            } else {
                constraint =
                        (HugeGraphSchemaType)Iterables.getOnlyElement(related,null).getSchemaType();
                assert constraint!=null;
            }
            schemaTypeConstraint = constraint;
            cachedTypeConstraint = true;
        } else {
            constraint = schemaTypeConstraint;
        }
        return constraint;
    }

    @Override
    public void resetCache() {
        base.resetCache();
        fieldMap=null;
    }

    @Override
    public boolean indexesKey(PropertyKey key) {
        return getField(key)!=null;
    }

    @Override
    public String getBackingIndexName() {
        return base.getDefinition().getValue(TypeDefinitionCategory.BACKING_INDEX,String.class);
    }

}
