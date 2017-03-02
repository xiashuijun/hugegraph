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

package com.baidu.hugegraph.diskstorage;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

/**
 */
public interface EntryList extends List<Entry> {

    /**
     * Returns the same iterator as {@link #iterator()} with the only difference
     * that it reuses {@link Entry} objects when calling {@link java.util.Iterator#next()}.
     * Hence, this method should only be used if references to {@link Entry} objects are only
     * kept and accesed until the next {@link java.util.Iterator#next()} call.
     *
     * @return
     */
    public Iterator<Entry> reuseIterator();


    /**
     * Returns the total amount of bytes this entry consumes on the heap - including all object headers.
     *
     * @return
     */
    public int getByteSize();



    public static final EmptyList EMPTY_LIST = new EmptyList();

    static class EmptyList extends AbstractList<Entry> implements EntryList {

        @Override
        public Entry get(int index) {
            throw new ArrayIndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<Entry> reuseIterator() {
            return iterator();
        }

        @Override
        public int getByteSize() {
            return 0;
        }
    }

}
