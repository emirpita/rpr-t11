package main.java.ba.unsa.etf.rpr;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Main extends Application {
    private static GeografijaDAO baza = GeografijaDAO.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    private static void glavniGrad() {
        Scanner ulaz = new Scanner(System.in);
        String drzava = ulaz.nextLine();
        var grad = baza.glavniGrad(drzava);
        System.out.println("Glavni grad države " + grad.getDrzava().getNaziv() + " je " + grad.getNaziv());
    }

    public static String ispisiGradove() {
        var gradovi = baza.gradovi();
        String result = "";
        for (var grad : gradovi)
            result += grad.gradUString() + "\n";
        return result;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        DrzavaModel drzavaModel = new DrzavaModel(baza);
        GradModel gradModel = new GradModel(baza);
        loader.setController(new Controller(baza, drzavaModel, gradModel));
        Parent root = loader.load();
        primaryStage.setTitle("Baza podataka");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        primaryStage.show();
    }

}
/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    public static void main(String[] args)
    {
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
            statement.execute("INSERT INTO grad VALUES(1, 'Pariz', 2200000, 1)");
            statement.execute("INSERT INTO grad VALUES(2, 'London', 8136000, 2)");
            statement.execute("INSERT INTO grad VALUES(3, 'Manchester', 2200000, 2)");
            statement.execute("INSERT INTO grad VALUES(4, 'Bec', 1868000, 3)");
            statement.execute("INSERT INTO grad VALUES(5, 'Graz', 283869, 3)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Sve OK");
        statement.execute("DROP TABLE main.drzava;");
        statement.execute("DROP TABLE main.grad;");
        statement.execute("alter table drzava drop column glavni_grad");
        statement.execute("alter table drzava add glavni_grad int references drzava(id);");
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
}*/

