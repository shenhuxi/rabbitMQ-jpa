package com.zpself.jpa.controller;

import com.alibaba.fastjson.JSON;
import com.zpself.module.system.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.Map.Entry;


/**
 *
 * Create By qinzhw
 * 2018年4月12日下午3:37:26
 */
public class BaseController {
    public Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public HttpServletRequest request;




    public String getUserToken() {
        String token = request.getHeader("token");
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        if(StringUtils.isBlank(token)){
            token = request.getSession().getId();
        }
        return token;
    }
    public String getValidErrorMsg(BindingResult... brs) {
        List<String> msglist = new ArrayList<>();
        for (BindingResult br: brs) {
            List<ObjectError> errors = br.getAllErrors();
            errors.forEach(error -> msglist.add(error.getDefaultMessage()));
        }
        return StringUtils.join(msglist, "|");
    }


    /**
     * 封装分页信息
     *
     * @author shixh
     * @param pageNumber
     * @param pagzSize
     * @param sort
     * @return
     */
    public PageRequest buildPageRequest(int pageNumber, int pagzSize, Sort sort) {
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }

    /**
     * 封装分页信息
     *
     * @author shixh
     * @param pageNumber
     * @param pagzSize
     * @return
     */
    public PageRequest buildPageRequest(int pageNumber, int pagzSize) {
        return new PageRequest(pageNumber - 1, pagzSize);
    }


    /**
     * 封装分页信息
     * @param pageNumber
     * @param pagzSize
     * @param sort
     * @param sortType
     * @return
     */
    public PageRequest buildPageRequest(int pageNumber, int pagzSize, String[] sort, String[] sortType) {
        Sort s;
        if (sort != null && sort.length != 0) {
            int length = sort.length;
            List<Sort.Order> orders = new ArrayList<Sort.Order>(length);
            if(sortType == null || sortType.length == 0 || sortType.length != length) {
                sortType = new String[length];
                for (int i = 0; i < length; i++) {
                    sortType[i] = "ASC";
                }
            }
            for (int i = 0; i < length; i++) {
                if (StringUtils.isBlank(sort[i])) {
                    continue;
                }
                Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortType[i]), sort[i]);
                orders.add(order);
            }
            s = new Sort(orders);
        } else {
            s = new Sort(new Sort.Order(Sort.Direction.ASC,"id"));
        }

        return new PageRequest(pageNumber - 1, pagzSize, s);
    }

    /**
     * 封装分页信息,支持多条件排序
     * 	"sort":["type","id"],
     *  "sortType":["DESC","ASC"],
     */

    /**
     * 请求参数转MAP
     *
     * @author shixh
     * @return
     */
    public Map<String, Object> getParameterMap() {
        Map<String,String[]> properties = request.getParameterMap();
        Map<String, Object> returnMap = new HashMap<>();
        Iterator<Entry<String,String[]>> entries = properties.entrySet().iterator();
        Entry<String,String[]> entry;
        String name = "";
        Object value = "";
        while (entries.hasNext()) {
            entry = entries.next();
            name = entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
                continue; /*值为空时 忽略查询字段 add by chaihu*/
            } else if (valueObj instanceof String[]) {
                String valueStr = "";
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    valueStr = values[i] + ",";
                }
                value = valueStr.substring(0, valueStr.length() - 1);
            } else {
                value = valueObj;
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    public Map<String, Object> excludeNull(Map<String, Object> paramMap) {
        Set<String> set = paramMap.keySet();
        Iterator<String> it = set.iterator();
        List<String> listKey = new ArrayList<String>();
        while (it.hasNext()) {
            String str = it.next();
            if(paramMap.get(str)==null || "".equals(paramMap.get(str))){
                listKey.add(str) ;
            }
        }
        for (String key : listKey) {
            paramMap.remove(key);
        }
        return paramMap;
    }
    /**
     * zengpeng
     */
    public Map<String, Object> changeRelation(String  anotherName,Map<String, Object> map) {
    	Map<String, Object> newMap = new HashMap<String, Object>();
    	 for (Entry<String, Object> entry : map.entrySet()) {
             String key = entry.getKey();
             int indexOf = key.indexOf("_");
             if(indexOf>1) {
            	 key=key.replace("_", "_"+anotherName);
             }
             newMap.put(key, entry.getValue());
    	 }
    	 return newMap;
    }
}
