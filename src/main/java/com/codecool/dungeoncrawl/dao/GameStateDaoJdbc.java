package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.model.GameState;
import com.codecool.dungeoncrawl.model.PlayerModel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GameStateDaoJdbc implements GameStateDao {
    private Connection connection;

    @SneakyThrows
    @Override
    public void add(GameState state) {
        try(PreparedStatement statement = connection.prepareStatement(Queries.ADD_GAME_STATE)) {
            statement.setString(1, state.getCurrentMap());
            statement.setInt(2, state.getPlayer().getId());
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public void update(GameState state) {
        try (PreparedStatement statement = connection.prepareStatement(Queries.UPDATE_GAME_STATE)) {
            statement.setString(1, state.getCurrentMap());
            statement.setDate(2, state.getSavedAt());
            statement.setInt(3, state.getPlayer().getId());
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public GameState get(int id) {
        try (PreparedStatement statement = connection.prepareStatement(Queries.GET_GAME_STATE)) {
            statement.setInt(1, id);

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    String currentMap = resultSet.getString("current_map");
                    Date savedAt = resultSet.getDate("saved_at");
                    int playerId = resultSet.getInt("player_id");

                    //get playermodel by id to create gamestate
//                    return new GameState(currentMap, savedAt, playerModel);
                }
            }
        }
        return null;
    }

    @SneakyThrows
    @Override
    public List<GameState> getAll() {
        List<GameState> gameStates = new ArrayList<>();

        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(Queries.GET_ALL_GAME_STATES);

            while(resultSet.next()) {
                String currentMap = resultSet.getString("current_map");
                Date savedAt = resultSet.getDate("saved_at");
                PlayerModel playerModel = null;
                int playerId = resultSet.getInt("player_id");

                //get playermodel by id to create gamestate
//                gameStates.add(new GameState(currentMap, savedAt, playerModel));
            }
        }

        return gameStates;
    }
}
