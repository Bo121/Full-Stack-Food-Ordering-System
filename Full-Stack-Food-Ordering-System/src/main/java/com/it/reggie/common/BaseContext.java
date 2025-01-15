package com.it.reggie.common;

/**
 * Utility class based on ThreadLocal to save and retrieve the current logged-in user's ID.
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * Set the value
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * Get the value
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
