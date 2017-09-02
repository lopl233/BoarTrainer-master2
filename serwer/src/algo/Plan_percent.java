package algo;

import java.sql.ResultSet;
import java.util.HashMap;

public class Plan_percent{
    String name = "";
    HashMap<String,HashMap<String,Integer>> percents = new HashMap<String, HashMap<String,Integer>>();
    void fill(){
        HashMap<String,Integer> hashmapa = new HashMap<String,Integer>();
        hashmapa.put("15-19",100);
        hashmapa.put("20-29",100);
        hashmapa.put("30-39",100);
        percents.put("Age", hashmapa);

        hashmapa = new HashMap<String,Integer>();
        hashmapa.put("140-149",100);
        hashmapa.put("150-159",100);
        hashmapa.put("160-169",100);
        hashmapa.put("170-179",100);
        hashmapa.put("180-189",100);
        hashmapa.put("190-199",100);
        hashmapa.put("200-209",100);
        hashmapa.put("210-219",100);
        percents.put("Height", hashmapa);

        hashmapa = new HashMap<String,Integer>();
        hashmapa.put("40-49",100);
        hashmapa.put("50-59",100);
        hashmapa.put("60-74",100);
        hashmapa.put("75-89",100);
        hashmapa.put("90-119",100);
        hashmapa.put("120-199",100);
        percents.put("Weight",  hashmapa);

        hashmapa = new HashMap<String,Integer>();
        hashmapa.put("1-2",100);
        hashmapa.put("3-4",85);
        hashmapa.put("5-6",100);
        hashmapa.put("7 i więcej",100);
        percents.put("Fraquency",  hashmapa);

        hashmapa = new HashMap<String,Integer>();
        hashmapa.put("Podstawowy",90);
        hashmapa.put("Średnio zaawansowany",100);
        hashmapa.put("Zaawansowany",100);
        percents.put("Advancement level",  hashmapa);

        hashmapa = new HashMap<String,Integer>();
        hashmapa.put("Wytrzymałość",100);
        hashmapa.put("Redukcja",80);
        hashmapa.put("Siła",90);
        percents.put("Goal",  hashmapa);
    }
    void fill(ResultSet rs){

    }
    int Calculate(String Age, String Height, String Weight, String Fraquency, String Advancement_level, String Goal  ){
        int result = 100;
        result*= percents.get("Age").get(Age);
        result /=100;
        result*= percents.get("Height").get(Height);
        result /=100;
        result*= percents.get("Weight").get(Weight);
        result /=100;
        result*= percents.get("Fraquency").get(Fraquency);
        result /=100;
        result*= percents.get("Advancement level").get(Advancement_level);
        result /=100;
        result*= percents.get("Goal").get(Goal);
        result /=100;
        return  result;
    }

    Plan_percent(){
        fill();
    }
}

