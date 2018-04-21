package android.dkh.com.caro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 13/04/2018.
 */

public class Chessboard {
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private int[][] board;// mảng chứa các bước đã đi, -1 là chưa đi, 0 là người chơi đi, 1 là máy đi
    private int player;// người chơi
    private Minimax minimax;

    private Context context;

    private int bitmapWidth;
    private int bitmapHeight;
    private int colQty;
    private int rowQty;
    private List<Line> arrayLines;

    private Bitmap bmX;
    private Bitmap bmO;

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(int bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(int bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

    public int getColQty() {
        return colQty;
    }

    public void setColQty(int colQty) {
        this.colQty = colQty;
    }

    public int getRowQty() {
        return rowQty;
    }

    public void setRowQty(int rowQty) {
        this.rowQty = rowQty;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public Chessboard(Context context, int bitmapWidth, int bitmapHeight, int colQty, int rowQty) {
        this.context = context;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.colQty = colQty;
        this.rowQty = rowQty;
    }

    // phương thức khởi tạo, reset các giá trị của phương thức
    public void init() {
        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        int strokeWidth = 2;
        paint.setStrokeWidth(strokeWidth);
        board = new int[rowQty][colQty];
        player = 0;
        arrayLines = new ArrayList<>();
        minimax = new Minimax();

//        gán giá trị -1 cho tất cả các ô, chưa có nước cờ nào
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                board[i][i] = -1;//-1 là chưa đi
            }
        }
        int cellWitdh = bitmapWidth / colQty;
        for (int i = 0; i <= colQty; i++) {
            arrayLines.add(new Line(i * cellWitdh, 0, i * cellWitdh, bitmapHeight));
        }

        int cellHeight = bitmapHeight / rowQty;
        for (int i = 0; i <= rowQty; i++) {
            arrayLines.add(new Line(0, i * cellHeight, bitmapWidth, i * cellHeight));
        }


    }

    public Bitmap drawBoard() {
        for (int i = 0; i< arrayLines.size(); i++) {
            canvas.drawLine(
                    arrayLines.get(i).getStartX(),
                    arrayLines.get(i).getStartY(),
                    arrayLines.get(i).getEndX(),
                    arrayLines.get(i).getEndY(),
                    paint
            );
        }
        bmX = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_x);
        bmO = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_o);
        return bitmap;
    }

    public boolean onTouch(final View v, MotionEvent event) {
        int cellWitdh = v.getWidth() / colQty;
        int cellHeight = v.getHeight() / rowQty;

        int colIndex = (int) (event.getX() / cellWitdh);
        int rowIndex = (int) (event.getY() / cellHeight);

        if (board[rowIndex][colIndex] != -1)
            return true;// có người đi rồi

        board[rowIndex][colIndex] = player;
        onDrawBoard(colIndex, rowIndex, v);
        v.invalidate();

        player = (player+1) % 2;

        if (iSGameOver()){
            init();
            Log.i("Status","Game đã kết thúc");
        } else {
            int count = getCurrentDept();
            final int currentDept = rowQty * colQty - count;
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // cho mình 1 nước đi
                    // duyệt mảng 2 chiều board nếu board khác -1 thì có bước đi
                    Record record = minimax.MinimaxRecord(Chessboard.this, 1, currentDept, 64);// nước đi
                    //  có nước đi, đặt nước đi
                    // tiến trình
                    makeMove(record.getMove());
                    onDrawBoard(record.getMove().getColIndex(), record.getMove().getRowIndex(), v);
                }
            });
        }
        v.invalidate();
        return true;
    }
    public void onDrawBoard(int colIndex, int rowIndex, View view){
        int cellWidth = view.getWidth()/ colQty;
        int cellHeight = view.getHeight()/rowQty;
        // gán nước đi là người chơi nào
        board[rowIndex][colIndex] = player;
        int padding = 50;
        if (player == 0) {
            canvas.drawBitmap(
                    bmX,
                    new Rect(0, 0, bmX.getWidth(), bmX.getHeight()),
                    new Rect(colIndex * cellWidth + padding,
                             rowIndex * cellHeight + padding,
                             (rowIndex + 1) * cellWidth + padding,
                             (colIndex + 1) * cellHeight - padding),
                    paint);
            player = 1;
        } else {
            canvas.drawBitmap(bmO,
                    new Rect(0, 0, bmO.getWidth(), bmO.getHeight()),
                    new Rect(colIndex * cellWidth,
                             rowIndex * cellHeight,
                             (rowIndex + 1) * cellWidth,
                             (colIndex + 1) * cellHeight),
                    paint);
            player = 0;
        }
    }

    // Kiểm tra Game đã kết thúc hay chưa?
    public  boolean iSGameOver(){
        if (checkWin(0) || checkWin(1))return true;
        int count = 0;
        for (int i =0; i <rowQty; i++){
            for (int j = 0; j < colQty; j++){
                if (board[i][j] == -1) count++;
            }
        }
        if (count == 0){
            return true; //trò chơi kết thúc
        }
        //chưa thắng hoặc còn vị trí để đi => game chưa kết thúc

        return false;
    }

    public boolean checkWin(int player){
        if (board[0][0] == player && board[0][1] == player && board [0][2] == player)
            return true;

        if (board[1][0] == player && board[1][1] == player && board[1][2] == player)
            return  true;

        if (board[2][0] == player && board[2][1] == player && board[2][2] == player)
            return true;

        if (board[0][0] == player && board[1][0] == player && board[2][0] == player)
            return true;

        if (board[0][1] == player && board[1][1] == player && board[2][1] == player)
            return true;

        if (board[0][2] == player && board[1][2] == player && board [2][2] == player)
            return true;

        if (board[0][0] == player && board[1][1] == player && board[2][2]  == player)
            return true;

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;
        return false;
    }

    // tạo mới 1 danh sách, duyệt qua từng vị trí đi, nếu là -1 thì còn vị trí đi
    public List<Move> getMove(){
        List<Move> moves = new ArrayList<>();
        for (int i =0; i <rowQty; i++){
            for (int j=0; j <colQty; j++){
                if (board[i][j] == -1) moves.add(new Move(i, j));// có thể đi được
            }
        }
        return moves;
    }

    public void makeMove(Move move){
        board[move.getRowIndex()][move.getColIndex()] = player;
        player = (player + 1) % 2;//hoán đổi người chơi, 1 qua 0, hoặc 0 qua 1
    }

    // đánh giá bàn cờ;
    // trở về điểm tương ứng với player,
    // bot thắng là 1,
    // bot thua là 0
    public  int evalute(int player){
        if (checkWin(player)) return 1;
        if (checkWin((player + 1) % 2)) return -1;
        return 0;
    }

    public int getCurrentDept(){
        int count = 0;
        for (int i =0; i< rowQty; i++){
            for (int j = 0; j <colQty; j++){
                if (board[i][j] == -1) count++;
            }
        }
        return count;
    }

    public  int[][] getNewBoard(){
        int[][] newBoard = new int[rowQty][colQty];
        for (int i =0; i<rowQty; i++){
            for (int j =0; j<colQty; j++){
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

}
