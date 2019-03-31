package com.zpself.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 描述:
 * 作者: qinzhw
 * 创建时间:
 **/
@NoRepositoryBean
public interface CommonRepository<T,ID extends Serializable>  extends JpaRepository<T,ID>, JpaSpecificationExecutor<T> {

    /**
     * 根据查询参数查询
     * @param searchParams 【过滤条件】
     * @param pageRequest 【分页参数对象】
     * @return
     */
    Page<T> findPageByParams(Map<String, Object> searchParams, PageRequest pageRequest);
    /*效率较低*/
    <K>Page<K> findPageByParams(Map<String, Object> searchParams, PageRequest pageRequest, Class<K> clz);

    /**
     * 根据查询条件查询 不分页
     * @param searchParams
     * @return
     */
    List<T> findListByParams(Map<String, Object> searchParams, Sort sort);
    /*效率较低*/
    <K>List<K> findListByParams(Map<String, Object> searchParams, Sort sort, Class<K> clz);
    /**
     * 根据查询条件查询 不分页
     * @param searchParams
     * @return
     */
    List<T> findListByParams(Map<String, Object> searchParams);



    /**
     * 根据原生SQL查询数据
     * @param sql
     * @param pageRequest 分页参数对象
     * @return
     */
    @SuppressWarnings("rawtypes")
    Page<T> findPageByNativeSQL(String sql, PageRequest pageRequest) ;

    /**
     * 根据原生SQL及查询条件查询数据
     * @param sql
     * @param searchParams 查询条件
     * @param clz 对象类名
     * @param pageRequest 分页参数对象
     * @return
     */
    @SuppressWarnings("rawtypes")
    <K>Page<K> findPageByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest) ;

    /**
     * 原生SQL和查询条件查询 不分页
     * @param sql
     * @param searchParams
     * @param clz
     * @param sort
     * @return
     */
    @SuppressWarnings("rawtypes")
    <K>List<K> findListByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, Sort sort);

    /**
     * 按JQL分页查询
     * @param jpql
     * @param searchParams 【过滤条件】
     * @param pageRequest 【分页参数对象】
     * @return
     */
    <K>Page<K> findPageByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest);

    /**
     * 按JQL查询不分页
     * @param jpql
     * @param searchParams【过滤条件】
     * @param clz
     * @param sort
     * @param <K>
     * @return
     */
    <K>List<K> findListByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, Sort sort);

}
