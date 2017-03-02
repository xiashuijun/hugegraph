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

package com.baidu.hugegraph.graphdb.types.vertices;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.baidu.hugegraph.core.schema.ConsistencyModifier;
import com.baidu.hugegraph.core.Multiplicity;
import com.baidu.hugegraph.graphdb.internal.Order;
import com.baidu.hugegraph.graphdb.internal.InternalRelationType;
import com.baidu.hugegraph.graphdb.transaction.StandardHugeGraphTx;
import com.baidu.hugegraph.graphdb.types.IndexType;
import com.baidu.hugegraph.graphdb.types.SchemaSource;
import com.baidu.hugegraph.graphdb.types.TypeDefinitionCategory;
import com.baidu.hugegraph.graphdb.types.TypeUtil;
import org.apache.tinkerpop.gremlin.structure.Direction;

import javax.annotation.Nullable;
import java.util.List;

/**
 */
public abstract class RelationTypeVertex extends HugeGraphSchemaVertex implements InternalRelationType {

    public RelationTypeVertex(StandardHugeGraphTx tx, long id, byte lifecycle) {
        super(tx, id, lifecycle);
    }

    @Override
    public long[] getSortKey() {
        return getDefinition().getValue(TypeDefinitionCategory.SORT_KEY, long[].class);
    }

    @Override
    public Order getSortOrder() {
        return getDefinition().getValue(TypeDefinitionCategory.SORT_ORDER, Order.class);
    }

    @Override
    public long[] getSignature() {
        return getDefinition().getValue(TypeDefinitionCategory.SIGNATURE, long[].class);
    }

    @Override
    public boolean isInvisibleType() {
        return getDefinition().getValue(TypeDefinitionCategory.INVISIBLE, Boolean.class);
    }

    @Override
    public Multiplicity multiplicity() {
        return getDefinition().getValue(TypeDefinitionCategory.MULTIPLICITY, Multiplicity.class);
    }

    private ConsistencyModifier consistency = null;

    public ConsistencyModifier getConsistencyModifier() {
        if (consistency==null) {
            consistency = TypeUtil.getConsistencyModifier(this);
        }
        return consistency;
    }

    private Integer ttl = null;

    @Override
    public Integer getTTL() {
        if (null == ttl) {
            ttl = TypeUtil.getTTL(this);
        }
        return ttl;
    }

    public InternalRelationType getBaseType() {
        Entry entry = Iterables.getOnlyElement(getRelated(TypeDefinitionCategory.RELATIONTYPE_INDEX,Direction.IN),null);
        if (entry==null) return null;
        assert entry.getSchemaType() instanceof InternalRelationType;
        return (InternalRelationType)entry.getSchemaType();
    }

    public Iterable<InternalRelationType> getRelationIndexes() {
        return Iterables.concat(ImmutableList.of(this),Iterables.transform(getRelated(TypeDefinitionCategory.RELATIONTYPE_INDEX,Direction.OUT),new Function<Entry, InternalRelationType>() {
            @Nullable
            @Override
            public InternalRelationType apply(@Nullable Entry entry) {
                assert entry.getSchemaType() instanceof InternalRelationType;
                return (InternalRelationType)entry.getSchemaType();
            }
        }));
    }

    private List<IndexType> indexes = null;

    public Iterable<IndexType> getKeyIndexes() {
        List<IndexType> result = indexes;
        if (result==null) {
            ImmutableList.Builder<IndexType> b = ImmutableList.builder();
            for (Entry entry : getRelated(TypeDefinitionCategory.INDEX_FIELD,Direction.IN)) {
                SchemaSource index = entry.getSchemaType();
                b.add(index.asIndexType());
            }
            result = b.build();
            indexes=result;
        }
        assert result!=null;
        return result;
    }

    public void resetCache() {
        super.resetCache();
        indexes=null;
    }
}
