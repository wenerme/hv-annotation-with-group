package me.wener.issues.demo.data.entity;

import java.io.Serializable;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
public interface Persistable<ID extends Serializable> {

    ID getId();
}
