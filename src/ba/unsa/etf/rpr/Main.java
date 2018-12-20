package ba.unsa.etf.rpr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:baza.db");
            System.out.println("Radi driver");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            System.out.println("Uspostavljena veza");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.execute("CREATE TABLE drzava(id INT PRIMARY KEY ,naziv VARCHAR not null )");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.execute("CREATE TABLE grad(id int primary key, naziv varchar, broj_stanovnika int, drzava int references drzava(id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.execute("ALTER TABLE drzava add column glavni_grad int references drzava(id);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.execute("INSERT INTO grad VALUES(1, Pariz, 2200000, 1)");
            statement.execute("INSERT INTO grad VALUES(2, London, 8136000, 2)");
            statement.execute("INSERT INTO grad VALUES(3, Manchester, 2200000, 2)");
            statement.execute("INSERT INTO grad VALUES(4, Bec, 1868000, 3)");
            statement.execute("INSERT INTO grad VALUES(5, Graz, 283869, 3)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Sve OK");
        /*statement.execute("DROP TABLE main.drzava;");
        statement.execute("DROP TABLE main.grad;");
        statement.execute("alter table drzava drop column glavni_grad");
        statement.execute("alter table drzava add glavni_grad int references drzava(id);");*/
        System.out.println("Gradovi su:\n" + ispisiGradove());
        glavniGrad();
    }

    public static void glavniGrad() {
        String unos;
        System.out.println("Unesite naziv grada: ");
        Scanner scanner = new Scanner(System.in);
        unos = scanner.nextLine();
        if (GeografijaDAO.getInstance().nadjiDrzavu(unos) == null)
            System.out.println("Nepostojeca drzava");
        else {
            Drzava d = GeografijaDAO.getInstance().nadjiDrzavu(unos);
            System.out.println("Glavni grad drzave " + d.getNaziv() + " je " + d.getGlavniGrad().getNaziv());
        }
    }

    public static String ispisiGradove() {
        ArrayList<Grad> gradovi = GeografijaDAO.getInstance().gradovi();
        String pov = "";
        for (Grad g : gradovi) {
            pov += g;
        }
        return pov;
    }
}

