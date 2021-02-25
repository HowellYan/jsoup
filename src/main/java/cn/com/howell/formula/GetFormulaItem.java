package cn.com.howell.formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetFormulaItem {
    public static void main(String[] args) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map = getItem("=消耗/((注册数+保皇)+444)+梵蒂冈/(事实上+是淡粉色的)", map);
    }

    /**
     * + - * / ( )
     * @param formula
     * @return
     */
    public static Map<String, List<String>> getItem(String formula,  Map<String, List<String>> map) {

        List<String> list = map.get("1");
        if(list == null) {
            list = new ArrayList<String>();
        }
        formula = formula.replace("=","");
        String[] formulas =  formula.split("[+ - * / ( )]");
        for (String i : formulas) {
            if(i.trim().length() != 0) {
                list.add(i);
            }
        }
        map.put("1",  list);

        Pattern p = Pattern.compile("\\(([^}]*)\\)");
        Matcher m = p.matcher(formula);
        while(m.find()){
            list.add(m.group().substring(1, m.group().length()-1));
        }

        System.out.println(list.toString());
        return map;
    }


}
