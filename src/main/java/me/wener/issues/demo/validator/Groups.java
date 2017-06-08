package me.wener.issues.demo.validator;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
public interface Groups {

    /**
     * Group used for HTTP PUT, update
     */
    interface Update {

    }

    /**
     * Group used for HTTP POST
     */
    interface Create {

    }

    /**
     * Group used for HTTP PATCH, partial update, update nonnull field.
     */
    interface Patch {

    }
}
