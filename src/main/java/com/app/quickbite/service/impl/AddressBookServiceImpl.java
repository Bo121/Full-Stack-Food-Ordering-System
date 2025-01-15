package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.entity.AddressBook;
import com.app.quickbite.mapper.AddressBookMapper;
import com.app.quickbite.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
