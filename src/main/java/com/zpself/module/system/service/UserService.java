package com.dingxin.gdsj.modules.system.service;

import com.dingxin.gdsj.common.jpa.service.BaseService;
import com.dingxin.gdsj.modules.system.entity.User;

import java.util.List;

/**
 * 描述:
 * 作者: qinzhw
 * 创建时间: 2018/8/14 14:43
 */
public interface UserService extends BaseService<User,Long> {

    User findByAccount(String account);
    
    User findByUuid(String uuid);

	boolean batchDel(List<Long> list);

	void batchImport(List<User> list);


    List<String> getAllClass();

	List<User> findByName(String teacherName);

    
}