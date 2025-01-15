package com.app.quickbite.controller;

import com.app.quickbite.common.BaseContext;
import com.app.quickbite.common.R;
import com.app.quickbite.entity.AddressBook;
import com.app.quickbite.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Address book management
 */
/**
 * Address Book Management
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * <h2>Add a New Address<h2/>
     *
     * @param addressBook Address book object; @RequestBody indicates that JSON-formatted data is received
     * @return {@link R}
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * <h2>Set Default Address<h2/>
     *
     * @param addressBook Address book object; @RequestBody indicates that JSON-formatted data is received
     * @return {@link R}
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        // SQL: update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        // SQL: update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * <h2>Get Address by ID<h2/>
     *
     * @param id Address ID; @PathVariable indicates the parameter is obtained from the URL before the "?"
     * @return {@link R}
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook); // TODO: Returned data lacks proper mapping for the `label` field to the frontend's label selection state.
        } else {
            return R.error("Object not found");
        }
    }

    /**
     * <h2>Get Default Address<h2/>
     *
     * @return {@link R}
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        // SQL: select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook == null) {
            return R.error("Object not found");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * <h2>Get All Addresses of a User<h2/>
     *
     * @param addressBook Address book object; @RequestBody indicates that JSON-formatted data is received
     * @return {@link R}
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        // Condition builder
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        // SQL: select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * <h2>Update Address Book<h2/>
     *
     * @param addressBook Address book object; @RequestBody indicates that JSON-formatted data is received
     * @return {@link R}
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook) {
        log.info("Updated addressBook:{}", addressBook);
        // SQL: update address_book set name = ?, phone = ?, address = ?, is_default = ?, update_time = ? where id = ?
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * <h2>Delete Address Book by ID<h2/>
     *
     * @param ids Address ID
     * @return {@link R}
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("ID to delete:{}", ids);
        addressBookService.removeById(ids);

        return R.success("Deleted successfully");
    }
}