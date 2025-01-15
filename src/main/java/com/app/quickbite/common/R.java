package com.app.quickbite.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic response result class
 * <p>All server response data will ultimately be encapsulated in this object.
 *
 * @param <T> The type of the data contained in the response.
 */
@Data
public class R<T> {

    /**
     * Status code: 1 for success, 0 or other numbers for failure.
     */
    private Integer code;

    /**
     * Error message.
     */
    private String msg;

    /**
     * Specific data payload.
     */
    private T data;

    /**
     * Dynamic data map.
     */
    private Map<String, Object> map = new HashMap<>();

    /**
     * Creates a successful response with the given data and success code.
     *
     * @param object The success data object.
     * @param <T>    The type of the data.
     * @return An instance of this class containing the success code and data.
     */
    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.data = object;
        r.code = 1;
        return r;
    }

    /**
     * Creates a failed response with the given error message.
     *
     * @param msg The error message.
     * @param <T> The type of the data (if any).
     * @return An instance of this class containing the failure code and error message.
     */
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    /**
     * Adds a key-value pair to the dynamic data map.
     *
     * @param key   The key for the data.
     * @param value The value associated with the key.
     * @return The current instance of this class with the updated map.
     */
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}

