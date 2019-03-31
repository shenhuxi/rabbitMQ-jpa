package com.zpself.jpa.search.original;


import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import static com.dingxin.data.jpa.search.original.Operator.BTW;

public class SearchFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchFilter.class);

	public String fieldName;
	public Object value;
	public Operator operator;

    /**
     * from 和 to，用于between的情况。 Add by Alex Yan 2018-05-24
     */
	public Date from;
	public Date to;

	public SearchFilter(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}

    /**
     * 添加了from 和 to，用于between的情况。 Add by Alex Yan 2018-05-24
     * @param fieldName
     * @param operator
     * @param value
     * @param from
     * @param to
     * @throws ParseException
     */
	public SearchFilter(String fieldName, Operator operator, Object value, Date from, Date to) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
		this.from = from;
		this.to = to;
	}

    /**
     * 拼装查询条件
     * @param searchParams
     * @return
     * @throws ParseException
     */
	public static Map<String, SearchFilter> parse(Map<String,Object> searchParams) throws ParseException {
		Map<String, SearchFilter> filters = Maps.newHashMap();

		for (Entry<String, Object> entry : searchParams.entrySet()) {
			
			String key = entry.getKey();
			Object value = entry.getValue();
			String[] names = StringUtils.split(key, "_");
			if (names.length != 2) {
				//过滤 pageNumber,pageSize,sort,sortType add shixh 0511
				logger.warn("{} is not a valid search filter name,A good example is:EQ_userName",key);
				continue;
			}
			String filedName = names[1];
			Operator operator = Operator.valueOf(names[0]);
			SearchFilter filter = null;
			// BTW 仅支持时间段查询，如果没有结束时间，当前时间为结束时间。
			if(BTW.equals(operator) && StringUtils.isNotEmpty(String.valueOf(value))) {
				String[] dateArrange = String.valueOf(value).split(",");
				if (dateArrange.length == 2) {
					Date fromDate = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").parse(dateArrange[0]);
                    Date toDate = null;
                    if(StringUtils.isEmpty(dateArrange[1].trim())) {
					    toDate = new Date();
                    } else {
                        toDate = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").parse(dateArrange[1]);
                    }
					filter = new SearchFilter(filedName, operator, value, fromDate, toDate);
				} else {
					continue;
				}
            } else {
                filter = new SearchFilter(filedName, operator, value);
            }
			filters.put(key, filter);
		}

		return filters;
	}

	
}
