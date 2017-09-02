package algo;


import java.sql.SQLException;

public class test {

    public static void main(String[] args) throws SQLException {

        System.out.println(new Fasade().Calculate("20-29", 100, 100, "3-4", "Podstawowy", "Siła"));
        System.exit(0);
    }
}
