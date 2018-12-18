package ba.unsa.etf.rpr;

import java.util.ArrayList;

public class GeografijaDAO {
    public static void removeInstance() {
    }

    public static GeografijaDAO getInstance() {
        return new GeografijaDAO();
    }

    public ArrayList<Grad> gradovi() {
        return new ArrayList<>();
    }

    public Grad glavniGrad(String glavniGrad) {
        return new Grad();
    }

    public void obrisiDrzavu(String drzava) {
    }

    public Drzava nadjiDrzavu(String drzava) {
        return new Drzava();
    }

    public void dodajGrad(Grad grad) {
    }

    public void dodajDrzavu(Drzava drzava) {
    }

    public void izmijeniGrad(Grad grad) {
    }
}
