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

package com.baidu.hugegraph.diskstorage.keycolumnvalue.keyvalue;

import com.baidu.hugegraph.diskstorage.BackendException;
import com.baidu.hugegraph.diskstorage.keycolumnvalue.StoreManager;

/**
 * {@link StoreManager} for {@link KeyValueStore}.
 *
 */
public interface KeyValueStoreManager extends StoreManager {

    /**
     * Opens a key-value database by the given name. If the database does not exist, it is
     * created. If it has already been opened, the existing handle is returned.
     * <p/>
     *
     * @param name Name of database
     * @return Database Handle
     * @throws com.baidu.hugegraph.diskstorage.BackendException
     *
     */
    public KeyValueStore openDatabase(String name) throws BackendException;



}
