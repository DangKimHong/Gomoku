package android.dkh.com.caro;

/**
 * Created by MyPC on 16/04/2018.
 */

public class Minimax {
    public Record MinimaxRecord(Chessboard chessboard, int player, int maxDept, int currentDept){
        Move bestMove = null;
        int bestScore;// giá trị điểm tốt nhất
        if (chessboard.iSGameOver() || currentDept == maxDept){
            return new Record(null, chessboard.evalute(player));
        }

        if (chessboard.getPlayer() == player){
            bestScore = Integer.MIN_VALUE;
        } else {
            bestScore = Integer.MAX_VALUE;
        }
        // duyệt qua all các nước có thể đi trên bàn cờ
        for (Move move:chessboard.getMove()){
            //tạo mới 1 bàn cờ
            Chessboard newChess = new Chessboard(chessboard.getContext(), chessboard.getBitmapWidth(), chessboard.getBitmapHeight(), chessboard.getColQty(), chessboard.getRowQty());
            newChess.setBoard(chessboard.getNewBoard());
            newChess.setPlayer(chessboard.getPlayer());

            // đánh dấu nước đi
            newChess.makeMove(move);
            Record record = MinimaxRecord(newChess, player, maxDept, currentDept++);

            // currentscore nằm trong record
            // đến lượt bot

            if (chessboard.getPlayer() == player){
                if (record.getScore() >bestScore){
                    bestScore = record.getScore();
                    bestMove = move;
                }
                //lượt người chơi
            }else {
                if (record.getScore() <bestScore){
                    bestScore = record.getScore();
                    bestMove  = move;
                }
            }
        }
        return new Record(bestMove, bestScore);
    }
}
