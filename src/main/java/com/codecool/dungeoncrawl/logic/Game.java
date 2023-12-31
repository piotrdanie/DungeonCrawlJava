package com.codecool.dungeoncrawl.logic;


import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.logic.exceptions.NewLevelException;
import com.codecool.dungeoncrawl.logic.gameobject.actors.monsters.Monster;
import com.codecool.dungeoncrawl.logic.map.GameMap;
import com.codecool.dungeoncrawl.logic.map.GameMapFactory;
import com.codecool.dungeoncrawl.logic.map.MapLoader;
import com.codecool.dungeoncrawl.logic.gameobject.actors.player.Player;
import com.codecool.dungeoncrawl.logic.messages.Message;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import static com.codecool.dungeoncrawl.logic.Direction.generateRandomDirection;

public class Game {

    @Getter @Setter
    private Message message;
    private List<String> mapFiles;
    @Getter @Setter
    private GameMap currentMap;
    private int actualLevel;
    @Getter
    private Player player;
    @Getter @Setter
    private Thread monstersThread;
    @Getter
    private GameDatabaseManager dbManager;


    public Game() {
        this.dbManager = new GameDatabaseManager();
        try {
            dbManager.setup();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.message = Message.getInstance();
        this.player = new Player();
        this.mapFiles = GameMapFactory.createGameMaps();
        this.actualLevel = 0;
        this.currentMap = MapLoader.loadMap(mapFiles.get(actualLevel), player);
        this.monstersThread = createMonstersThread();
        monstersThread.start();
    }

    private Thread createMonstersThread() {
        Thread monstersThread = new Thread(() -> {
            while (true) {
//                message.setActualMessage("it works");
//                System.out.println("it works");
                Platform.runLater(() -> {
//                    System.out.println("move cow");
//                    message.setActualMessage("or not");
                    try {
                        monstersTurn();
                    } catch (NewLevelException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Delay between turns (optional)
                try {
                    Thread.sleep(220); // Adjust the delay as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return monstersThread;
    }

    private void monstersTurn() throws NewLevelException {
        Iterator<Monster> iterator = getCurrentMap().getMonsters().iterator();
        while(iterator.hasNext()) {
            Monster monster = iterator.next();

            if (!monster.isAlive()) {
                iterator.remove();
            } else {
                Direction direction = generateRandomDirection();
                monster.move(direction.getX(), direction.getY(), getCurrentMap().getPlayer());
            }
        }
    }

    public void gameTurn(Direction direction) {
        try {

            if (!getCurrentMap().getPlayer().isAlive()) {
                GameEvent gameEvent = GameEvent.GAME_OVER;
                gameEventHandler(gameEvent);
            }

            getCurrentMap().getPlayer().getName();
            GameEvent gameEvent = getCurrentMap().getPlayer().move(direction.getX(), direction.getY());
            if (gameEvent != null) {
                gameEventHandler(gameEvent);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void gameEventHandler(GameEvent gameEvent) {
        switch (gameEvent) {
            case GAME_END:
                System.out.println("Game is finished");
                System.exit(0);
                break;
            case NEXT_LEVEL:
                changeCurrentMap();
                break;
            case GAME_OVER:
                System.out.println("Game Over");
                System.exit(0);
                break;
        }
    }

    private void changeCurrentMap() {
        actualLevel++;
        currentMap = MapLoader.loadMap(mapFiles.get(actualLevel), player);
    }
}