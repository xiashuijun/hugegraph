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

package com.baidu.hugegraph.graphdb.serializer;

import com.google.common.collect.Iterators;
import com.baidu.hugegraph.core.*;
import com.baidu.hugegraph.core.schema.HugeGraphManagement;
import com.baidu.hugegraph.diskstorage.configuration.ModifiableConfiguration;
import com.baidu.hugegraph.graphdb.configuration.GraphDatabaseConfiguration;
import com.baidu.hugegraph.graphdb.database.StandardHugeGraph;
import com.baidu.hugegraph.graphdb.serializer.attributes.*;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.*;

import static org.junit.Assert.*;

/**
 */
public class SerializerGraphConfiguration {

    StandardHugeGraph graph;

    @Before
    public void initialize() {
        ModifiableConfiguration config = GraphDatabaseConfiguration.buildGraphConfiguration();
        config.set(GraphDatabaseConfiguration.STORAGE_BACKEND,"inmemory");
        config.set(GraphDatabaseConfiguration.CUSTOM_ATTRIBUTE_CLASS, TClass1.class.getName(), "attribute1");
        config.set(GraphDatabaseConfiguration.CUSTOM_SERIALIZER_CLASS, TClass1Serializer.class.getName(), "attribute1");
        config.set(GraphDatabaseConfiguration.CUSTOM_ATTRIBUTE_CLASS, TEnum.class.getName(), "attribute4");
        config.set(GraphDatabaseConfiguration.CUSTOM_SERIALIZER_CLASS, TEnumSerializer.class.getName(), "attribute4");
        graph = (StandardHugeGraph) HugeGraphFactory.open(config);
    }

    @After
    public void shutdown() {
        graph.close();
    }

    @Test
    public void testOnlyRegisteredSerialization() {
        HugeGraphManagement mgmt = graph.openManagement();
        PropertyKey time = mgmt.makePropertyKey("time").dataType(Integer.class).make();
        mgmt.makePropertyKey("any").cardinality(Cardinality.LIST).dataType(Object.class).make();
        mgmt.buildIndex("byTime",Vertex.class).addKey(time).buildCompositeIndex();
        mgmt.makeEdgeLabel("knows").make();
        mgmt.makeVertexLabel("person").make();
        mgmt.commit();

        HugeGraphTransaction tx = graph.newTransaction();
        HugeGraphVertex v = tx.addVertex("person");
        v.property("time", 5);
        v.property("any", new Double(5.0));
        v.property("any", new TClass1(5,1.5f));
        v.property("any", TEnum.THREE);
        tx.commit();

        tx = graph.newTransaction();
        v = (HugeGraphVertex) tx.query().has("time",5).vertices().iterator().next();
        assertEquals(5,(int)v.value("time"));
        assertEquals(3, Iterators.size(v.properties("any")));
        tx.rollback();

        //Verify that non-registered objects aren't allowed
        for (Object o : new Object[]{new TClass2("abc",5)}) {
            tx = graph.newTransaction();
            v = tx.addVertex("person");
            try {
                v.property("any", o); //Should not be allowed
                tx.commit();
                fail();
            } catch (IllegalArgumentException e) {
            } finally {
                if (tx.isOpen()) tx.rollback();
            }

        }
    }


}
