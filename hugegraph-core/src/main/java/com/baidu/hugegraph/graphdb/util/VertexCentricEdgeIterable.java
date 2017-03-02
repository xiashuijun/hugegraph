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

package com.baidu.hugegraph.graphdb.util;

import com.google.common.base.Preconditions;
import com.baidu.hugegraph.core.HugeGraphRelation;
import com.baidu.hugegraph.graphdb.internal.InternalVertex;
import com.baidu.hugegraph.graphdb.internal.RelationCategory;
import org.apache.tinkerpop.gremlin.structure.Direction;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 */

public class VertexCentricEdgeIterable<R extends HugeGraphRelation> implements Iterable<R> {

    private final Iterable<InternalVertex> vertices;
    private final RelationCategory relationCategory;

    public VertexCentricEdgeIterable(final Iterable<InternalVertex> vertices, final RelationCategory relationCategory) {
        Preconditions.checkArgument(vertices!=null && relationCategory!=null);
        this.vertices = vertices;
        this.relationCategory = relationCategory;
    }


    @Override
    public Iterator<R> iterator() {
        return new EdgeIterator();
    }


    private class EdgeIterator implements Iterator<R> {

        private final Iterator<InternalVertex> vertexIter;
        private Iterator<HugeGraphRelation> currentOutEdges;
        private HugeGraphRelation nextEdge = null;

        public EdgeIterator() {
            this.vertexIter = vertices.iterator();
            if (vertexIter.hasNext()) {
                currentOutEdges = relationCategory.executeQuery(vertexIter.next().query().direction(Direction.OUT)).iterator();
                getNextEdge();
            }
        }

        private void getNextEdge() {
            assert vertexIter != null && currentOutEdges != null;
            nextEdge = null;
            while (nextEdge == null) {
                if (currentOutEdges.hasNext()) {
                    nextEdge = currentOutEdges.next();
                    break;
                } else if (vertexIter.hasNext()) {
                    currentOutEdges = relationCategory.executeQuery(vertexIter.next().query().direction(Direction.OUT)).iterator();
                } else break;
            }
        }

        @Override
        public boolean hasNext() {
            return nextEdge != null;
        }

        @Override
        public R next() {
            if (nextEdge == null) throw new NoSuchElementException();
            HugeGraphRelation returnEdge = nextEdge;
            getNextEdge();
            return (R)returnEdge;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
