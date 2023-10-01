package com.example.java_ml;

import java.util.Random;

public class NeuralNet {

    final int N_INPUT;  // 入力層の数
    final int N_HIDDEN; // 中間層の数
    final int N_OUTPUT;  // 出力層の数

    double w1[][];    //入力層>隠れ層の重み
    double w2[][];    //隠れ層>出力層の重み
    double b1[];    //定数1を入力とした入力層>隠れ層の重み 数式的には閾値θとした時の-θと等しい
    double b2[];    //定数1を入力とした隠れ層>出力層の重み 数式的には閾値θとした時の-θと等しい

    double input[];  // 入力層
    double hidden[]; // 中間層
    double output[]; // 出力層

    double alpha = 0.1;    //学習率

    public NeuralNet(int nInput, int nHidden, int nOutput) {
        N_INPUT = nInput;
        N_HIDDEN = nHidden;
        N_OUTPUT = nOutput;

        input = new double[N_INPUT];  // 入力層
        hidden = new double[N_HIDDEN]; // 中間層
        output = new double[N_OUTPUT]; // 出力層

        // 重みを-0.1~0.1で初期化
        Random rnd = new Random();

        w1 = new double[N_INPUT][N_HIDDEN];
        for (int i = 0; i < N_INPUT; i++) {
            for (int j = 0; j < N_HIDDEN; j++) {
                w1[i][j] = (rnd.nextDouble() * 2.0 - 1.0) * 0.1;
            }
        }
        b1 = new double[N_HIDDEN]; // w0x0

        w2 = new double[N_HIDDEN][N_OUTPUT];
        for (int i = 0; i < N_HIDDEN; i++) {
            for (int j = 0; j < N_OUTPUT; j++) {
                w2[i][j] = (rnd.nextDouble() * 2.0 - 1.0) * 0.1;
            }
        }
        b2 = new double[N_OUTPUT];
    }

    // NNに入力し、出力を計算する
    public double[] compute(double x[]) {

        // 入力層の入力
        for (int i = 0; i < N_INPUT; i++) {
            input[i] = x[i];
        }

        // 中間層の計算
        for (int i = 0; i < N_HIDDEN; i++) {
            hidden[i] = 0.0;
            for (int j = 0; j < N_INPUT; j++) {
                hidden[i] += w1[j][i] * input[j];
            }
            hidden[i] += b1[i];
            hidden[i] = sigmoid(hidden[i]);
        }

        // 出力層の計算
        for (int i = 0; i < N_OUTPUT; i++) {
            output[i] = 0.0;
            for (int j = 0; j < N_HIDDEN; j++) {
                output[i] += w2[j][i] * hidden[j];
            }
            output[i] += b2[i];
            output[i] = sigmoid(output[i]);
        }

        return output;
    }

    // シグモイド関数
    public double sigmoid(double i) {
        double a = 1.0 / (1.0 + Math.exp(-i));
        return a;
    }

    // 誤差逆伝播法による重みの更新
    public void backPropagation(double teach[]) {

        // 誤差
        double[] deltas = new double[N_OUTPUT];

        // 中間層>出力層の重みを更新
        for (int j = 0; j < N_OUTPUT; j++) {
            deltas[j] = (teach[j] - output[j]) * output[j] * (1.0 - output[j]);
            for (int i = 0; i < N_HIDDEN; i++) {
                w2[i][j] += alpha * deltas[j] * hidden[i];
            }
            b2[j] += alpha * deltas[j];
        }

        // 入力層>中間層の重みを更新
        for (int i = 0; i < N_HIDDEN; i++) {

            double sum = 0.0;
            for (int j = 0; j < N_OUTPUT; j++) {
                sum += w2[i][j] * deltas[j]; //誤差の逆伝播
            }

            double delta = hidden[i] * (1.0 - hidden[i]) * sum;
            for (int j = 0; j < N_INPUT; j++) {
                w1[j][i] += alpha * delta * input[j];
            }
            b1[i] += alpha * delta;
        }
    }

    // 二乗誤差
    public double calcError(double teach[]) {
        double e = 0.0;
        for (int i = 0; i < teach.length; i++) {
            e += Math.pow(teach[i] - output[i], 2.0); // Math.pow(n,m) = nのm乗
        }
        e *= 0.5;
        return e;
    }

    // 学習
    public void learn(double[][] knownInputs, double[][] teach) {

        int step = 0; //試行回数
        while (true) {

            double e = 0.0; // 二乗誤差の総和(初期値は0.0)

            // すべての訓練データをニューラルネットワークに入力・計算・誤差伝搬
            for (int i = 0; i < knownInputs.length; i++) {
                compute(knownInputs[i]);
                backPropagation(teach[i]);
                e += calcError(teach[i]);
            }

            // 100刻みで誤差を表示
            if (step % 500 == 0) {
                System.out.println("step:" + step + ", loss=" + e);
            }

            // 二乗誤差が十分小さくなったら、終了
            if (e < 0.0001) {
                break;
            }

            step++;
        }

    }

}
