package com.dddryinside.service;

import com.dddryinside.models.Mood;
import com.dddryinside.models.Note;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataBaseAccess {
    private static final String DB_URL = "jdbc:sqlite:./mental.db";

    public static void saveNote(Note note) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDiaryTableIfNotExists();

            String insertQuery = "INSERT INTO diary (user_id, content, date) " +
                    "VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setInt(1, note.getUser_id());
                statement.setString(2, note.getContent());
                statement.setString(3, String.valueOf(note.getDate()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteNote(Note note) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDiaryTableIfNotExists();

            String deleteQuery = "DELETE FROM diary WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                statement.setInt(1, note.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Note> getNotes(int amount) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDiaryTableIfNotExists();

            String insertQuery = "SELECT id, content, date FROM diary WHERE user_id = ? ORDER BY id DESC LIMIT ?";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, AccountManager.getUser().getId());
            statement.setInt(2, amount);
            ResultSet resultSet = statement.executeQuery();

            List<Note> notes = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String content = resultSet.getString("content");
                LocalDate date = LocalDate.parse(resultSet.getString("date"));

                Note diaryNote = new Note(id, AccountManager.getUser(), content, date);
                notes.add(diaryNote);
            }
            return notes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Note> getNotes(int pageNumber, int pageSize) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDiaryTableIfNotExists();

            String insertQuery = "SELECT * FROM diary WHERE user_id = ? ORDER BY id DESC LIMIT ? OFFSET ?";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, AccountManager.getUser().getId());
            statement.setInt(2, pageSize);
            statement.setInt(3, (pageNumber - 1) * pageSize);
            ResultSet resultSet = statement.executeQuery();

            List<Note> notes = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String content = resultSet.getString("content");
                LocalDate date = LocalDate.parse(resultSet.getString("date"));

                Note diaryNote = new Note(id, AccountManager.getUser(), content, date);
                notes.add(diaryNote);
            }
            return notes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getDiaryPagesAmount(int pageSize) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDiaryTableIfNotExists();

            String insertQuery = "SELECT COUNT(*) FROM diary WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, AccountManager.getUser().getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int totalNotes = resultSet.getInt(1);

                int totalPages = totalNotes / pageSize;
                if (totalNotes % pageSize != 0) {
                    totalPages++;
                }

                return totalPages;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isCurrentMoodExist() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDailySurveyTableIfNotExists();

            String insertQuery = "SELECT id FROM daily_survey WHERE user_id = ? AND date = ?";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setInt(1, AccountManager.getUser().getId());
                statement.setString(2, String.valueOf(LocalDate.now()));
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PageManager.showNotification("Ошибка базы данных!");
        }
        return false;
    }

    public static void saveMood(int mood) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDailySurveyTableIfNotExists();

            String insertQuery = "INSERT INTO daily_survey (user_id, mood, date) " +
                    "VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setInt(1, AccountManager.getUser().getId());
                statement.setInt(2, mood);
                statement.setString(3, String.valueOf(LocalDate.now()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PageManager.showNotification("Ошибка базы данных!");
        }
    }

    public static double getAverageMood() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDailySurveyTableIfNotExists();

            String selectQuery = "SELECT AVG(mood) AS mood_avg FROM daily_survey WHERE user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setInt(1, AccountManager.getUser().getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Math.ceil(resultSet.getDouble("mood_avg") * 10.0) / 10.0;
                    } else {
                        return 0.0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PageManager.showNotification("Ошибка базы данных!");
        }
        return 0.0;
    }

    public static double getAverageMood(int days) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDailySurveyTableIfNotExists();

            String selectQuery = "SELECT AVG(mood) AS mood_avg " +
                    "FROM (SELECT mood " +
                    "FROM daily_survey " +
                    "WHERE user_id = ? " +
                    "ORDER BY date DESC " +
                    "LIMIT ?) " +
                    "AS recent_moods";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setInt(1, AccountManager.getUser().getId());
                statement.setInt(2, days);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Math.ceil(resultSet.getDouble("mood_avg") * 10.0) / 10.0;
                    } else {
                        return 0.0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PageManager.showNotification("Ошибка базы данных!");
        }
        return 0.0;
    }

    public static List<Mood> getMoodHistory() {
        List<Mood> moodHistory = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDailySurveyTableIfNotExists();

            String selectQuery = "SELECT mood, date FROM daily_survey WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
                statement.setInt(1, AccountManager.getUser().getId());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int mood = resultSet.getInt("mood");
                    String date = resultSet.getString("date");

                    moodHistory.add(new Mood(mood, date));
                }
        } catch (SQLException e) {
            e.printStackTrace();
            PageManager.showNotification("Ошибка базы данных!");
        }

        return moodHistory;
    }

    public static List<Mood> getMoodHistory(int days) {
        List<Mood> moodHistory = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDailySurveyTableIfNotExists();

            String selectQuery = "SELECT mood, date FROM daily_survey WHERE user_id = ? ORDER BY id DESC LIMIT ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setInt(1, AccountManager.getUser().getId());
            statement.setInt(2, days);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int mood = resultSet.getInt("mood");
                String date = resultSet.getString("date");

                moodHistory.add(new Mood(mood, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PageManager.showNotification("Ошибка базы данных!");
        }
        return moodHistory;
    }

    public static int getMoodChartPagesAmount(int pageSize) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDiaryTableIfNotExists();

            String insertQuery = "SELECT COUNT(*) FROM daily_survey WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, AccountManager.getUser().getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int total = resultSet.getInt(1);

                int totalPages = total / pageSize;
                if (total % pageSize != 0) {
                    totalPages++;
                }

                return totalPages;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Mood> getMoodHistory(int pageNumber, int pageSize) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            createDiaryTableIfNotExists();

            String insertQuery = "SELECT * FROM daily_survey WHERE user_id = ? ORDER BY id DESC LIMIT ? OFFSET ?";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, AccountManager.getUser().getId());
            statement.setInt(2, pageSize);
            statement.setInt(3, (pageNumber - 1) * pageSize);
            ResultSet resultSet = statement.executeQuery();

            List<Mood> moodHistory = new ArrayList<>();
            while (resultSet.next()) {
                int mood = resultSet.getInt("mood");
                String date = resultSet.getString("date");

                Mood moodObj = new Mood(mood, date);
                moodHistory.add(moodObj);
            }

            return moodHistory;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDailySurveyTableIfNotExists() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (isTableNotExists(connection, "daily_survey")) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS daily_survey (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER," +
                        "mood INTEGER," +
                        "date DATE)";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(createTableQuery);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDiaryTableIfNotExists() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (isTableNotExists(connection, "diary")) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS diary (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER," +
                        "content TEXT," +
                        "date DATETIME)";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(createTableQuery);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isTableNotExists(Connection connection, String name) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet resultSet = metadata.getTables(null, null, name, null);
        return !resultSet.next();
    }
}
