package android.dkh.com.gomokugame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static Button button;
    private static TextView textView;
    private Context context;

    private int [][] valueCell = new int[maxN][maxN];
    private int winner_play;// ai la ng win, 0 là hòa, 1 la player, 2 là bot
    private boolean firstMove;
    private int xMove, yMove; // vị trí mặc định của ô
    private int turnPlay;

    final static int maxN = 15;
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    private Drawable[] drawCell = new Drawable[4];//0 là empty, 1 là players, 2 là bot, 3 là bg


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setListen();
        loadResources();
        designBoardGame();
    }

    private void setListen(){
        button = (Button) findViewById(R.id.btnNG);
        textView = (TextView) findViewById(R.id.tvTurn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_game();
                play_game();
            }
        });
    }

    private void init_game() {
        // tạo UI trước khi chơi
        // điều khiển những giá trị có sẵn
        firstMove = true;
        winner_play = 0;
        for (int i = 0; i <maxN; i++){
            for (int j = 0; j <maxN; j++){
                ivCell[i][j].setImageDrawable(drawCell[0]);// default để trống
                valueCell[i][j] = 0;
            }
        }
    }
    private void play_game(){
        // mặc định là ai là người chơi trước
        Random random = new Random();
        turnPlay = random.nextInt(2)+1;// random.nextInt(2)+1 trả về giá trị (0,1)

        if (turnPlay == 1) {
            Toast.makeText(context, "Player First", Toast.LENGTH_SHORT).show();
            playerTurn();
        }else {
            Toast.makeText(context, "Bot First", Toast.LENGTH_SHORT).show();
            botTurn();
        }
    }

    private void botTurn() {
        textView.setText("Bot turn");
        if (firstMove){
            firstMove = false;
            xMove =7; yMove=7;
            make_a_move();
        }else {
            //tìm nước đi tốt nhất
            findBotMove();
            make_a_move();
        }
    }

    private final int[] iRow = {-1,-1,-1,0,1,1,1,0};
    private final int[] iCol = {-1,0,1,1,1,0,-1,-1};
    private void findBotMove() {
        List<Integer> listX  = new ArrayList<Integer>();
        List<Integer> listY  = new ArrayList<Integer>();

        // tìm ô còn trống để di chuyển
        final int range = 2;
        for (int i = 0; i <maxN; i++){
            for (int j = 0; j <maxN; j++){
                if (valueCell[i][j] != 0 ){
                    for (int t = 0; t <range; t++){
                        for (int k = 0; k < 8; k++){
                            int x = i+iRow[k]*t;
                            int y = j+iCol[k]*t;
                            if (inBoard(x,y) && valueCell[x][y] == 0){
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
            }
        }
        int lx = listX.get(0);
        int ly = listY.get(0);

        // bot luôn tìm vị trí nhỏ nhất trên ban cờ
        int res = Integer.MAX_VALUE-10;
        int rr;
        for (int i=0; i<listX.size(); i++){
            int x = listX.get(1);
            int y = listY.get(1);
            valueCell[x][y] =2;
            rr = getValue_Position();
            if(rr< res){
                res = rr;lx =x; ly = y;
            }
            valueCell[x][y] = 0;
        }
        xMove = lx; yMove =ly;
    }

    // tạo nước đi và thay đổi lượt
    private void make_a_move() {
        ivCell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);
        valueCell[xMove][yMove] = turnPlay;
        // kiểm tra thắng

        // nếu không có ô trống tồn tại thì vẽ nước nước đi
        if(noEmptyCell()){
            Toast.makeText(context, "Draw!", Toast.LENGTH_SHORT).show();
            return;
        }else if(checkWin()){
            if (winner_play == 1) {
                Toast.makeText(context, "Winner: Player ", Toast.LENGTH_SHORT).show();
                textView.setText("Winner is Player");
            }
            else {
                Toast.makeText(context, "Winner: Bot", Toast.LENGTH_SHORT).show();
                textView.setText("Winner is Bot");
            }
                return;
            }
        if (turnPlay==1 ){
            turnPlay = (1+2)- turnPlay;
            botTurn();
        }else {
            turnPlay = 3 - turnPlay;
            playerTurn();
        }
    }

    private boolean checkWin() {
        // kiểm tra xem tại ví trí ô hiện tại xMove, yMove có thể tạo thêm 5 ô nữa hay không
        if(winner_play != 0 ) return true;

        // kiểm tra trong row
        VectorEnd(xMove, 0, 0, 1, xMove, yMove);

        // kiểm tra trong cột
        VectorEnd(0, yMove, 1, 0,xMove, yMove);

        // kiểm tra từ trái qua phải
        if(xMove+yMove >= maxN-1) {
            VectorEnd(maxN - 1, xMove + yMove - maxN + 1, -1, 1, xMove, yMove);
        }else {
            VectorEnd(xMove+yMove, 0, -1, 1, xMove, yMove);
        }

        // kiểm tra từ phải qua trái
        if (xMove <= yMove){
            VectorEnd(xMove - yMove + maxN + 1, maxN-1, -1, -1, xMove, yMove);
        }else {
            VectorEnd(maxN-1, maxN-1 - (xMove-yMove), -1, -1, xMove, yMove);
        }
        if (winner_play !=0 ) return true;
        else return false;
    }

    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry) {
        // kiểm tra thắng bằng vector(vx, vy) in range(rx, ry) -4 *(vx, vy)
        if (winner_play != 0 )return;
        final int range = 4;
        int i, j;
        int xbelow = rx - range*vx;
        int ybelow = ry - range*vy;
        int xabove = rx + range*vx;
        int yabove = ry + range*vy;
        String st = "";
        i = xx; j = yy;

        while (!inside(i, xbelow, xabove) || !inside(j, ybelow, yabove)){
            i+=vx; j+=vy;
        }
        while (true){
            st = st + String.valueOf(valueCell[i][j]);
            if (st.length() == 5){
                EvalEnd(st);
                st = st.substring(1,5);
            }
            i+=vx; j+=vy;
            if (!inBoard(i,j) || !inside(i, xbelow, xabove) || !inside(j, ybelow, yabove) || winner_play !=0){
                break;
            }
        }

    }

    private boolean inBoard(int i, int j) {
        // kiểm tra i, j có nằm trong board ko
        if (i < 0 ||  i>maxN-1 ||j >maxN-1 ) return false;
        return true;
    }

    private void EvalEnd(String st) {
        switch (st){
            case "1111" :winner_play = 1; break;
            case "2222" :winner_play = 2; break;
            default: break;
        }
    }

    // kiểm tra xem  i có nằm trong (xbelow, xabove)
    private boolean inside(int i, int xbelow, int xabove) {
        return (i - xbelow)*(i - xabove) <= 0 ;
    }

    private boolean noEmptyCell() {
        for (int i = 0; i < maxN; i++){
            for (int j = 0; j < maxN; j++){
                if (valueCell[i][j] == 0) return  false;
            }

        }
        return true;
    }

    private void playerTurn() {
        textView.setText("Player turn");
        firstMove = false;
        isClicked = false;
        // lắng nghe nước đi của player
    }


    private void loadResources() {
        drawCell[0] = null;
        drawCell[1] = context.getResources().getDrawable(R.drawable.ic_x);
        drawCell[2] = context.getResources().getDrawable(R.drawable.ic_o);
        drawCell[3] =   context.getResources().getDrawable(R.drawable.cell_bg);
    }

    private boolean isClicked;// kiểm tra trạng thái của ô, player đã click vào hay chưa, chỉ được click 1 ô
    @SuppressLint("NewApi")
    private void designBoardGame() {
        int sizeofCell = Math.round(SreenWidth()/maxN);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeofCell * maxN, sizeofCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeofCell, sizeofCell);

        LinearLayout linBoadGame  = (LinearLayout) findViewById(R.id.linBoard);

        // tạo ô
        for (int i= 0; i < maxN; i++){
            LinearLayout linRow = new LinearLayout(context);
            //dòng
            for (int j = 0; j < maxN; j++){
                ivCell[i][j]  = new ImageView(context);
                // tạo ô, set default cho ô, có 3 trạng thái: trống, người chơi chọn, bot chọn
                ivCell[i][j].setBackground(drawCell[3]);
                final int  x =i; final int y=j;
                ivCell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (valueCell[x][y] == 0) {// ô trống
                            if (turnPlay == 1 || !isClicked){
                                Log.d("move: ", "click to cell" );
                                isClicked = true;
                                xMove = x;
                                yMove = y; //i, j là giá trị cuối cùng
                                make_a_move();
                            }
                        }
                    }
                });
                linRow.addView(ivCell[i][j], lpCell);
            }
            linBoadGame.addView(linRow,lpRow);
        }
    }

    private float SreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm  =resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    private int Eval(String st, int p1){
        int b1 =1, b2 =1;
        if (p1 == 1){
            b1=2;
            b2 = 1;
        }else {
            b1 =1;
            b2 =2;
        }
        switch (st){
            case "111110": return b1 * 1000000000;
            case "011111": return b1 * 1000000000;
            case "211111": return b1 * 1000000000;
            case "111112": return b1 * 1000000000;
            case "011110": return b1 * 100000000;
            case "101110": return b1 * 1002;
            case "011101": return b1 * 1002;
            case "011112": return b1 * 1000;
            case "011100": return b1 * 102;
            case "001110": return b1 * 102;
            case "210111": return b1 * 100;
            case "211110": return b1 * 100;
            case "211011": return b1 * 100;
            case "211101": return b1 * 100;
            case "010100": return b1 * 10;
            case "011000": return b1 * 10;
            case "001100": return b1 * 10;
            case "211000": return b1 * 1;
            case "201100": return b1 * 1;
            case "201110": return b1 * 1;
            case "200001": return b1 * 1;

            case "222220": return b1 * -1000000000;
            case "022222": return b1 * -1000000000;
            case "122222": return b1 * -1000000000;
            case "222221": return b1 * -1000000000;
            case "022220": return b1 * -100000000;
            case "202220": return b1 * -1002;
            case "022202": return b1 * -1002;
            case "022221": return b1 * -1000;
            case "022200": return b1 * -102;
            case "002220": return b1 * -102;
            case "120222": return b1 * -100;
            case "122220": return b1 * -100;
            case "122022": return b1 * -100;
            case "122202": return b1 * -100;
            case "020200": return b1 * -10;
            case "022000": return b1 * -10;
            case "000220": return b1 * -10;
            case "122000": return b1 * -1;
            case "102200": return b1 * -1;
            case "100220": return b1 * -1;
            case "100022": return b1 * 1;
            default:break;
        }
        return  0 ;
    }
    // tìm vị trí bàn cờ
    public int getValue_Position() {
        int rr = 0;
        int pl = turnPlay;
        // dòng
        for(int i=0; i<maxN; i++){
            rr+=CheckValue(maxN-1, i, -1, 0, pl);
        }
        // cột
        for (int i= 0; i<maxN; i++){
            rr+=CheckValue(i, maxN-1, 0, -1, pl);
        }

        // ngang từ phải sang trái
        for(int i=0; i<maxN; i++){
            rr+=CheckValue(i, maxN-1, -1, -1, pl);
        }
        for (int i=0; i<maxN; i++){
            rr+=CheckValue(maxN-1, i, -1, -1, pl);
        }

        // ngang từ trái qua qua
        for (int i=0; i<maxN; i++){
            rr+=CheckValue(i, 0, -1, 1, pl);
        }

        for (int i=0; i<maxN; i++){
            rr+=CheckValue(maxN-1, i, -1, 1, pl);
        }

        return rr;
    }

    private int CheckValue(int xd, int yd, int vx, int vy, int pl) {
        int i, j;
        int rr =0;
        i =xd;j =yd;
        String st = String.valueOf(valueCell[i][j]);
        while (true){
            i+=vx; j+=vy;
            if (inBoard(i, j)){
                st = st + String.valueOf(valueCell[i][j]);
                if (st.length() == 6){
                    rr +=Eval(st, pl);
                    st = st.substring(1,6);
                }
            }else break;
        }
        return rr;
    }
}
