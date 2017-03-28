package com.baidu.hugegraph2.type.define;

/**
 * Created by jishilei on 17/3/18.
 */

public enum Cardinality {

    /**
     * Only a single value may be associated with the given key.
     */
    SINGLE,

    /**
     * Multiple but distinct values may be associated with the given key.
     */
    MULTIPLE;


    public String schema() {
        // enum object -> string -> lowercase
        return this.toString().toLowerCase();
    }
}