package com.zpself.jpa.dialect;

import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

/**
 * 描述: 增强oracle方言，解决大文本保存问题（No Dialect mapping for JDBC type: 2011）
 * 作者: qinzhw
 * 创建时间: 2018/12/21 15:47
 */
public class MyOracle12cDialect extends Oracle12cDialect {
 
	public MyOracle12cDialect() {
		super();
//		registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());
//		registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
//		registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.STRING.getName());
//		registerHibernateType(Types.DECIMAL, StandardBasicTypes.DOUBLE.getName());
		registerHibernateType(Types.NCLOB, StandardBasicTypes.STRING.getName());
//		registerFunction( "date", new SQLFunctionTemplate(StandardBasicTypes.DATE, "to_date(?1,'yyyy-MM-dd')") );
//		registerFunction( "sum_day", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1") );
//		registerFunction( "sum_day2", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1+?2"));
//		registerFunction( "sum_month", new SQLFunctionTemplate(StandardBasicTypes.DATE, "add_months(?1,?2)") );
		}
}
