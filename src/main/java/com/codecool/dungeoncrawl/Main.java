package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.Direction;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.exceptions.NewLevelException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    static GameMap firstMap = MapLoader.loadMap("/map.txt");
    static GameMap secondMap = MapLoader.loadMap("/map2.txt");

    static GameMap currentlMap;
    Canvas canvas = new Canvas(
            getCurrentMap().getWidth() * Tiles.TILE_WIDTH,
            getCurrentMap().getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();

    public static void main(String[] args) {
        currentlMap = firstMap;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));

        ui.add(new Label("Health: "), 0, 0);
        ui.add(healthLabel, 1, 0);

        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(canvas);
        borderPane.setRight(ui);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        refresh();

        scene.setOnKeyPressed(keyEvent -> {
            try {
                onKeyPressed(keyEvent);
            } catch (NewLevelException e) {
                // change levels
                changeCurrentMap();
                refresh();
            }
        });

        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    private void onKeyPressed(KeyEvent keyEvent) throws NewLevelException {

        switch (keyEvent.getCode()) {
            case UP:
                getCurrentMap().getPlayer().move(0, -1);
                refresh();
                break;
            case DOWN:
                getCurrentMap().getPlayer().move(0, 1);
                refresh();
                break;
            case LEFT:
                getCurrentMap().getPlayer().move(-1, 0);
                refresh();
                break;
            case RIGHT:
                getCurrentMap().getPlayer().move(1,0);
                refresh();
                break;
        }
    }

    private void gameTurn(Direction direction) {
        try {
            getCurrentMap().getPlayer().move(direction.getX(), direction.getY());
            refresh();
        } catch (Exception e) {
            // change levels
            changeCurrentMap();
            refresh();
        }

    }

    private GameMap getCurrentMap() {
        return currentlMap;
    }

    private void changeCurrentMap() {
        currentlMap = secondMap;
    }


    private void refresh() {
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int x = 0; x < getCurrentMap().getWidth(); x++) {
            for (int y = 0; y < getCurrentMap().getHeight(); y++) {
                Cell cell = getCurrentMap().getCell(x, y);
                if (cell.getSomething() != null) {
                    Tiles.drawTile(context, cell.getSomething(), x, y);
                } else {
                    Tiles.drawTile(context, cell, x, y);
                }
            }
        }
        healthLabel.setText("" + getCurrentMap().getPlayer().getHealth());
    }
}
