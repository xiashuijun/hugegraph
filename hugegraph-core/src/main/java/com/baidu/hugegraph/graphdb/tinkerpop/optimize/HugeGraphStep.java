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

package com.baidu.hugegraph.graphdb.tinkerpop.optimize;

import com.google.common.collect.Iterables;
import com.baidu.hugegraph.core.HugeGraphQuery;
import com.baidu.hugegraph.core.HugeGraphTransaction;
import com.baidu.hugegraph.graphdb.query.BaseQuery;
import com.baidu.hugegraph.graphdb.query.HugeGraphPredicate;
import com.baidu.hugegraph.graphdb.query.graph.GraphCentricQueryBuilder;
import com.baidu.hugegraph.graphdb.query.profile.QueryProfiler;
import com.baidu.hugegraph.graphdb.tinkerpop.profile.TP3ProfileWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.Profiling;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.process.traversal.util.MutableMetrics;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class HugeGraphStep<S, E extends Element> extends GraphStep<S, E> implements HasStepFolder<S, E>, Profiling, HasContainerHolder {

    private final List<HasContainer> hasContainers = new ArrayList<>();
    private int limit = BaseQuery.NO_LIMIT;
    private List<OrderEntry> orders = new ArrayList<>();
    private QueryProfiler queryProfiler = QueryProfiler.NO_OP;


    public HugeGraphStep(final GraphStep<S, E> originalStep) {
        super(originalStep.getTraversal(), originalStep.getReturnClass(), originalStep.isStartStep(), originalStep.getIds());
        originalStep.getLabels().forEach(this::addLabel);
        this.setIteratorSupplier(() -> {
            HugeGraphTransaction tx = HugeGraphTraversalUtil.getTx(traversal);
            HugeGraphQuery query = tx.query();
            for (HasContainer condition : hasContainers) {
                query.has(condition.getKey(), HugeGraphPredicate.Converter.convert(condition.getBiPredicate()), condition.getValue());
            }
            for (OrderEntry order : orders) query.orderBy(order.key, order.order);
            if (limit != BaseQuery.NO_LIMIT) query.limit(limit);
            ((GraphCentricQueryBuilder) query).profiler(queryProfiler);
            return Vertex.class.isAssignableFrom(this.returnClass) ? query.vertices().iterator() : query.edges().iterator();
        });
    }

    @Override
    public String toString() {
        return this.hasContainers.isEmpty() ? super.toString() : StringFactory.stepString(this, this.hasContainers);
    }

    @Override
    public void addAll(Iterable<HasContainer> has) {
        Iterables.addAll(hasContainers, has);
    }

    @Override
    public void orderBy(String key, Order order) {
        orders.add(new OrderEntry(key, order));
    }

    @Override
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public void setMetrics(MutableMetrics metrics) {
        queryProfiler = new TP3ProfileWrapper(metrics);
    }

    @Override
    public List<HasContainer> getHasContainers() {
        return this.hasContainers;
    }

    @Override
    public void addHasContainer(final HasContainer hasContainer) {
        this.addAll(Collections.singleton(hasContainer));
    }
}

