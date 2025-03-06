package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SqlUserData implements UserDataAccess{

    public SqlUserData() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
            String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, userData.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData result = null;
        try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, password, email FROM userData WHERE username=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, username);
                    try (var rs = ps.executeQuery()) {
                        if(rs.next()){
                            var password = rs.getString("password");
                            var email = rs.getString("email");
                            result = new UserData(username, password, email);
                        }
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage());
            }
        return result;
    }

    @Override
    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE userData";
            try(var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userData (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        SqlGameData.configureDatabase(createStatements);
    }

}