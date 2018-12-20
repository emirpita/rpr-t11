package ba.unsa.etf.rpr;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private Connection connection;
    private String databaseURL = "jdbc:sqlite:baza.db";
    private Statement statement;


    private GeografijaDAO() {
        boolean init = !databaseExists();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseURL);
            statement = connection.createStatement();
            if (init) {
                initializeData();
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    public static void removeInstance() {
        instance = null;
    }

    public static GeografijaDAO getInstance() {
        if (instance == null)
            initialize();
        return instance;
    }

    private void initializeData() {
    }

    private boolean databaseExists() {
        File db = new File("/baza.db");
        return db.exists();
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradovi = new ArrayList<>();

        String query = "SELECT * from grad;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TODO sortirati gradove!!!
        return gradovi;
    }

    public Grad glavniGrad(String drzava) {
        String query = "select g.* from grad g, drzava d where d.naziv=? and d.glavni_grad = g.id";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, drzava);
            ResultSet resultSet = preparedStatement.executeQuery();
            int id = resultSet.getInt("g.id");
            String naziv = resultSet.getString("g.naziv");
            int brojStan = resultSet.getInt("g.broj_stanovnika");
            return new Grad(id, naziv, brojStan, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Drzava nadjiDrzavu(String drzava) {
        String query = "select * from drzava d where d.naziv=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, drzava);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isClosed())
                return null;

            int drzavaID = resultSet.getInt("id");
            Integer gradID = resultSet.getInt("glavni_grad");
            Drzava novaDrzava = new Drzava(drzavaID, drzava, null);
            Grad gGrad = glavniGrad(drzava);
            gGrad.setDrzava(novaDrzava);
            novaDrzava.setGlavniGrad(gGrad);
            return novaDrzava;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // zanemaren id
    public void dodajGrad(Grad grad) {
        String query = "insert into grad values(-1, ?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, grad.getNaziv());
            preparedStatement.setInt(2, grad.getBrojStanovnika());
            preparedStatement.setNull(3, 0);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // mozda treba malo poraditi na ovome
    public void dodajDrzavu(Drzava drzava) {
        String query = "insert into drzava values(-1, ?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, drzava.getNaziv());
            this.dodajGrad(drzava.getGlavniGrad());
            preparedStatement.setNull(3, 0);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // mozda treba malo popraviti, koristiti opet prepared statement
    public void izmijeniGrad(Grad grad) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM grad WHERE naziv=" + grad.getNaziv() + ";");
            this.dodajGrad(grad);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void obrisiDrzavu(String drzava) {
        try {
            Statement statement = connection.createStatement();
            // prvo brisemo gradove
            statement.execute("DELETE FROM grad WHERE drzava=(SELECT id FROM drzava WHERE naziv=" + drzava + ";");
            // zatim brisemo drzavu
            statement.execute("DELETE FROM drzava WHERE naziv=" + drzava + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
