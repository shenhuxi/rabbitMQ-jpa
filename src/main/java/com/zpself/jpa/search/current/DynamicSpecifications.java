/**
 * 
 */
package com.zpself.jpa.search.current;

/**
 * @author Administrator
 * 
 */

import com.dingxin.data.jpa.utils.ColumnUtil;
import com.dingxin.data.jpa.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.sql.Timestamp;
import java.util.*;


public class DynamicSpecifications {
	
	private DynamicSpecifications() {}
	
    private static final ConversionService conversionService = new DefaultConversionService();

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> clazz) {
        return new Specification<T>() {
            @SuppressWarnings({ "rawtypes", "unchecked", "static-access", "incomplete-switch" })
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (filters!=null && !filters.isEmpty()) {
                    List<Predicate> andList = new ArrayList<>();
                    List<Predicate> orList = new ArrayList<>();
                    for (SearchFilter filter : filters) {
                        if(filter.value==null||filter.value.equals("")){
                            continue;
                        }
                        // nested path translate
                        String[] names = StringUtils.split(filter.getFieldName(), ".");
                        Path expression = root.get(names[0]);
                        for (int i = 1; i < names.length; i++) {
                            expression = expression.get(names[i]);
                        }

                        // convert value
                        Class attributeClass = expression.getJavaType();
                        if (!attributeClass.equals(String.class) && filter.getValue() instanceof String && conversionService.canConvert(String.class, attributeClass) && !filter.getOperator().IS.equals(filter.getOperator())) {
                            if(attributeClass == Date.class){
                                filter.setValue(new Date(Long.parseLong(filter.value+"")));
                            }
                            filter.setValue(conversionService.convert(filter.getValue(), attributeClass));
                        }
                        switch (filter.getOperator()) {
                        case EQ:
                            addList(filter.isUseAnd(), andList, orList, builder.equal(expression, filter.getValue()));
                            break;
                        case LIKE:
                            addList(filter.isUseAnd(), andList, orList, builder.like(expression, "%" + filter.getValue() + "%"));
                            break;
                        case RLIKE:
                            addList(filter.isUseAnd(), andList, orList, builder.like(expression, filter.getValue() + "%"));
                            break;
                        case LLIKE:
                            addList(filter.isUseAnd(), andList, orList, builder.like(expression, "%" + filter.getValue()));
                            break;case GT:
                            addList(filter.isUseAnd(), andList, orList, builder.greaterThan(expression, (Comparable) filter.getValue()));
                            break;
                        case LT:
                            addList(filter.isUseAnd(), andList, orList, builder.lessThan(expression, (Comparable) filter.getValue()));
                            break;
                        case GTE:
                            addList(filter.isUseAnd(), andList, orList, builder.greaterThanOrEqualTo(expression, (Comparable) filter.getValue()));
                            break;
                        case LTE:
                            addList(filter.isUseAnd(), andList, orList, builder.lessThanOrEqualTo(expression, (Comparable) filter.getValue()));
                            break;
                        case IS:
                            if ("NULL".equalsIgnoreCase(filter.getValue().toString())) {
                                addList(filter.isUseAnd(), andList, orList, builder.isNull(expression));
                            } else {
                                addList(filter.isUseAnd(), andList, orList, builder.isNotNull(expression));
                            }
                            break;
                        case NEQ:
                            addList(filter.isUseAnd(), andList, orList, builder.notEqual(expression, (Comparable) filter.getValue()));
                            break;
                        case IN:
                            if (filter.getValue() instanceof Object[]) {
                                List<Object> ls = new ArrayList<>();
                                for (Object object : (Object[]) filter.getValue()) {
                                    ls.add(object);
                                }
                                addList(filter.isUseAnd(), andList, orList, in(builder, expression, ls));
                            } else if (filter.getValue() instanceof Collection) {
                                addList(filter.isUseAnd(), andList, orList, in(builder, expression, (Collection<Object>) filter.getValue()));
                            } else {
                                List<Object> ls = new ArrayList<>();
                                ls.add(filter.getValue());
                                addList(filter.isUseAnd(), andList, orList, in(builder, expression, ls));
                            }
                            break;
                        case NIN:
                            if (filter.getValue() instanceof Object[]) {
                                List<Object> ls = new ArrayList<>();
                                for (Object object : (Object[]) filter.getValue()) {
                                    ls.add(object);
                                }
                                addList(filter.isUseAnd(), andList, orList, builder.not(in(builder, expression, ls)));
                            } else if (filter.getValue() instanceof Collection) {
                                addList(filter.isUseAnd(), andList, orList, builder.not(in(builder, expression, (Collection<Object>) filter.getValue())));
                            } else {
                                List<Object> ls = new ArrayList<>();
                                ls.add(filter.getValue());
                                addList(filter.isUseAnd(), andList, orList, builder.not(in(builder, expression, ls)));
                            }
                        }
                    }
                    int osize = orList.size();
                    Predicate pre = null;
                    if (!andList.isEmpty()) {
                        pre = builder.and(andList.toArray(new Predicate[andList.size()]));
                    }
                    if (osize > 0) {
                        Predicate or = builder.or(orList.toArray(new Predicate[orList.size()]));
                        if (pre != null) {
                            return builder.and(pre, or);
                        } else {
                            return or;
                        }
                    }
                    if (pre != null) {
                        return pre;
                    }
                }
                return builder.conjunction();
            }

            /**
             * 根据条件放到And或Or的列表中.
             * 
             * @param useAnd
             * @param andList
             * @param orList
             * @param value
             */
            private void addList(boolean useAnd, List<Predicate> andList, List<Predicate> orList, Predicate value) {
                if (useAnd) {
                    andList.add(value);
                } else {
                    orList.add(value);
                }

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
                if(filter.value==null||filter.value.equals("")){
                    continue;
                }
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
			StringBuffer sb = new StringBuffer(" where 1=1 ");
			for (SearchFilter filter : filters) {
				String[] names = StringUtils.split(filter.fieldName, ".");
				String colimn = ColumnUtil.camelToUnderline(names[0]);
				if(filter.value==null||filter.value.equals("")){
                    continue;
                }
                if(filter.value instanceof List){
                    List l = (List) filter.value;
                    if(l.size()<1 || l.size()==1&&"".equals(l.get(0))){
                        continue;
                    }
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
                    Long time;
                    try {
                        time = Long.parseLong(filter.value+"");
                        Timestamp s = new Timestamp(time);
                        String timeStampStr = "to_timestamp('"+s+"','syyyy-mm-dd hh24:mi:ss.ff')";
                        sb.append(" and "+aliases+"."+colimn+" >" + timeStampStr);
                        break;
                    } catch (NumberFormatException e) {
                        sb.append(" and "+aliases+"."+colimn+" >" + filter.value);
                        break;
                    }
				case LT:
                    try {
                        time = Long.parseLong(filter.value+"");
                        Timestamp s = new Timestamp(time);
                        String timeStampStr = "to_timestamp('"+s+"','syyyy-mm-dd hh24:mi:ss.ff')";
                        sb.append(" and "+aliases+"."+colimn+" <" + timeStampStr);
                        break;
                    } catch (NumberFormatException e) {
                        sb.append(" and "+aliases+"."+colimn+" <" + filter.value);
                        break;
                    }
				case GTE:
					sb.append(" and "+aliases+"."+colimn+" >=" + filter.value);
					break;
				case LTE:
					sb.append(" and "+aliases+"."+colimn+" <=" + filter.value);
					break;
				case IN:
                    List o = (List) filter.value;
					sb.append(" and "+aliases+"."+colimn+" in(" +org.apache.commons.lang3.StringUtils.join(o.toArray(),",")+")");
					break;
                case NIN:
                    o = (List) filter.value;
                    sb.append(" and "+aliases+"."+colimn+" not in(" + org.apache.commons.lang3.StringUtils.join(o.toArray(),",") +")");
                    break;
				}
			}
			return sb.toString();
		}
		return "";
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Predicate in(CriteriaBuilder builder, Path expression, Collection<Object> value) {
        if ((value == null) || (value.isEmpty())) {
            return null;
        }
        In in = builder.in(expression);
        for (Object object : value) {
            in.value(object);
        }
        return in;
    }

    /**
     * 创建动态查询条件组合.
     */
    public static <T> Specification<T> buildSpecification(Map<String, Object> searchParams, Class<T> clz) {
        List<SearchFilter> filters = SearchFilter.parseList(searchParams);
        return DynamicSpecifications.bySearchFilter(filters, clz);
    }
    
    public static String buildSqlWithEnd(Map<String, Object> searchParams,String alisa)  {
    	StringBuffer sb = new StringBuffer("");
    	Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
    	String sql = DynamicSpecifications.bySearchFilter(filters.values(),alisa);
		return sb.append(sql).toString();
	}
    
    public static String buildSqlWithEnd(Map<String, Object> searchParams)  {
    	StringBuffer sb = new StringBuffer("");
    	Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
    	String sql = DynamicSpecifications.bySearchFilter(filters.values());
		return sb.append(sql).toString();
	}
    
}
