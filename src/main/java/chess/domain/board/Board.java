package chess.domain.board;

import chess.domain.piece.Empty;
import chess.domain.piece.Piece;
import chess.domain.piece.PieceColor;
import chess.domain.position.Position;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

public class Board {

    private static final Piece EMPTY_PIECE = new Empty();

    private final Map<Piece, Position> coordinates;

    public Board(Map<Piece, Position> coordinates) {
        this.coordinates = coordinates;
    }

    public Board() {
        this(new LinkedHashMap<>());
    }

    public void putPiece(Piece piece, Position position) {
        coordinates.put(piece, position);
    }

    public Piece findPieceBy(Position position) {
        return coordinates.entrySet()
            .stream()
            .filter(entry -> Objects.equals(entry.getValue(), position))
            .map(Entry::getKey)
            .findFirst()
            .orElse(EMPTY_PIECE)
            ;
    }

    public Position findPositionBy(Piece piece) {
        return coordinates.get(piece);
    }

    public boolean move(Position source, Position target) {
        Path path = generateAvailablePath(source);
        if (isMovable(target, path)) {
            coordinates.remove(findPieceBy(target));
            putPiece(findPieceBy(source), target);
            return true;
        }
        return false;
    }

    public boolean isMovable(Position target, Path path) {
        return path.isAble(target);
    }

    public Path generateAvailablePath(Position from) {
        return findPieceBy(from).generatePaths(from, this);
    }

    public Map<Piece, Position> remainPieces(PieceColor color) {
        return coordinates.entrySet()
            .stream()
            .filter(entry -> entry.getKey().hasColor(color))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public Map<Piece, Position> remainPawns(Map<Piece, Position> pieces) {
        return pieces.entrySet()
            .stream()
            .filter(entry -> entry.getKey().isPawn())
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public boolean kingDead() {
        int count = (int) coordinates.keySet().stream()
            .filter(Piece::isKing)
            .count();
        return count == 1;
    }

    public Map<Piece, Position> getCoordinates() {
        return coordinates;
    }
}