# 手書きで入力した数字を画像認識するシステム

授業の最終課題として提出したシステムです。授業内では、画像認識させたい数字を手入力で表現していました。

例）0を入力したい場合

![0を入力したい場合](入力例.png)

私はこれを発展させ、**手書きで入力**できるようにしました。

プログラムを起動すると、このようにキャンバス付きのウィンドウが表示されます。

![ウィンドウ](ウィンドウ.png)

0～9の数字を手書きで入力し確定ボタンを押すと、白を0、黒を1とした10×10の配列に変換されます。

![数字](test.png)

その後、用意していた訓練データによってコンピュータが機会学習を行い、最終的には手書き入力した数字が何かを推測します。

![推論結果](推論結果.png)
