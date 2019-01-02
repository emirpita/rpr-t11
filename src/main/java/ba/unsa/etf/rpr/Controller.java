package main.java.ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {
    private static GeografijaDAO baza = null;
    public Button changeDrzavaBttn;
    public Button addDrzavaBttn;
    public Button changeGradBttn;
    public Button addGradBttn;
    public TableView<Drzava> tabelaDrzava;
    public TableView<Grad> tabelaGradova;
    public TextField textfield1;
    public TextField textfield2;
    public TextField textfield3;
    public TextField textfield4;
    public Button clearBttn;
    public Button deleteDrzavaBttn;
    public Button deleteGradBttn;
    public Button refreshBttn;
    public TableColumn<Drzava, Integer> idDrzavaKolona;
    public TableColumn<Drzava, String> nazivDrzaveKolona;
    public TableColumn<Drzava, Grad> glavniGradKolona;
    public TableColumn<Grad, Integer> idGradKolona;
    public TableColumn<Grad, String> nazivGradKolona;
    public TableColumn<Grad, Integer> brojStanovnikaKolona;
    public TableColumn<Grad, Drzava> drzavaIdKolona;
    private DrzavaModel modelDrzava;
    private GradModel modelGradova;

    public Controller(GeografijaDAO baza, DrzavaModel drzavaModel, GradModel gradModel) {
        Controller.baza = baza;
        modelDrzava = drzavaModel;
        modelGradova = gradModel;
    }

    @FXML
    public void initialize() {
        textfield1.setDisable(true);
        clearBttnOnClick(null);
        tabelaDrzava.setItems(modelDrzava.getDrzave());
        tabelaGradova.setItems(modelGradova.getGradovi());

        tabelaDrzava.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tabelaGradova.getSelectionModel().clearSelection();
                addDrzavaBttn.setDisable(true);
                addGradBttn.setDisable(true);
                changeGradBttn.setDisable(true);
                changeDrzavaBttn.setDisable(false);
                textfield4.setDisable(true);
                modelDrzava.setTrenutnaDrzava(newValue);
                textfield1.setText(String.valueOf(newValue.getId()));
                textfield2.setText(newValue.getNaziv());
                textfield3.setText(String.valueOf(newValue.getGlavniGrad().getId()));
            }
        });

        tabelaDrzava.setRowFactory(tv -> {
            TableRow<Drzava> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    changeDrzavaOnClick(null);
                }
            });
            return row;
        });

        tabelaGradova.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tabelaDrzava.getSelectionModel().clearSelection();
                addDrzavaBttn.setDisable(true);
                addGradBttn.setDisable(true);
                changeDrzavaBttn.setDisable(true);
                changeGradBttn.setDisable(false);
                textfield4.setDisable(false);
                modelGradova.setTrenutniGrad(newValue);
                textfield1.setText(String.valueOf(newValue.getId()));
                textfield2.setText(newValue.getNaziv());
                textfield3.setText(String.valueOf(newValue.getBrojStanovnika()));
                textfield4.setText(String.valueOf(newValue.getDrzava().getId()));
            }
        });

        tabelaGradova.setRowFactory(tv -> {
            TableRow<Grad> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    changeGradOnClick(null);
                }
            });
            return row;
        });

        idDrzavaKolona.setCellValueFactory(new PropertyValueFactory<>("idDrzava"));
        nazivDrzaveKolona.setCellValueFactory(new PropertyValueFactory<>("nazivDrzave"));
        glavniGradKolona.setCellValueFactory(new PropertyValueFactory<>("glavniGrad"));

        idGradKolona.setCellValueFactory(new PropertyValueFactory<>("idGrad"));
        nazivGradKolona.setCellValueFactory(new PropertyValueFactory<>("nazivGrad"));
        brojStanovnikaKolona.setCellValueFactory(new PropertyValueFactory<>("brojStanovnika"));
        drzavaIdKolona.setCellValueFactory(new PropertyValueFactory<>("drzava"));
    }

    private void showAlert(String title, String headerText) {
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle(title);
        error.setHeaderText(headerText);
        error.show();
    }

    public void changeDrzavaOnClick(ActionEvent actionEvent) {
        if (modelDrzava.getTrenutnaDrzava() == null)
            return;
        if (textfield4.isDisabled()) {
            int id = Integer.parseInt(textfield1.getText());
            String naziv = textfield2.getText();
            int glavniGradId = Integer.parseInt(textfield3.getText());
            baza.izmijeniDrzava(new Drzava(id, naziv, new Grad(glavniGradId, "", 0, null)));
            showAlert("Uspjeh", "Uspjesno izmjenjena drzava");
        }
    }

    public void addDrzavaOnClick(ActionEvent actionEvent) {
        if (textfield2.getText().isEmpty() || textfield4.getText().isEmpty())
            return;
        String naziv = textfield2.getText();
        String nazivGrada = textfield4.getText();
        baza.dodajDrzavu(new Drzava(0, naziv, new Grad(0, nazivGrada, 0, null)));
        showAlert("Uspjeh", "Uspjesno dodata drzava");
    }

    public void changeGradOnClick(ActionEvent actionEvent) {
        if (modelGradova.getTrenutniGrad() == null)
            return;
        if (!textfield4.isDisabled()) {
            int id = Integer.parseInt(textfield1.getText());
            String naziv = textfield2.getText();
            int brojStanovnika = Integer.parseInt(textfield3.getText());
            int drzavaId = Integer.parseInt(textfield4.getText());
            baza.izmijeniGrad(new Grad(id, naziv, brojStanovnika, new Drzava(drzavaId, "", null)));
            showAlert("Uspjeh", "Uspjesno izmjenjen grad");
        }
    }

    public void addGradOnClick(ActionEvent actionEvent) {
        if (textfield2.getText().isEmpty() || textfield4.getText().isEmpty())
            return;
        String naziv = textfield2.getText();
        int brojStanovnika = Integer.parseInt(textfield3.getText());
        String drzavaNaziv = textfield4.getText();
        baza.dodajGrad(new Grad(0, naziv, brojStanovnika, new Drzava(0, drzavaNaziv, null)));
        showAlert("Uspjeh", "Uspjesno dodat grad");
    }

    public void clearBttnOnClick(ActionEvent actionEvent) {
        textfield1.clear();
        textfield2.clear();
        textfield3.clear();
        textfield4.clear();
        textfield4.setDisable(false);
        changeGradBttn.setDisable(true);
        changeDrzavaBttn.setDisable(true);
        addDrzavaBttn.setDisable(false);
        addGradBttn.setDisable(false);
        tabelaDrzava.getSelectionModel().clearSelection();
        tabelaGradova.getSelectionModel().clearSelection();
        textfield1.setPromptText("Id");
        textfield2.setPromptText("Naziv");
        textfield3.setPromptText("Broj stanovnika (grad)");
        textfield4.setPromptText("Naziv grada / Naziv drzave");
    }

    public void deleteGradOnClick(ActionEvent actionEvent) {
        if (modelGradova.getTrenutniGrad() == null)
            return;
        baza.obrisiGrad(modelGradova.getTrenutniGrad().getNaziv());
        showAlert("Uspjeh", "Uspjesno izbrisan grad");
    }

    public void deleteDrzavaOnClick(ActionEvent actionEvent) {
        if (modelDrzava.getTrenutnaDrzava() == null)
            return;
        baza.obrisiDrzavu(modelDrzava.getTrenutnaDrzava().getNaziv());
        showAlert("Uspjeh", "Uspjesno izbrisana drzava");
    }

    public void refreshOnClick(ActionEvent actionEvent) {
        modelDrzava.refresh(baza);
        modelGradova.refresh(baza);
    }
}