package view;

import control.MainController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class InteractionPanelHandler {
    private JTextArea output;
    private JTextField stationInNetwork;
    private JButton insertButton;
    private JTextField otherStation;
    private JButton newConnectionButton;
    private JButton killConnectionButton;
    private JTextField stationFrom;
    private JTextField stationTo;
    private JButton searchButton;
    private JButton deleteButton;
    private JPanel panel;
    private JTextArea systemOutput;
    private JButton searchShortestPathButton;
    private JTextField weight;

    private MainController mainController;

    public InteractionPanelHandler(MainController mainController) {
        this.mainController = mainController;
        createButtons();
        update();
    }

    private void createButtons(){
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!stationInNetwork.getText().isEmpty()){
                    String name = stationInNetwork.getText();
                    if(mainController.insertStation(name)){
                        update();
                        addToSysoutput("Es wurde die Station " + name + " hinzugefügt.");
                    }else{
                        addToSysoutput("Die Station " + name + " existiert bereits. Der Graph wurde nicht verändert.");
                    }
                }else{
                    addToSysoutput("Bitte geben Sie den Namen einer Station ein.");
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!stationInNetwork.getText().isEmpty()){
                    String name = stationInNetwork.getText();
                    if(mainController.deleteStation(name)){
                        update();
                        addToSysoutput("Es wurde die Station " + name + " gelöscht. Ebenfalls wurden alle Verbindungen zu dieser Station entfernt.");
                    }else{
                        addToSysoutput("Die Station  " + name + " existiert nicht.");
                    }
                }else{
                    addToSysoutput("Bitte geben Sie den Namen einer Station ein.");
                }
            }
        });
        newConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!stationInNetwork.getText().isEmpty() && !otherStation.getText().isEmpty()){
                    String start = stationInNetwork.getText();
                    String destination = otherStation.getText();
                    int pWeight = Integer.valueOf(weight.getText());
                    if(mainController.connect(start, destination, pWeight)){
                        update();
                        addToSysoutput("Die Stationen " + start + " und " + destination + " sind nun verbunden.");
                    }else{
                        addToSysoutput("Die Station " + start + " oder die Station " + destination + " existiert nicht oder sie waren bereits verbunden. Der Graph wurde nicht verändert.");
                    }
                }else{
                    addToSysoutput("Sie müssen zwei Stationen angeben.");
                }
            }
        });
        killConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!stationInNetwork.getText().isEmpty() && !otherStation.getText().isEmpty()){
                    String start = stationInNetwork.getText();
                    String destination = otherStation.getText();
                    if(mainController.disconnect(start, destination)){
                        update();
                        addToSysoutput("Die Verbindung zwischen " + start + " und " + destination + " wurde gekappt. SEV wird eingerichtet!");
                    }else{
                        addToSysoutput("Die Station " + start + " oder die Station " + destination + " existiert nicht oder sie waren vorher schon nicht verbunden. Der Graph wurde nicht verändert.");
                    }
                }else{
                    addToSysoutput("Sie müssen zwei Stationen angeben.");
                }
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!stationFrom.getText().isEmpty() && !stationTo.getText().isEmpty()){
                    String from = stationFrom.getText();
                    String to   = stationTo.getText();
                    String[] links = mainController.getLinksBetween(from, to);
                    if(links != null){
                        String str = "";
                        for(int i = 0; i < links.length; i++){
                            str = str + links[i];
                            if(i != links.length - 1){
                                str = str + " -> ";
                            }
                        }                        
                        addToSysoutput(str);
                    }else{
                        addToSysoutput("Es wurde keine Verbindung zwischen " + from + " und " + to + " gefunden.");
                    }
                }else{
                    addToSysoutput("Für die Suche müssen Sie zwei Stationen angeben.");
                }
            }
        });


        searchShortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!stationFrom.getText().isEmpty() && !stationTo.getText().isEmpty()){
                    String from = stationFrom.getText();
                    String to   = stationTo.getText();
                    String[] path = mainController.shortestPath(from, to);
                    if(path != null){
                        String str = "";
                        for(int i = 0; i < path.length; i++){
                            str = str + path[i];
                            if(i != path.length - 1){
                                str = str + " -> ";
                            }
                        }
                        addToSysoutput(str);
                    }else{
                        addToSysoutput("Es wurde keine Verbindung zwischen " + from + " und " + to + " gefunden.");
                    }
                }else{
                    addToSysoutput("Für die Suche müssen Sie zwei Stationen angeben.");
                }
            }
        });

    }

    public JPanel getPanel(){
        return panel;
    }

    private void update(){
        resetOutput();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
        df.setRoundingMode(RoundingMode.HALF_UP);

        String[] allStations = mainController.getTrainNetwork();
        if(allStations != null){
            for(int i = 0; i < allStations.length; i++){
                String[] connectedStations = mainController.getAllConnectedStationsFrom(allStations[i]);
                int amount = 0;
                String allConnectedStations = "";
                if(connectedStations != null){
                    amount = connectedStations.length;

                    for(int j = 0; j < amount; j++){
                        if(j != amount - 1){
                            allConnectedStations = allConnectedStations + connectedStations[j] + ", ";
                        }else{
                            allConnectedStations = allConnectedStations + connectedStations[j];
                        }

                    }
                }
                double cd = Double.parseDouble(df.format(mainController.centralityDegreeOfStation(allStations[i])));

                addToOutput(amount, cd, allStations[i], allConnectedStations);
            }
        }

        double dense = Double.parseDouble(df.format(mainController.dense()));
        addToOutput(dense);
    }

    private void resetOutput(){
        String str = " #Direkt | Zentralitätsgrad |       Station       |       Direkt zu"+"\n";
        str = str +  "---------------------------------------------------------------------------------------------"+"\n";
        output.setText(str);
    }

    private void addToOutput(int amount, double connection, String name, String neighbours){
        String str = " ";
        for(int i = 0; i < 8-String.valueOf(amount).length(); i++){
            str = str + " ";
        }
        str = str + amount;
        str = str + " | " + connection;
        for(int i = 0; i < 16-String.valueOf(connection).length();i++){
            str = str + " ";
        }
        str = str + " | " + name;
        for(int i = 0; i < 18-name.length(); i++){
            str = str + " ";
        }
        str = str + " | " + neighbours + "\n";
        output.setText(output.getText()+str);
    }

    private void addToOutput(double dense){
        String str = "\n\n\nDichte des Netzwerks: " + dense;
        output.setText(output.getText()+str);
    }

    private void addToSysoutput(String text){
        systemOutput.setText(systemOutput.getText()+"\n"+text);
    }
}
