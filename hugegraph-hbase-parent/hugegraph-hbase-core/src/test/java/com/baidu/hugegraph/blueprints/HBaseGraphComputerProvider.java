// Copyright 2017 hugegraph Authors
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

package com.baidu.hugegraph.blueprints;

import com.baidu.hugegraph.HBaseStorageSetup;
import com.baidu.hugegraph.diskstorage.configuration.ModifiableConfiguration;
import com.baidu.hugegraph.graphdb.olap.computer.FulgoraGraphComputer;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.GraphProvider;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.io.IOException;

/**
 */
@GraphProvider.Descriptor(computer = FulgoraGraphComputer.class)
public class HBaseGraphComputerProvider extends AbstractHugeGraphComputerProvider {

    @Override
    public ModifiableConfiguration getHugeGraphConfiguration(String graphName, Class<?> test, String testMethodName) {
        ModifiableConfiguration config = super.getHugeGraphConfiguration(graphName, test, testMethodName);
        config.setAll(HBaseStorageSetup.getHBaseConfiguration(graphName).getAll());
        return config;
    }

    @Override
    public Graph openTestGraph(final Configuration config) {
        try {
            HBaseStorageSetup.startHBase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.openTestGraph(config);
    }

}
