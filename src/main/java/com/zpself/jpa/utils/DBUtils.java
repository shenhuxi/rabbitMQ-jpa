package com.zpself.jpa.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


/**
 * 
* Title: StringUtils 
* Description:  数据库操作工具类
* @author dicky  
* @date 2018年7月5日 上午10:16:11
 */
public class DBUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(DBUtils.class);
	
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	private DBUtils() {}

	   /**
     * 根据查询器中的SQL语句拆分出需要查询的字段（属性）
     *
     * @param SQL 【需要解析的SQL语句】
     * @return 【返回字段名的数组】
     */
    public static String[] getFields(String SQL) {
        if (StringUtils.isEmpty(SQL)) {
            return EMPTY_STRING_ARRAY;
        }
        // 替换掉子查询
        String sql = replaceAllKh(SQL);
        // 获取from之前的SQL
        sql = getFromBefore(sql);

        if (sql.length() <= 6 || sql.indexOf("* ") != -1) {
            return EMPTY_STRING_ARRAY;
        }
        // 去掉第1个select
        sql = sql.substring(6, sql.length()).trim();
        // 如果有多个select 说明是复杂sql
        if (sql.indexOf("select ") != -1) {
            LOG.error("SQL语句解析错误【{}】",SQL);
            return EMPTY_STRING_ARRAY;
        }

        sql = sql.replaceAll("\\(.*?\\)", "");
        String[] atts = sql.trim().split(",");
        for (int i = 0; i < atts.length; i++) {
            String att = atts[i].trim();
            if (att.indexOf("*") != -1) {
            	atts[0] = "ALL_COLUMNS";
                return atts;
            }
            String[] tems = att.split(" ");
            String tem = "";
            if (tems.length > 1) {
                tem = tems[tems.length - 1].trim();
            } else {
                tem = att.trim();
            }
            atts[i] = tem.substring(tem.indexOf(".") + 1, tem.length());
        }
        return atts;
    }
    
    /**
     * 把小括号()及括号中的内容替换成空格
     *
     * @param SQL
     * @return
     */
    private static String replaceAllKh(String SQL) {
        if (StringUtils.isEmpty(SQL)) {
            return "";
        }
        String mysql = SQL;
        int b = mysql.lastIndexOf("(");
        int e = mysql.indexOf(")");
        String temp = mysql;
        while (b > e) {
            temp = temp.substring(0, e) + " " + temp.substring(e + 1, temp.length());
            e = temp.indexOf(")");
        }
        if (b != -1) {
            temp = "";
            for (int i = b; i <= e; i++) {
                temp += " ";
            }
            mysql = mysql.substring(0, b) + temp + mysql.substring(e + 1, mysql.length());
        }
        if (mysql.lastIndexOf("(") != -1) {
            mysql = replaceAllKh(mysql);
        }
        return mysql;
    }
    
    /**
     * 截取SQL语句中from之前的字符串内容【不包含from】
     *
     * @param SQL
     * @return
     */
    public static String getFromBefore(String SQL) {
        if (StringUtils.isEmpty(SQL)) {
            return "";
        }
        // 把SQL语句变成全小写
        String sql = SQL.trim().toLowerCase();
        // 去掉条件中的''符号
        sql = sql.replaceAll("''", "  ");

        // 去掉条件中字符串类型的常量值
        sql = replaceAllDyh(sql);

        // 去掉带括号的子查询
        sql = replaceAllKh(sql);

        int b = sql.lastIndexOf(" from ");

        if (b != -1) {
            return SQL.substring(0, b).trim();
        } else {
            return SQL.trim();
        }
    }
    /**
     * 把条件中的单引号''及引号中的内容替换成功空格
     *
     * @param sql
     * @return
     */
    private static String replaceAllDyh(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return "";
        }
        String mysql = sql;
        int b = mysql.indexOf("'");
        if (b != -1) {
            mysql = mysql.substring(0, b) + " " + mysql.substring(b + 1, mysql.length());
        }
        int e = mysql.indexOf("'");
        if (e != -1) {
            StringBuilder temp = new StringBuilder("");
            for (int i = b; i <= e; i++) {
                temp.append(" ");
            }
            mysql = mysql.substring(0, b) + temp + mysql.substring(e + 1, mysql.length());
        }
        if (mysql.indexOf("'") != -1) {
            mysql = replaceAllDyh(mysql);
        }
        return mysql;
    }
    /**
	 * 获取对象表名
	 * 
	 * @author shixh
	 * @param clazz
	 * @return
	 */
	public static String getTableName(Class clazz) {
		Table annotation = (Table) clazz.getAnnotation(Table.class);
		if (annotation != null) {
			return annotation.name();
		}
		return ColumnUtil.camelToUnderline(clazz.getSimpleName());
	}
	

    /**
     * 把对象数组转换成实体对象
     * 
     * @param list
     *            【需要转换的对象数组集合】
     * @param atts
     *            【对象数组对应的属性数组】
     * @param clz
     *            【实体对象类】
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T>  List<T> parseObjects(List<Object[]> list, String[] atts, Class clz) throws Exception {
        List<T> result = new ArrayList<>();
        for (int i = 0; list != null && i < list.size(); i++) {
            Object[] objs = null;
            if(list.get(i) instanceof Object[]){
            	objs =list.get(i) ;
            }else{
            	objs=new Object[]{list.get(i)};
            }         
            if (objs.length > atts.length + 1 || objs.length < atts.length) {
                LOG.error("你的sql语句太复杂系统目前不支持！sql语句中列数：{}；返回结果列数：{}", atts.length,objs.length);
                throw new Exception("你的sql语句太复杂系统目前不支持！sql语句中列数：" + atts.length + "；返回结果列数：" + objs.length);
            } else {
                Object vo = clz.newInstance();
                for (int j = 0; j < atts.length; j++) {
                    if (objs[j] != null) {
                        BeanUtils.setProperty(vo, atts[j], objs[j]);
                    } else if (BeanUtils.getProperty(vo, atts[j]) != null) {
                        BeanUtils.setProperty(vo, atts[j], null);
                    }
                }
                result.add((T) vo);
            }
        }
        return result;
    }
}
