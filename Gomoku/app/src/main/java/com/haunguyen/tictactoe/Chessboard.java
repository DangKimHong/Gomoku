package com.haunguyen.tictactoe;

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


public class Chessboard {
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private int[][] board;//cac buoc đã đi -1 là chưa đi, 0 la nguoi choi, 1 la may
    private int player;//nguoi choi nào
    private Context context;
    private int bitmapWidth, bitmapHeight, colQty,rowQty;
    private List<Line> lines;
    private Minimax minimax;


    //gọi nhiều lần
    private Bitmap playerA, playerB;

    public Chessboard(Context context, int bitmapWidth, int bitmapHeight, int colQty, int rowQty) {
        this.context = context;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.colQty = colQty;
        this.rowQty = rowQty;
    }

    //lam cac thao tac khoi tao, reset lại giá trị của các phương thức
    public void init(){
        minimax = new Minimax();
        lines = new ArrayList<>();
        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        board = new int[rowQty][colQty];
        for(int i = 0; i<rowQty; i++){
            for(int j = 0; j < colQty;j++){
                board[i][j] = -1;//-1 là chưa đi
            }
        }

        player = 0;
        paint.setStrokeWidth(2);
        int celWidth = bitmapWidth/colQty;
        int celHeight = bitmapHeight/rowQty;
        for(int i = 0; i <= colQty; i++){
            lines.add(new Line(celWidth*i, 0, celWidth*i, bitmapHeight));
        }
        for(int i = 0; i <= rowQty; i++){
            lines.add(new Line(0, i*celHeight, bitmapWidth, i*celHeight));
        }
    }

    public Bitmap drawBoard(){
        for(int i = 0; i < lines.size(); i++){
            canvas.drawLine(
                    lines.get(i).getX1(),
                    lines.get(i).getY1(),
                    lines.get(i).getX2(),
                    lines.get(i).getY2(),
                    paint
            );
        }
        playerA = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_player_a);
        playerB = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_player_b);

        return bitmap;
    }

    public boolean onTouch(final View view, MotionEvent motionEvent) {
        int cellWidth = view.getWidth() / colQty;
        int cellHeight = view.getHeight() / rowQty;
        int colIndex = (int) (motionEvent.getX() / cellWidth);
        int rowIndex = (int) (motionEvent.getY() / cellHeight);
        if (board[rowIndex][colIndex] != -1) {
            return true;//co nguoi di roi
        }


        board[rowIndex][colIndex] = player;
        onDrawBoard(colIndex, rowIndex, view);
        view.invalidate();
        player = (player + 1) % 2;
        if (isGameOver()) {
            init();
            Log.i("GAME_STATUS", "over");
        } else {
            int count = getCurrentDept();
            final int currentDetp = rowQty * colQty - count;
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //cho mình 1 nước đi, nghĩa là mọi đến minimax
                    //duyệt mảng 2 chiều board nếu mà board khác -1 thì có bước đi
                    Record record = minimax.minimaxRecode(Chessboard.this, 1, currentDetp, 9);//nước đi
                    //có nước đi, đặt nước đi
                    //tiến trình
                    makeMove(record.getMove());
                    onDrawBoard(record.getMove().getColIndex(), record.getMove().getRowIndex(), view);

                }
            });
        }
        view.invalidate();
        //        //cap nhat lai ban cờ
        //        player = (player == 0 ? 1 : 0); // (player+1)%2
        return true;
    }

    public void onDrawBoard(int colIndex, int rowIndex, View view){
        int cellWidth = view.getWidth()/colQty;
        int cellHeight = view.getHeight()/rowQty;
        board[rowIndex][colIndex] = player;//gán nước đi là người chơi nào
        int padding = 50;
        if(player == 0){
            canvas.drawBitmap(
                    playerA,
                    new Rect(0,0,playerA.getWidth(), playerA.getHeight()),
                    new Rect(colIndex*cellWidth+padding,rowIndex*cellHeight+padding,(colIndex+1)*cellWidth -padding, (rowIndex+1)*cellHeight -padding),
                    paint);
            player = 1;
        }else {
            canvas.drawBitmap(
                    playerB,
                    new Rect(0,0,playerB.getWidth(), playerB.getHeight()),
                    new Rect(colIndex*cellWidth,rowIndex*cellHeight,(colIndex+1)*cellWidth, (rowIndex+1)*cellHeight),
                    paint);
            player = 0;

        }
    }

    //ktra coi có hết game chưa
    public boolean isGameOver(){
        if (checkWin(0) || checkWin(1))
            return true;
        int count = 0;
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (board[i][j] == -1) count++;
            }
        }
        if (count == 0){
            return true;//trò chơi kết thúc
        }
        //chưa thắng hoặc còn vị trí để đi=>game chưa kết thúc
        return false;

    }

    private boolean checkWin(int player) {
        if (board[0][0] == player && board[0][1] == player && board[0][2] == player)
            return true;

        if (board[1][0] == player && board[1][1] == player && board[1][2] == player)
            return true;

        if (board[2][0] == player && board[2][1] == player && board[2][2] == player)
            return true;

        if (board[0][0] == player && board[1][0] == player && board[2][0] == player)
            return true;

        if (board[0][1] == player && board[1][1] == player && board[2][1] == player)
            return true;

        if (board[0][2] == player && board[1][2] == player && board[2][2] == player)
            return true;

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;
        return false;
    }

    public List<Move> getMove() {
        //tạo mới 1 danh sách, duyệt qua từng vị trí, nếu -1 còn vị trí đi
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (board[i][j] == -1) moves.add(new Move(i, j));//có thể đi dc
            }
        }
        return moves;
    }

    public void makeMove(Move move) {
        board[move.getRowIndex()][move.getColIndex()] = player;
        player = (player + 1) % 2;//hoan đổi người chơi, 1 qua 0, hoặc 0 qua 1
    }

    //dánh giá bàn cở, trở về điểm tương ứng v player, boss thắng là 1, boss thua là -1, hòa là 0
    public int evaluate(int player) {
        if (checkWin(player))
                return 1;
        if (checkWin((player + 1) % 2))
                return -1;
        return 0;
    }

    public int[][] getNewBoard(){
        int[][] newBoard = new int[rowQty][colQty];
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }
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

    public int getCurrentDept(){
        int count = 0;
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (board[i][j] == -1) count++;
            }
        }
        return count;
    }


}
