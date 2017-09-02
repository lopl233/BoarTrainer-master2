package algo;

import java.util.ArrayList;

public class Plan_Search {
    Plan_percent plan_percent = new Plan_percent();
    Plan_compare plan_compare = new Plan_compare();
    class Plan_compare{
        String name;
        public int percent;
        int id;
        public String toString(){
            return name + " " + percent;
        }
    }

    Plan_Search(String name, int plan_id, String age, String Height, String Weight, String Fraquency, String Advancement_level, String goal ){
        plan_compare.id = plan_id;
        plan_compare.name = name;
        plan_percent.fill();
        plan_compare.percent = plan_percent.Calculate(age, Height, Weight, Fraquency, Advancement_level, goal);
    }

}
