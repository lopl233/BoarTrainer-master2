public class User_params {
    int age;
    int height;
    int weight;
    String fraquency;
    String Advancement_level;
    String goal;
    User_params(int age, int height, int weight, String fraquency, String Advancement_level, String goal){
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.fraquency = fraquency;
        this.Advancement_level = Advancement_level;
        this.goal = goal;
    }
    public String toString(){
        return String.format("Age : %s, Height : %s, Weight : %s, Fraquency : %s, Advancement_level : %s, , Goal : %s,", age, height, weight, fraquency, Advancement_level, goal);
    }
}
