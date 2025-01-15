package com.app.quickbite.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.app.quickbite.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
