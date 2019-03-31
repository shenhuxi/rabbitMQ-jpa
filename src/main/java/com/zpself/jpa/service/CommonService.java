package com.zpself.jpa.service;

import com.zpself.jpa.former.MyResultTransformer;
import com.zpself.jpa.repository.CommonRepository;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 *
 * Create By qinzhw
 * 2018年5月8日上午11:11:42
 */
public abstract class CommonService<E,ID extends Serializable>  {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected EntityManager em;
	
	public abstract CommonRepository<E, ID> getCommonRepository();
	
	
	/**
	 * 根据ID获取某个Entity
	 * @param id
	 * @return
	 */
	public E get(ID id) {
		return getCommonRepository().getOne(id);
	}

	/**
	 * 根据ID查找某个Entity（建议使用）
	 * @param id
	 * @return
	 */
	public E findOne(ID id)  {
		E e = getCommonRepository().findOne(id);
		return e;
	}

	/**
	 * 获取所有的Entity列表
	 * @return
	 */
	public List<E> getAll() {
		return getCommonRepository().findAll();
	}
	
	/**
	 * 获取Entity的总数
	 * @return
	 */
	public Long getTotalCount() {
		return getCommonRepository().count();
	}

	/**
	 * 保存Entity
	 * @param entity
	 * @return
	 */
	public E save(E entity){
		return getCommonRepository().save(entity);
	}

	
	/**
	 * 修改Entity
	 * @param entity
	 * @return
	 */
	public E update(E entity) {
		return getCommonRepository().save(entity);
	}
	
	/**
	 * 删除Entity
	 * @param entity
	 */
	public void delete(E entity) {
		getCommonRepository().delete(entity);
	}

	/**
	 * 根据Id删除某个Entity
	 * @param id
	 */
	public void delete(ID id) {
		try {
			getCommonRepository().delete(id);
		} catch (EmptyResultDataAccessException e) {
			//如果是ID在DB不存在,不往外抛异常 add shixh 0521
			logger.info("如果是ID在DB不存在,不往外抛异常");
		}
		
	}

	/**
	 * 删除Entity的集合类
	 * @param entities
	 */
	public void delete(Collection<E> entities) {
		getCommonRepository().delete(entities);
	}

	/**
	 * 清空缓存，提交持久化
	 */
	public void flush() {
		getCommonRepository().flush();
	}
	
	/**
	 * 根据查询信息获取某个Entity的列表
	 * @param spec
	 * @return
	 */
	public List<E> findAll(Specification<E> spec) {
		return getCommonRepository().findAll(spec);
	}
	
	/**
	 * 根据查询信息获取某个Entity的列表
	 * @param searchParams
	 * @return
	 */
	public List<E> findAll(Map<String, Object> searchParams) {
		return getCommonRepository().findListByParams(searchParams);
	}
	
	/**
	 * 获取Entity的分页信息
	 * @param searchParams
	 * @param pageRequest
	 * @return
	 */
	public Page<E> findAll(Map<String, Object> searchParams, PageRequest pageRequest){
		return getCommonRepository().findPageByParams(searchParams, pageRequest);
	}
	public <K>Page<K> findAll(Map<String, Object> searchParams, PageRequest pageRequest, Class<K> clz){
		return getCommonRepository().findPageByParams(searchParams, pageRequest,clz);
	}
	
	/**
	 * 获取Entity的分页信息
	 * @param pageable
	 * @return
	 */
	public Page<E> findAll(Pageable pageable){
		return getCommonRepository().findAll(pageable);
	}
	
	/**
	 * 根据查询条件和分页信息获取某个结果的分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<E> findAll(Specification<E> spec, Pageable pageable) {
		return getCommonRepository().findAll(spec, pageable);
	}
	
	/**
	 * 根据查询条件和排序条件获取某个结果集列表
	 * @param spec
	 * @param sort
	 * @return
	 */
	public List<E> findAll(Specification<E> spec, Sort sort) {
		return getCommonRepository().findAll(spec,sort);
	}
	
	/**
	 * 查询某个条件的结果数集
	 * @param spec
	 * @return
	 */
	public long count(Specification<E> spec) {
		return getCommonRepository().count(spec);
	}
	

	/**
	 * 根据SQL返回分页,支持多表查询
	 * @author shixh
	 * @param sql
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page<E> getPage(String sql,int pageNum,int pageSize) {
		PageRequest pageRequest = new PageRequest(pageNum, pageSize,new Sort(Sort.Direction.DESC, "id"));
		return getCommonRepository().findPageByNativeSQL(sql, pageRequest);
	}
	
	/**
	 * 根据原生SQL返回分页
	 * @author shixh
	 * @param sql
	 * @param pageNum
	 * @param pageSize
	 * @param sort
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page<E> getPage(String sql,int pageNum,int pageSize,Sort sort) {
		PageRequest pageRequest = new PageRequest(pageNum, pageSize,sort);
		return getCommonRepository().findPageByNativeSQL(sql, pageRequest);
	}
	
 
	/**
	 * 支持多个表字段 返回 一个 对象 
	 * @author shixh
	 * @param sql
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page<E> getPage(String sql,PageRequest pageable) {
		return getCommonRepository().findPageByNativeSQL(sql, pageable);
	}

	/***************增强公共方法 add by qzhw 2019年1月22日 17:47:24 begin**************/
	/**
	 * @author qzhw
	 */
	public <K> Page<K> getPageVo(String sql, PageRequest pageable, Class<K> clazz){
		Query query = em.createNativeQuery(sql);
		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		query.unwrap(SQLQuery.class)
				.setResultTransformer(new MyResultTransformer(clazz));
		List<K> content = query.getResultList();
		StringBuffer countSql = new StringBuffer("select count(1) from( ").append(sql).append(" )");
		BigDecimal conut = (BigDecimal) em.createNativeQuery(countSql.toString()).getSingleResult();
		PageImpl<K> page = new PageImpl<K>(content, pageable, conut.intValue());
		return page;
	}

	public <K> List<K> getListVo(String sql, Class<K> clazz){
		Query query = em.createNativeQuery(sql);
		query.unwrap(SQLQuery.class)
				.setResultTransformer(new MyResultTransformer(clazz));
		List<K> content = query.getResultList();
		return content;
	}
	public List<Map<String, Object>> getMap(String sql){
		Query query = em.createNativeQuery(sql);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
	}
    public List<List<Object>> getList(String sql){
        Query query = em.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.TO_LIST);
        return query.getResultList();
    }
	public <K>Page<K> findPageByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest) {
		return getCommonRepository().findPageByNativeSQLAndParams(sql, searchParams, clz, pageRequest);
	}
	public <K>List<K> findListByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, Sort sort) {
		return getCommonRepository().findListByNativeSQLAndParams(sql, searchParams, clz, sort);
	}
	public <K>Page<K> findPageByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest) {
		return getCommonRepository().findPageByJPQL(jpql, searchParams, clz, pageRequest);
	}
	public <K>List<K> findListByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, Sort sort) {
		return getCommonRepository().findListByJPQL(jpql, searchParams, clz, sort);
	}
	/***************增强公共方法 add by qzhw 2019年1月22日 17:47:24 end**************/
}