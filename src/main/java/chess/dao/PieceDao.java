package chess.dao;

import chess.dto.BoardElementDto;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PieceDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<BoardElementDto> boardDtoRowMapper = (resultSet, rowNum) -> new BoardElementDto(
            resultSet.getString("piece_name"),
            resultSet.getString("piece_color"),
            resultSet.getString("position")
    );

    public PieceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void savePiece(int gameId, BoardElementDto boardElementDto) {
        String sql = "insert into piece (game_id, piece_name, piece_color, position) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                gameId,
                boardElementDto.getPieceName(),
                boardElementDto.getPieceColor(),
                boardElementDto.getPosition());
    }

    public List<BoardElementDto> findAllPieceById(int gameId) {
        String sql = "SELECT * FROM piece WHERE game_id=?";
        return jdbcTemplate.query(sql, boardDtoRowMapper, gameId);
    }

    public void deleteAllPieceById(int gameId) {
        String sql = "DELETE FROM piece WHERE game_id = ?";
        jdbcTemplate.update(sql, gameId);
    }

    public void deletePieceByIdAndPosition(int gameId, String position) {
        String sql = "DELETE FROM piece WHERE game_id = ? and position = ?";
        jdbcTemplate.update(sql, gameId, position);
    }

    public void updatePiecePosition(int gameId, String from, String to) {
        String sql = "UPDATE piece SET position = ? WHERE position = ? AND game_id = ?";
        jdbcTemplate.update(sql, to, from, gameId);
    }
}