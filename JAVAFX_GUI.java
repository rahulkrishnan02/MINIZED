  package sample;

import eu.hansolo.medusa.Gauge;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jssc.SerialPort.MASK_RXCHAR;

public class Main extends Application {
    Gauge gauge = new Gauge();
    java.awt.Button btn = new java.awt.Button();
    SerialPort arduinoPort = null;
    ObservableList<String> portList;

    Label labelValue;
    final int NUM_OF_POINT = 50;
    XYChart.Series series;
    private void detectPort() {

        portList = FXCollections.observableArrayList();

        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        gauge.setTitle("DHOOM");
        gauge.setMaxValue(255);
        gauge.setMinValue(0);
        detectPort();
        final ComboBox comboBoxPorts = new ComboBox(portList);
        comboBoxPorts.valueProperty()
                .addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {

                    System.out.println(newValue);
                    disconnectArduino();
                    connectArduino(newValue);
                });

        Button b = new Button();
        Date date = new Date();


        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        yAxis.setLabel("Voltage");

        final LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Arduino Uno A0 Analog Input");
        series = new XYChart.Series();
        series.setName("A0 analog input");
        lineChart.getData().add(series);
        lineChart.setAnimated(false);

//        //pre-load with dummy data
        for (int i = 0; i < NUM_OF_POINT; i++) {
            series.getData().add(new XYChart.Data(i, 0));
        }
//        //

        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                comboBoxPorts, lineChart, gauge);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 500, 400);

        primaryStage.setTitle(
                "arduino-er.blogspot.com: Java + JavaFX + jSSC demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void shiftSeriesData(float newValue) {
        for (int i = 0; i < NUM_OF_POINT - 1; i++) {
            Date date = new Date();

            XYChart.Data<String, Number> ShiftDataUp =
                    (XYChart.Data<String, Number>) series.getData().get((i+1));
            Number shiftValue = ShiftDataUp.getYValue();
            XYChart.Data<String, Number> ShiftDataDn =
                    (XYChart.Data<String, Number>) series.getData().get(i);
            ShiftDataDn.setYValue(shiftValue);
        }
        XYChart.Data<String, Number> lastData =
                (XYChart.Data<String, Number>) series.getData().get(NUM_OF_POINT - 1);
        lastData.setYValue(newValue);
    }
    public boolean connectArduino(String port) {

        System.out.println("connectArduino");

        boolean success = false;
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setEventsMask(MASK_RXCHAR);
            final int[] k = {0}, a = {0};


            String filename = "C:\\Users\\ironm\\Desktop\\FORMULA MANIPAL\\jj1.xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("excelsheet");

            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if (serialPortEvent.isRXCHAR()) {
                    k[0]++;
                    try {
                        int l;

//                            String b = serialPort.readString();
                        byte[] b1 = serialPort.readBytes();
                        int value1 = b1[0] & 0xff;    //convert to int
                        String st1 = String.valueOf(value1);
                        int her = Integer.parseInt(st1);


//
                        HSSFRow row = sheet.createRow(k[0]);
                        System.out.println(k[0]);
                        row.createCell(0).setCellValue("value:");
                        row.createCell(1).setCellValue(her);
//                            System.out.println(st);

//                        }


                        FileOutputStream fileOut = new FileOutputStream(filename);

                        workbook.write(fileOut);
                        fileOut.close();
                        Platform.runLater(() -> {
//                            labelValue.setText(st);
                            shiftSeriesData((float) value1 * 5 / 255); //in 5V scale
                            gauge.setValue(Double.parseDouble(st1));
                        });

                    } catch (SerialPortException | IOException ex) {

                        Logger.getLogger(Main.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }

                }
            });

            arduinoPort = serialPort;
            success = true;
        } catch (SerialPortException ex) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex.toString());
        }

        return success;
    }

    public void disconnectArduino() {

        System.out.println("disconnectArduino()");
        if (arduinoPort != null) {
            try {
                arduinoPort.removeEventListener();

                if (arduinoPort.isOpened()) {
                    arduinoPort.closePort();
                }

            } catch (SerialPortException ex) {
                Logger.getLogger(Main.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void stop() throws Exception {

        disconnectArduino();
        super.stop();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
