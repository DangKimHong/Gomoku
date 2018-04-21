package android.dkh.com.caro;

/**
 * Created by MyPC on 16/04/2018.
 */

public class Record {
    private Move move;
    private int score;

    public Record() {
    }

    public Record(Move move, int score) {
        this.move = move;
        this.score = score;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
