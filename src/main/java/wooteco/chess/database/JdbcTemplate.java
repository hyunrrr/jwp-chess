package wooteco.chess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("CustomJdbcTemplate")
public class JdbcTemplate {

	private final DataSource dataSource;

	public JdbcTemplate(@Qualifier("CustomDataSource") final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public <T> T executeQuery(String query, RowMapper<T> rowMapper, PreparedStatementSetter preparedStatementSetter) {
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(query)
		) {
			preparedStatementSetter.setArgument(preparedStatement);
			return rowMapper.mapRow(preparedStatement.executeQuery());
		} catch (SQLException exception) {
			System.err.println(exception.getMessage());
		}
		return null;
	}

	public <T> T executeQuery(String query, RowMapper<T> rowMapper) {
		return executeQuery(query, rowMapper, preparedStatement -> {
		});
	}

	public Long executeUpdate(String query, PreparedStatementSetter preparedStatementSetter) {
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
		) {
			preparedStatementSetter.setArgument(preparedStatement);
			preparedStatement.executeUpdate();
			return generatedKey(preparedStatement);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private Long generatedKey(final PreparedStatement preparedStatement) {
		try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
			if (resultSet.next()) {
				return resultSet.getLong(1);
			}
			return null;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public Long executeUpdate(String query) {
		return executeUpdate(query, preparedStatement -> {
		});
	}

}