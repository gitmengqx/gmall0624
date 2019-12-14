package com.atguigu.gmall0624.bean;

import lombok.Data;
import lombok.extern.log4j.Log4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class BaseAttrInfo implements Serializable{

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 表示获取主键自增！
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;

    // attrValueList 字段不属于数据库，添加一个注解 属性名称不能随便换！
    @Transient
    private List<BaseAttrValue> attrValueList;

    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        strings.add("4");
        strings.add("5");
        // 增强for 循环
//        for (String string : strings) {
//            if ("2".equals(string)){
//                strings.remove(string);
//            }
//        }
        for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            if ("2".equals(next)){
                //strings.remove(next);
                iterator.remove();
            }
        }
        System.out.println(strings);
//        for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
//            String next = iterator.next();
//            if ("2".equals(next)){
//                strings.remove(next);
//            }
//        }
//        for (int i = 0; i < strings.size(); i++) {
//            String s = strings.get(i);
//            if ("2".equals(s)){
//                strings.remove(s);
//            }
//        }

    }

}
