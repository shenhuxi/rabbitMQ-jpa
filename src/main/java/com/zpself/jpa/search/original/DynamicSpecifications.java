package com.zpself.jpa.search.original;

import com.zpself.jpa.utils.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.List;


/**
 * 动态组装工具
 * @author shixh
 */
public class DynamicSpecifications {

	/**
	 * 动态组装查询条件返回Specification
	 * @param filters
	 * @param entityClazz
	 * @return
	 */
	public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				if (!CollectionUtils.isEmpty(filters)) {

					List<Predicate> predicates = Lists.newArrayList();
					for (SearchFilter filter : filters) {
						String[] names = StringUtils.split(filter.fieldName, ".");
						Path expression = root.get(names[0]);
						for (int i = 1; i < names.length; i++) {
							expression = expression.get(names[i]);
						}
						switch (filter.operator) {
						case EQ:
							predicates.add(builder.equal(expression, filter.value));
							break;
						case NEQ:
							predicates.add(builder.notEqual(expression, filter.value));
							break;
						case LIKE:
							predicates.add(builder.like(expression, "%" + filter.value + "%"));
							break;
						case GT:
							predicates.add(builder.greaterThan(expression, (Comparable) filter.value));
							break;
						case LT:
							predicates.add(builder.lessThan(expression, (Comparable) filter.value));
							break;
						case GTE:
							predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
							break;
						case LTE:
							predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) filter.value));
							break;
						case IN:
							predicates.add(expression.in(filter.value.toString().split(",")));
							break;
                        case BTW: // Add by Alex Yan 2018-05-24
                            predicates.add(builder.between(expression, (Comparable)filter.from, (Comparable)filter.to));
                            break;
						}
					}

					// 将所有条件用 and 联合起来
					if (!predicates.isEmpty()) {
						return builder.and(predicates.toArray(new Predicate[predicates.size()]));
					}
				}

				return builder.conjunction();
			}
		};
	}
	
	/**
	 * 动态组装查询条件返回SQL
	 * @param filters
	 * @return
	 */
	public static String bySearchFilter(Collection<SearchFilter> filters) {
		if (!CollectionUtils.isEmpty(filters)) {
			StringBuffer sb = new StringBuffer(" where 1=1 ");
			for (SearchFilter filter : filters) {
				String[] names = StringUtils.split(filter.fieldName, ".");
				String colimn = ColumnUtil.camelToUnderline(names[0]);
				switch (filter.operator) {
				case EQ:
					sb.append(" and "+colimn+"='"+filter.value+"'");
					break;
				case NEQ:
					sb.append(" and "+colimn+"<>'"+filter.value+"'");
					break;
				case LIKE:
					sb.append(" and "+colimn+" like '%" + filter.value + "%'");
					break;
				case GT:
					sb.append(" and "+colimn+" >" + filter.value);
					break;
				case LT:
					sb.append(" and "+colimn+" <" + filter.value);
					break;
				case GTE:
					sb.append(" and "+colimn+" >=" + filter.value);
					break;
				case LTE:
					sb.append(" and "+colimn+" <=" + filter.value);
					break;
				case IN:
					sb.append(" and "+colimn+" in(" + filter.value+")");
					break;
				}
			}
			return sb.toString();
		}
		return "";
	}
	
	/**
	 * 动态组装查询条件返回SQL
	 * @param filters
	 * @param aliases 别名
	 * @return
	 */
	public static String bySearchFilter(Collection<SearchFilter> filters,String aliases) {
		if (!CollectionUtils.isEmpty(filters)) {
			StringBuilder sb = new StringBuilder(" where 1=1 ");
			for (SearchFilter filter : filters) {
				String[] names = StringUtils.split(filter.fieldName, ".");
				String colimn = ColumnUtil.camelToUnderline(names[0]);
				if(filter.value==null) {
                    break;
                }
				switch (filter.operator) {
				case EQ:
					sb.append(" and "+aliases+"."+colimn+"='"+filter.value+"'");
					break;
				case NEQ:
					sb.append(" and "+aliases+"."+colimn+"<>'"+filter.value+"'");
					break;
				case LIKE:
					sb.append(" and "+aliases+"."+colimn+" like '%" + filter.value + "%'");
					break;
				case GT:
					sb.append(" and "+aliases+"."+colimn+" >" + filter.value);
					break;
				case LT:
					sb.append(" and "+aliases+"."+colimn+" <" + filter.value);
					break;
				case GTE:
					sb.append(" and "+aliases+"."+colimn+" >=" + filter.value);
					break;
				case LTE:
					sb.append(" and "+aliases+"."+colimn+" <=" + filter.value);
					break;
				case IN:
					sb.append(" and "+aliases+"."+colimn+" in(" + filter.value+")");
					break;
				}
			}
			return sb.toString();
		}
		return "";
	}
}
