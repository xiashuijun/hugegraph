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

package com.baidu.hugegraph.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import com.baidu.hugegraph.core.attribute.AttributeSerializer;
import com.baidu.hugegraph.diskstorage.ScanBuffer;
import com.baidu.hugegraph.diskstorage.WriteBuffer;

import java.util.UUID;

/**
 */
public class UUIDSerializer implements AttributeSerializer<UUID>  {

    @Override
    public UUID read(ScanBuffer buffer) {
        long mostSignificantBits = buffer.getLong();
        long leastSignificantBits = buffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    @Override
    public void write(WriteBuffer buffer, UUID attribute) {
        buffer.putLong(attribute.getMostSignificantBits());
        buffer.putLong(attribute.getLeastSignificantBits());
    }

    @Override
    public UUID convert(Object value) {
        Preconditions.checkNotNull(value);
        if(value instanceof String){
            return UUID.fromString((String) value);
        }
        return null;
    }
}
