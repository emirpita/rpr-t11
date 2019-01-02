package main.java.ba.unsa.etf.rpr;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance;
    private Connection connection;
    private String databaseURL = "jdbc:sqlite:baza.db";
    private PreparedStatement statement;

    private GeografijaDAO() {
        boolean init = !databaseExists();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseURL);
            //statement = connection.prepareStatement();
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
        try {
            statement = connection.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            ResultSet resultGradovi = statement.executeQuery();
            while (resultGradovi.next()) {
                Grad grad = new Grad();
                int idGrad = resultGradovi.getInt(1);
                grad.setId(idGrad);
                String nazivGrad = resultGradovi.getString(2);
                grad.setNaziv(nazivGrad);
                int brojStanovnika = resultGradovi.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int drzavaId = resultGradovi.getInt(4);
                grad.setDrzava(new Drzava(drzavaId, "", null));
                gradovi.add(grad);
            }
            statement = connection.prepareStatement("SELECT * FROM drzava");
            ResultSet resultDrzave = statement.executeQuery();
            while (resultDrzave.next()) {
                Drzava drzava = new Drzava();
                int idDrzava = resultDrzave.getInt(1);
                drzava.setId(idDrzava);
                String nazivDrzave = resultDrzave.getString(2);
                drzava.setNaziv(nazivDrzave);
                int glavniGradId = resultDrzave.getInt(3);
                for (var grad : gradovi) {
                    if (grad.getDrzava().getId() == drzava.getId()) {
                        grad.setDrzava(drzava);
                    }
                    if (glavniGradId == grad.getId())
                        drzava.setGlavniGrad(grad);
                }
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
        return gradovi;
        /*String query = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gradovi;*/
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

    public ArrayList<Drzava> drzave() {
        ArrayList<Drzava> drzave = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT d.id, d.naziv, d.glavni_grad FROM drzava d, grad g WHERE d.glavni_grad = g.id ORDER BY broj_stanovnika DESC");
            ResultSet resultDrzave = statement.executeQuery();
            while (resultDrzave.next()) {
                Drzava drzava = new Drzava();
                int idDrzava = resultDrzave.getInt(1);
                drzava.setId(idDrzava);
                String nazivDrzave = resultDrzave.getString(2);
                drzava.setNaziv(nazivDrzave);
                int gradId = resultDrzave.getInt(3);
                PreparedStatement podUpit = connection.prepareStatement("SELECT * FROM grad WHERE id = ?");
                podUpit.setInt(1, gradId);
                ResultSet resultGradovi = podUpit.executeQuery();
                Grad grad = new Grad();
                while (resultGradovi.next()) {
                    int idGrad = resultGradovi.getInt(1);
                    grad.setId(idGrad);
                    String nazivGrad = resultGradovi.getString(2);
                    grad.setNaziv(nazivGrad);
                    int brojStanovika = resultGradovi.getInt(3);
                    grad.setBrojStanovnika(brojStanovika);
                    grad.setDrzava(drzava);
                }
                drzava.setGlavniGrad(grad);
                drzave.add(drzava);
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
        return drzave;
    }

    public void izmijeniDrzava(Drzava drzava) {
        try {
            statement = connection.prepareStatement("UPDATE drzava SET naziv = ?, glavni_grad = ? WHERE id = ?");
            statement.setString(1, drzava.getNaziv());
            statement.setInt(2, drzava.getGlavniGrad().getId());
            statement.setInt(3, drzava.getId());


            int indeksReda = statement.executeUpdate();
            System.out.println("Uspjesno izmjenjen " + indeksReda + " red");
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public void obrisiGrad(String naziv) {
        try {
            statement = connection.prepareStatement("SELECT d.id FROM grad g, drzava d WHERE d.glavni_grad = g.id AND g.naziv = ?");
            statement.setString(1, naziv);
            ResultSet result = statement.executeQuery();
            int brojac = 0;
            while (result.next()) {
                int idDrzava = result.getInt(1);
                PreparedStatement podUpit = connection.prepareStatement("DELETE FROM drzava WHERE id = ?");
                podUpit.setInt(1, idDrzava);
                podUpit.executeUpdate();
                brojac++;
            }
            if (brojac == 0)
                return;
            statement = connection.prepareStatement("DELETE FROM grad WHERE naziv = ?");
            statement.setString(1, naziv);
            statement.executeUpdate();
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }
}

