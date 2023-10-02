package com.example.java_ml;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;

public class App extends Application {
    static final int MASS_X = 10; // マス目の数（縦）
    static final int MASS_Y = 10; // マス目の数（横）

    public static void main(String[] args) {
        launch(args);
        NeuralNet nn = new NeuralNet(100, 100, 4);

        // 訓練データ（入力）
        NumberData nd = new NumberData();
        double knownInputs[][] = nd.return訓練Number();

        // 教師データ
        double t[][] = nd.return教師Number();

        // 学習
        System.out.println("--学習開始--");
        nn.learn(knownInputs, t);
        System.out.println("--学習終了--");

        System.out.println("\n--推論開始--");
        // ---------------------
        // 推定はここから
        // ---------------------

        double unknownInputs[] = tempnumber;

        double[] output = nn.compute(unknownInputs);
        print(unknownInputs, output);


        System.out.println("\n--推論終了--");

    }

    // 画面に入力データと実体値、予測値を表示する
    public static void print(double[] input, double[] output) {
        System.out.println();
        System.out.println("入力データ");
        for (int j = 0; j < MASS_Y; j++) {
            for (int k = 0; k < MASS_X; k++) {
                System.out.print((int) input[j * MASS_X + k]);
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println("ニューラルネットワークが予測した値（文字）：" + value(output));
    }

    // 出力データと数字のマッピングを行う
    public static int value(double[] a) {
        // ステップ関数（0.5を閾値として0,1に変換）
        //  if     0 <= an < 0.5   then  0
        //  if  0.5 <   an <    1  then  1
        int v1 = (int) (a[0] + 0.5);
        int v2 = (int) (a[1] + 0.5);
        int v3 = (int) (a[2] + 0.5);
        int v4 = (int) (a[3] + 0.5); // int型に変換 → 切り捨て　0.5 → 0　1.5 → 1

        // 出力層の値
        return v1 * 8 + v2 * 4 + v3 * 2 + v4 * 1;
    }

    // ---------------------
    // 追加分はここから
    // ---------------------

    // 入力データ用の配列
    static double[] tempnumber = new double[100];

    // キャンバス付きのウィンドウを表示する
    public void start(Stage stage) {
        try {
            // シーングラフの構成
            BorderPane  root = new BorderPane();
            root.setCenter( createCanvas() );

            // ウィンドウの表示
            Scene scene = new Scene(root,200,200);
            stage.setScene(scene);
            stage.setTitle("JavaML");
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // キャンバスと描画に関する処理
    protected Pane createCanvas() {

        int     width   = 100; //キャンバスの大きさ  100 * 100
        int     height  = 100; //

        VBox    layout  = new VBox(); //キャンバス、ボタンの配置
        Canvas  canvas  = new Canvas( width , height );
        Button  消去ボタン = new Button( /*"init"*/"消去" );
        Button  確定ボタン = new Button( /*"parse"*/"確定" );

        layout.getChildren().add( canvas );
        layout.getChildren().add( 消去ボタン );
        layout.getChildren().add( 確定ボタン );


        GraphicsContext g   = canvas.getGraphicsContext2D(); //描画処理の取得
        g.setFill( Color.WHITE ); //キャンバスの初期色
        g.fillRect( 0 , 0 , width , height ); //キャンバスの初期化

        g.setStroke(Color.SILVER); //補助線の色
        for(int i = 0;i <= 10;i++) {
            g.strokeLine(0, 10 * i, 100, 10 * i);
            g.strokeLine(10 * i, 0, 10 * i, 100);
        } //補助線の描画


        // クリック時の描画
        canvas.addEventHandler(
            MouseEvent.ANY ,
            e -> {

                Color   col     = null; // 描画色の宣言

                // 描画色の決定
                switch( e.getButton() ) {
                    case PRIMARY: // 左クリック
                        col = Color.BLACK; break;
                    case SECONDARY: // 右クリック
                        col = Color.WHITE; break;
                    default:
                        return;
                }

                GraphicsContext g1  = canvas.getGraphicsContext2D();

                // 押下場所の色を変更
                g1.setFill( col );
                g1.fillRect( e.getX() - 5 , e.getY() - 5 ,  10 , 10 );

            });

        // 消去ボタン押下時にキャンバスを初期化
        消去ボタン.addEventHandler(
            MouseEvent.MOUSE_CLICKED ,
            e -> {

                // キャンバスの初期化
                GraphicsContext g2   = canvas.getGraphicsContext2D();
                g2.setFill( Color.WHITE );
                g2.fillRect( 0 , 0 , width , height );

                g.setStroke(Color.SILVER);
                for(int i = 0;i <= 10;i++) {
                    g.strokeLine(0, 10 * i, 100, 10 * i);
                    g.strokeLine(10 * i, 0, 10 * i, 100);
                }
            });

        // 確定ボタン押下時にキャンバスの内容を保存 + 縮小
        確定ボタン.addEventHandler(
            ActionEvent.ANY ,
            e -> {
                // キャンバスの内容を取得
                WritableImage img = canvas.snapshot( null , null );
                // 保存可能な型の宣言
                BufferedImage resultImg = new BufferedImage((int)img.getWidth(), (int)img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                // 保存可能な型に変換
                SwingFXUtils.fromFXImage(img,resultImg);


                // NNへ入力するために0と1のみの配列を作成
                try {
                    // 画像をpngで保存
                    ImageIO.write(resultImg, "png", new File("test.png"));

                    // 変換したファイルの取得
                    BufferedImage bufferedImage = ImageIO.read(new File("test.png"));

                    // 取得した画像の変換先の宣言
                    BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_BINARY);

                    // 取得した画像を10分の1(10ピクセル * 10ピクセル)に縮小 + 白黒のみの画像に変換
                    image.createGraphics().drawImage(bufferedImage.getScaledInstance(10,10,Image.SCALE_AREA_AVERAGING),0,0,10,10,null);

                    //　変換した画像を保存
                    ImageIO.write(image, "png", new File("newtest.png"));

                    // 保存した画像を読み取り可能な型に変換
                    WritableImage newImg = SwingFXUtils.toFXImage(image, null);

                    // ピクセルを配列に格納するためのフォーマットの定義
                    WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();

                    // ピクセル数の取得 = 100
                    int size = (int) (newImg.getWidth() * newImg.getHeight());

                    // ピクセルのデータを格納する配列
                    int[] pixels = new int[ size ];

                    // ピクセルの格納
                    // 左上から右下へ
                    //(読み取り始めるX座標、Y座標、読み取る幅、高さ、フォーマット、格納先の配列、配列へのオフセット、次の行までの距離)
                    newImg.getPixelReader().getPixels( 0 , 0 , (int)newImg.getWidth() , (int)newImg.getHeight() ,
                            format , pixels, 0 , (int)newImg.getWidth() );

                    // 白ならば0、黒ならば1を配列に取得
                    for( int i=0 ; i<size ; i++ ) {
                        if (pixels[i] == -1) {
                            tempnumber[i] = 0;
                        } else {
                            tempnumber[i] = 1;
                        }
                    }

                    // ウィンドウの終了
                    Platform.exit();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            });

        return layout;
    }

}
