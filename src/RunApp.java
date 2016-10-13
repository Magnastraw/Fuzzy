/**
 * Created by viro on 04.08.2016.
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class RunApp extends Application {

    //Названия нечетких пожмножеств
    private String name[][] = {{"Small enough", "Small", "Average", "Big", "Big enough"}, {"Very small", "Small enough", "Small", "Average", "Big", "Big enough", "Very big"}};

    public static void main(String[] args) {
        launch(args);
    }

    //Задает серии данных для трапециевидных фукнций принадлежности(одна серия-одна функция)
    private void setSeries(XYChart.Series series, FuzzySet set) {
        series.getData().add(new XYChart.Data(set.getMin(), 0));
        series.getData().add(new XYChart.Data(set.getMid(), set.getMidValue()));
        series.getData().add(new XYChart.Data(set.getMax(), 0));
    }

    //Задает серии данных для отображения степени принадлежности входной перменной нечеткому множества(одна серия-одна степень)
    private void setSeries(XYChart.Series series, FuzzySet set, double result[], double in, int i) {
        if (in > set.getMin() && in < set.getMax()) {
            if (in >= set.getMid()) {
                series.getData().add(new XYChart.Data(set.getMin(), 0));
                series.getData().add(new XYChart.Data((result[i] * (set.getMid() - set.getMin()) / set.getMidValue()) + set.getMin(), result[i]));
                series.getData().add(new XYChart.Data(in, result[i]));
                series.getData().add(new XYChart.Data(set.getMax(), 0));
            } else if (in < set.getMid()) {
                series.getData().add(new XYChart.Data(set.getMin(), 0));
                series.getData().add(new XYChart.Data(in, result[i]));
                series.getData().add(new XYChart.Data(set.getMax() - ((result[i] * (set.getMax() - set.getMid())) / set.getMidValue()), result[i]));
                series.getData().add(new XYChart.Data(set.getMax(), 0));
            }
        }
    }

    //Задает серии данных для отображения степеней принадлежности в результирубщей функции(одна серия-одна степень)
    private void setSeries(XYChart.Series series, FuzzySet set, double result[], int i) {

        series.getData().add(new XYChart.Data(set.getMin(), 0));
        series.getData().add(new XYChart.Data((result[i] * (set.getMid() - set.getMin()) / set.getMidValue()) + set.getMin(), result[i]));
        series.getData().add(new XYChart.Data(set.getMax() - ((result[i] * (set.getMax() - set.getMid())) / set.getMidValue()), result[i]));
        series.getData().add(new XYChart.Data(set.getMax(), 0));

    }

    //Задает серии данных для отображения текущего входного значения на графике
    private void setSeries(XYChart.Series series, double in) {
        series.getData().add(new XYChart.Data(in, 0));
        series.getData().add(new XYChart.Data(in, 1.0));
    }

    @Override
    public void start(Stage primaryStage) {
        //Создаем объект в котором расчитваем(ПЕРЕДЕЛАТЬ БЫ)
        Temperature temp = new Temperature();
        primaryStage.setTitle("FuzzyLogic");

        //оси
        final NumberAxis[] xAxis = new NumberAxis[3];
        final NumberAxis[] yAxis = new NumberAxis[3];
        //область графиков
        final AreaChart[] areaChart = new AreaChart[3];

        for (int i = 0; i < 3; i++) {
            xAxis[i] = new NumberAxis();
            yAxis[i] = new NumberAxis();
            areaChart[i] = new AreaChart<Number, Number>(xAxis[i], yAxis[i]);
        }

        //названия осей и графиков
        xAxis[0].setLabel("x");
        yAxis[0].setLabel("\u03bc(x)");

        xAxis[1].setLabel("\u03c6");
        yAxis[1].setLabel("μ(φ)");

        xAxis[2].setLabel("\u03c6");
        yAxis[2].setLabel("μ(θ)");

        areaChart[0].setTitle("Входные переменные");
        areaChart[2].setTitle("Выходные переменные");

        //отключаем символы(точки) и легенды на первых двух графиках, не нужны
        //так же отключаем анимацию для areChart а то багает при изменении серий
        for (int i = 0; i < 2; i++) {
            areaChart[i].setCreateSymbols(false);
            areaChart[i].setLegendVisible(false);
            areaChart[i].setAnimated(false);
        }

        //на последнем отключаем только символы, легенда идет как будто общая на всех, при желании можно убрать и запилить свою легенду
        areaChart[2].setCreateSymbols(false);
        areaChart[2].setAnimated(false);

        //Связный список серий данных
        //@series - серии трапециивидных функций принадлежности
        //@seriesResult - серии данных для отображения степени принадлежности какой то либо трапециивидной функции
        //@seriesCurrentVal - серии данных(не список) для отображения текущего значения на графике(прямая линия)
        ArrayList<XYChart.Series>[] series = new ArrayList[3];
        ArrayList<XYChart.Series>[] seriesResult = new ArrayList[3];
        XYChart.Series[] seriesCurrentVal = new XYChart.Series[3];
        series[2] = new ArrayList<>();
        seriesResult[2] = new ArrayList<>();

        //Первые два графика и третий разнесены в разные циклы из за разных setSeries()
        //заполняем серии данными
        for (int i = 0; i < 2; i++) {
            //создаем списки
            series[i] = new ArrayList<XYChart.Series>();
            seriesResult[i] = new ArrayList<XYChart.Series>();
            seriesCurrentVal[i] = new XYChart.Series<>();

            setSeries(seriesCurrentVal[i], temp.getTemp(i));

            for (int j = 0; j < temp.getSize(i); j++) {
                //добавляем по серии на график
                series[i].add(new XYChart.Series<>());
                seriesResult[i].add(new XYChart.Series<>());
                setSeries(series[i].get(j), temp.getSet(name[i][j], i));
                setSeries(seriesResult[i].get(j), temp.getSet(name[i][j], i), temp.getResult(i), temp.getTemp(i), j);
            }

        }

        for (int j = 0; j < temp.getSize(2); j++) {
            series[2].add(new XYChart.Series<>());
            seriesResult[2].add(new XYChart.Series<>());
            series[2].get(j).setName(name[1][j]);
            setSeries(series[2].get(j), temp.getSet(name[1][j], 2));
            setSeries(seriesResult[2].get(j), temp.getSet(name[1][j], 2), temp.getResult(2), j);
        }



        for (int i = 0; i < 3; i++) {
            for (XYChart.Series node : series[i]) {
                areaChart[i].getData().addAll(node);
            }
            for (XYChart.Series node : seriesResult[i]) {
                areaChart[i].getData().addAll(node);
            }
            //Условия стоит чтоб currentVal не вылезло за пределы массива
            if(i<2){
                areaChart[i].getData().addAll(seriesCurrentVal[i]);
            }
        }

        //Создаем VBox , в нем areChart располагают сверху вниз
        VBox root = new VBox();
        root.getChildren().addAll(areaChart[0], areaChart[1], areaChart[2]);
        Scene scene = new Scene(root, 900, 1000);

        //Делаем так чтоб цвета для серий больше 8 нормально отображались, без повторений
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 14; i++) {
                for (Node node : areaChart[j].lookupAll(".series" + i)) {
                    node.getStyleClass().remove("default-color" + (i % 8));
                    node.getStyleClass().add("default-color" + (i % 14));
                }
            }
        }

        //то же самое с легендой
        int k = 0;
        for (Node node : areaChart[2].lookupAll(".chart-legend-item")) {
            if (node instanceof Label && ((Label) node).getGraphic() != null) {
                ((Label) node).getGraphic().getStyleClass().remove("default-color" + (k % 8));
                ((Label) node).getGraphic().getStyleClass().add("default-color" + (k % 14));
            }
            k++;
        }

        //Подключаем к каждому areaChart свой стиль css
        areaChart[0].getStylesheets().add("Chart.css");
        areaChart[1].getStylesheets().add("Chart2.css");
        areaChart[2].getStylesheets().add("Chart3.css");

        //Создаем слайд бары и названия к ним(по хорошему вынести куда то отдельно это)
        Label coordinateRange = new Label("x");
        coordinateRange.setTranslateY(-10);
        coordinateRange.setFont(new Font("Times New Roman", 20));
        Label wheelDegRange = new Label("φ");
        wheelDegRange.setTranslateY(-10);
        wheelDegRange.setFont(new Font("Times New Roman", 20));

        Slider coordinateSlider = new Slider(-150, 150, 0);
        Slider wheelDegSlider = new Slider(-180, 180, 0);

        final Label coordinateValue = new Label(
                Double.toString(coordinateSlider.getValue()));
        final Label wheelDegValue = new Label(
                Double.toString(wheelDegSlider.getValue()));

        coordinateValue.setTranslateY(-10);
        coordinateValue.setTranslateX(10);
        wheelDegValue.setTranslateY(-10);
        wheelDegValue.setTranslateX(10);

        coordinateSlider.setShowTickLabels(true);
        coordinateSlider.setShowTickMarks(true);
        wheelDegSlider.setShowTickLabels(true);
        wheelDegSlider.setShowTickMarks(true);
        coordinateSlider.setMajorTickUnit(100);
        coordinateSlider.setMaxWidth(300);
        wheelDegSlider.setMajorTickUnit(60);

        //Сетка для слайд баров и лейблов
        GridPane controlRoot = new GridPane();

        controlRoot.getColumnConstraints().add(new ColumnConstraints(50));
        controlRoot.getColumnConstraints().add(new ColumnConstraints(300));
        controlRoot.getColumnConstraints().add(new ColumnConstraints(50));

        controlRoot.add(coordinateRange, 0, 0);
        controlRoot.add(coordinateSlider, 1, 0);
        controlRoot.add(wheelDegRange, 0, 1);
        controlRoot.add(wheelDegSlider, 1, 1);
        controlRoot.add(coordinateValue, 2, 0);
        controlRoot.add(wheelDegValue, 2, 1);
        controlRoot.setAlignment(Pos.CENTER);

        //Задаем действия при изменении значения слайд бара
        coordinateSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                temp.coordinateValueChange(newValue.doubleValue());

                for (int j = 0; j < temp.getSize(0); j++) {
                    seriesResult[0].get(j).getData().clear();
                    setSeries(seriesResult[0].get(j), temp.getSet(name[0][j], 0), temp.getResult(0), temp.getTemp(0), j);
                }
                for (int j = 0; j < temp.getSize(2); j++) {
                    seriesResult[2].get(j).getData().clear();
                    setSeries(seriesResult[2].get(j), temp.getSet(name[1][j], 2), temp.getResult(2), j);
                }
                seriesCurrentVal[0].getData().clear();
                setSeries(seriesCurrentVal[0], temp.getTemp(0));
                coordinateValue.setText(String.format("%.2f", newValue.doubleValue()));
            }
        });

        wheelDegSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                temp.changeWheelDegValue(newValue.doubleValue());

                for (int j = 0; j < temp.getSize(1); j++) {
                    seriesResult[1].get(j).getData().clear();
                    setSeries(seriesResult[1].get(j), temp.getSet(name[1][j], 1), temp.getResult(1), temp.getTemp(1), j);
                }
                for (int j = 0; j < temp.getSize(2); j++) {
                    seriesResult[2].get(j).getData().clear();
                    setSeries(seriesResult[2].get(j), temp.getSet(name[1][j], 2), temp.getResult(2), j);
                }
                seriesCurrentVal[1].getData().clear();
                setSeries(seriesCurrentVal[1], temp.getTemp(1));
                wheelDegValue.setText(String.format("%.2f", newValue));
            }
        });

        //Добавляем сетку к общему окну
        root.getChildren().add(controlRoot);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}





      /* coordinateSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    temp.coordinateValueChange(coordinateSlider.getValue());

                    for (int j = 0; j < temp.getSize(0); j++) {
                        seriesResult[0].get(j).getData().clear();
                        setSeries(seriesResult[0].get(j), temp.getSet(name[0][j], 0), temp.getResult(0), temp.getTemp(0), j);
                    }

                    coordinateValue.setText(String.format("%.2f", coordinateSlider.getValue()));
                }
            }
        });*/