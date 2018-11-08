package arndt.com.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

//    private SudokuBits sudokuBits;
    private Sudoku sudoku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 10; i++) {
            final Button b = findViewById(getResId("button"+i, R.id.class));
            b.setOnClickListener(view -> {
                if(editMe != null) {
                    CharSequence t = b.getText();
                    editMe.setText(t);
                    editMe.setBackground(null);
                    sudoku.putNumber(Integer.parseInt(String.valueOf(b.getText())),editMe.getId());
                    editMe = null;
                }
            });
        }

        findViewById(getResId("random", R.id.class)).setOnClickListener(view -> {
            sudoku = new Sudoku(Sudoku.getRandom(Integer.parseInt(
                    ((EditText)findViewById(getResId("size",R.id.class))).getText().toString())));
            update(sudoku.toString());
        });

        findViewById(getResId("reset", R.id.class)).setOnClickListener(view -> {
            sudoku= new Sudoku();
            update(sudoku.toString());
        });

        findViewById(getResId("solve", R.id.class)).setOnClickListener(view -> {
            sudoku = Sudoku.solve(sudoku);
            update(sudoku.toString());
        });

        TableLayout gl = findViewById(R.id.grid);
        sudoku = new Sudoku();
        String board = sudoku.toString();
        int i = 0, k = 0;
        for(String row : board.split("\n")) {
            TableRow tr = new TableRow(this);
            int j = 0;
            for(Character column : row.toCharArray()) {
                TextView v = new TextView(this);
                v.setId(k);
                v.setWidth(tr.getWidth()/9);
                v.setGravity(Gravity.CENTER);
                v.setText(""+column);
                v.setTextAppearance(this, android.R.style.TextAppearance_Large);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBackground(view);
                        editMe = (TextView) view;
                    }
                });
                tr.addView(v);
                if((j+1)%3==0 && j < 8) {
                    View view = new View(this);
                    view.setLayoutParams(new TableRow.LayoutParams(1,TableRow.LayoutParams.MATCH_PARENT));
                    tr.addView(view);
                }
                j++;
                k++;
            }
            gl.addView(tr);
            if((i+1)%3==0) {
                TableRow ttr = new TableRow(this);
                for (Character column : row.toCharArray()) {
                    TextView v = new TextView(this);
                    v.setWidth(tr.getWidth()/9);
                    v.setGravity(Gravity.CENTER);
                    v.setText(" ");
                    ttr.addView(v);
                }
                gl.addView(ttr);
            }
            i++;
        }
    }

    private void update(String board) {
        int i = 0;
        for (String row : board.split("\n")) {
            for (Character column : row.toCharArray()) {
                ((TextView) findViewById(i)).setText(column + "");
                i++;
            }
        }
    }

    private void setBackground(View view){
        //use a GradientDrawable with only one color set, to make it a solid color
        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, 0xFF000000); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(border);
        } else {
            view.setBackground(border);
        }
    }

    private TextView editMe;

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
