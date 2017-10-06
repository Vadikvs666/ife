/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author vadim
 */
public class SettingsFormGenerator {

    private Settings settings;
    private Stage form;
    private AnchorPane pane;
    private Boolean changed = false;
    private Button save;
    private Button reset;
    private Button close;
    private List<TextField> values;

    public SettingsFormGenerator(Settings settings) {
        this.settings = settings;
        this.form = new Stage();
        this.pane = new AnchorPane();
        this.save = new Button("Сохранить");
        save.setPrefWidth(120);
        this.reset = new Button("Сбросить");
        reset.setPrefWidth(120);
        this.close = new Button("Закрыть");
        close.setPrefWidth(120);
        AnchorPane.setBottomAnchor(close, 15.0);
        AnchorPane.setRightAnchor(close, 10.0);
        AnchorPane.setBottomAnchor(save, 15.0);
        AnchorPane.setRightAnchor(save, 140.0);
        AnchorPane.setBottomAnchor(reset, 15.0);
        AnchorPane.setRightAnchor(reset, 270.0);
        pane.getChildren().addAll(save,  close);
        save.setOnAction((event) -> onSaveButton());
        close.setOnAction((event) -> onCloseButton());
        values = new ArrayList<>();
        genarateControls();
    }

    private void genarateControls() {
        List<Setting> list = settings.getSettings();
        double topAnchor = 15.0;
        for (Setting s : list) {
            Label l = new Label(s.getPrint());
            l.setMaxWidth(150);
            TextField t = new TextField(s.getValue());
            t.setOnKeyPressed((event) -> changed());
            t.setMinWidth(300);
            t.setMaxWidth(300);
            t.setId(s.getName());
            pane.getChildren().addAll(l, t);
            AnchorPane.setTopAnchor(l, topAnchor);
            AnchorPane.setTopAnchor(t, topAnchor);
            AnchorPane.setLeftAnchor(l, 15.0);
            AnchorPane.setLeftAnchor(t, 180.0);
            topAnchor = topAnchor + 40;
            pane.setMinHeight(topAnchor + 70);
            values.add(t);
        }
        pane.setMinWidth(500);
    }

    private void onCloseButton() {
        if (changed) {
            //ask to close
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Внимание имеются не сохраненные изменения");
            alert.setHeaderText("Сохранить?");
            ButtonType buttonYES = new ButtonType("Да");
            ButtonType buttonNO = new ButtonType("Нет");
            alert.getButtonTypes().setAll(buttonYES, buttonNO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonYES) {
                onSaveButton();
                form.close();
            } else {
                form.close();
            }
        } else {
            form.close();
        }
    }

    private void onSaveButton() {
        save.setText("Сохранить");
        changed = false;
        for (TextField text : values) {
            System.out.println(text.getId() + "  : " + text.getText());
            settings.setValue(text.getId(), text.getText());
        }
    }

    public void show() {
        Scene scene = new Scene(pane);
        form.setScene(scene);
        form.showAndWait();
    }

    private void changed() {
        save.setText("Сохранить*");
        changed = true;
    }

}
