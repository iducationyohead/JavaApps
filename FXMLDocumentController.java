/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fmxlcalculator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

/**
 *
 * @author Jeremy H @ PCGeeks, llc.
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Button btnZero;

    public Button getBtnZero(int z) {
        int z = 0;
        return z;
    }

    public void setBtnZero(Button btnZero) {
        this.btnZero = btnZero;
    }

    public Button getBntDot() {
        return bntDot;
    }

    public void setBntDot(Button bntDot) {
        this.bntDot = bntDot;
    }

    public Button getBtnEquals() {
        return btnEquals;
    }

    public void setBtnEquals(Button btnEquals) {
        this.btnEquals = btnEquals;
    }

    public Button getBtnAdd() {
        return btnAdd;
    }

    public void setBtnAdd(Button btnAdd) {
        this.btnAdd = btnAdd;
    }

    public Button getBtnOne() {
        return btnOne;
    }

    public void setBtnOne(Button btnOne) {
        this.btnOne = btnOne;
    }

    public Button getBtnTwo() {
        return btnTwo;
    }

    public void setBtnTwo(Button btnTwo) {
        this.btnTwo = btnTwo;
    }

    public Button getBtnThree() {
        return btnThree;
    }

    public void setBtnThree(Button btnThree) {
        this.btnThree = btnThree;
    }

    public Button getBtnSubtract() {
        return btnSubtract;
    }

    public void setBtnSubtract(Button btnSubtract) {
        this.btnSubtract = btnSubtract;
    }

    public Button getBtnFour() {
        return btnFour;
    }

    public void setBtnFour(Button btnFour) {
        this.btnFour = btnFour;
    }

    public Button getBtnFive() {
        return btnFive;
    }

    public void setBtnFive(Button btnFive) {
        this.btnFive = btnFive;
    }

    public Button getBtnSix() {
        return btnSix;
    }

    public void setBtnSix(Button btnSix) {
        this.btnSix = btnSix;
    }

    public Button getBtnDivide() {
        return btnDivide;
    }

    public void setBtnDivide(Button btnDivide) {
        this.btnDivide = btnDivide;
    }

    public Button getBtnSeven() {
        return btnSeven;
    }

    public void setBtnSeven(Button btnSeven) {
        this.btnSeven = btnSeven;
    }

    public Button getBtnEight() {
        return btnEight;
    }

    public void setBtnEight(Button btnEight) {
        this.btnEight = btnEight;
    }

    public Button getBtnNine() {
        return btnNine;
    }

    public void setBtnNine(Button btnNine) {
        this.btnNine = btnNine;
    }

    public Button getBtnMultiply() {
        return btnMultiply;
    }

    public void setBtnMultiply(Button btnMultiply) {
        this.btnMultiply = btnMultiply;
    }

    public DialogPane getTxtWindow() {
        return txtWindow;
    }

    public void setTxtWindow(DialogPane txtWindow) {
        this.txtWindow = txtWindow;
    }

    

    public FXMLDocumentController(Button btnZero, Button bntDot, Button btnEquals, Button btnAdd, Button btnOne, Button btnTwo, Button btnThree, Button btnSubtract, Button btnFour, Button btnFive, Button btnSix, Button btnDivide, Button btnSeven, Button btnEight, Button btnNine, Button btnMultiply, DialogPane txtWindow) {
        this.btnZero = btnZero;
        this.bntDot = bntDot;
        this.btnEquals = btnEquals;
        this.btnAdd = btnAdd;
        this.btnOne = btnOne;
        this.btnTwo = btnTwo;
        this.btnThree = btnThree;
        this.btnSubtract = btnSubtract;
        this.btnFour = btnFour;
        this.btnFive = btnFive;
        this.btnSix = btnSix;
        this.btnDivide = btnDivide;
        this.btnSeven = btnSeven;
        this.btnEight = btnEight;
        this.btnNine = btnNine;
        this.btnMultiply = btnMultiply;
        this.txtWindow = txtWindow;
    }
    @FXML
    private Button bntDot;
    @FXML
    private Button btnEquals;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnOne;
    @FXML
    private Button btnTwo;
    @FXML
    private Button btnThree;
    @FXML
    private Button btnSubtract;
    @FXML
    private Button btnFour;
    @FXML
    private Button btnFive;
    @FXML
    private Button btnSix;
    @FXML
    private Button btnDivide;
    @FXML
    private Button btnSeven;
    @FXML
    private Button btnEight;
    @FXML
    private Button btnNine;
    @FXML
    private Button btnMultiply;
    @FXML
    private DialogPane txtWindow;
    @FXML
    private Button btnExit;
    
  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
