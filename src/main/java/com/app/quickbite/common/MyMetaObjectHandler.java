package com.app.quickbite.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * Automatically fills fields during insertion.
     *
     * @param metaObject The metadata object to be processed.
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("Automatically filling common fields [insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());     // Retrieves the current user's ID from ThreadLocal.
        metaObject.setValue("updateUser", BaseContext.getCurrentId());     // Retrieves the current user's ID from ThreadLocal.
    }

    /**
     * Automatically fills fields during updates.
     *
     * @param metaObject The metadata object to be processed.
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("Automatically filling common fields [update]...");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();                            // Retrieves the current thread's ID.
        log.info("Current thread ID={}", id);                                // Logs the thread ID.

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());      // Retrieves the current user's ID from ThreadLocal.
    }
}
