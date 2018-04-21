package android.dkh.com.caro;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView imgView;
    private Chessboard chessboard;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = findViewById(R.id.img);
        chessboard = new Chessboard(this, 600, 600, 3, 3);
        chessboard.init();

        bitmap = chessboard.drawBoard();

        imgView.setImageBitmap(bitmap);

        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return chessboard.onTouch(view, motionEvent);
            }
        });
    }
}
