import java.util.*;
import java.util.Map.Entry;
import java.io.*;

class Main {
    public static void main(String srgs[]) {
        Scanner scan = new Scanner(System.in);//標準入力
        System.out.print("入力ファイル名：");
        String file = scan.next();//入力ファイル名(csv限定)
        String line = null;
        int dim = 0, datanum = 0;//dim:次元数、datanum:データ数
        String[] data = null;
        try {//入力ファイルの次元数とデータ数の算出
            File csv = new File(file);
            BufferedReader bf_init = new BufferedReader(new FileReader(csv));
            while (bf_init.ready()) {
                line = bf_init.readLine();
                data = line.split(",");
                datanum++;
            }
            dim = data.length;
            bf_init.close();
        } catch (FileNotFoundException e) {//例外処理
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = null;
        System.out.println("次元数：" + dim);
        System.out.println("データ数：" + datanum);
        double[][] imputdata = new double[datanum][dim];//入力データ
        try {
            File csv = new File(file);
            BufferedReader bf = new BufferedReader(new FileReader(csv));
            int i = 0;
            while (bf.ready()) {
                line = bf.readLine();
                data = line.split(",");
                for (int j = 0; j < data.length; j++) {
                    imputdata[i][j] = Double.parseDouble(data[j]);//doubleで読み込む
                }
                i++;
            }
            bf.close();
        } catch (FileNotFoundException e) {//例外処理
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("クラスター数：");
        int clnum = scan.nextInt();//クラスター数の入力
        double[][] clrep = new double[clnum][dim];// 代表点
        int[] claff = new int[datanum];// 代表所属点
        for (int a = 0; a < claff.length; a++) {//初期値は代表点がどこにも属さない-1とする。
            claff[a] = -1;
        }
        ArrayList<Integer> shuffleList = new ArrayList<Integer>();//ここからは最初の代表点をランダムに決定
        for (int a = 0; a < datanum; a++) {
            shuffleList.add(a);
        }//データ数分の値をarraylistで入れ、クラスター数分の値だけ持ってきて、その持ってきた数字の要素番号のデータを最初の代表点とする
        Collections.shuffle(shuffleList);
        scan.close();
        for (int a = 0; a < clnum; a++) {
            clrep[a] = imputdata[shuffleList.get(a).intValue()];
        }
        Map<Double, Integer> map = new HashMap<>();
        Entry<Double, Integer> min;
        boolean flag = false;
        int temp = 0;
        while (!flag) {//ここからがk-means法の処理
            flag = false;
            for (int a = 0; a < datanum; a++) {
                min = null;
                map = new HashMap<>();
                for (int b = 0; b < clrep.length; b++) {//hashmapに「ある入力座標とそれぞれの代表点との距離」と「対応する代表点」を入れる
                    map.put(distance(clrep[b], imputdata[a]), b);
                }
                for (Entry<Double, Integer> entry : map.entrySet()) {
                    if (min == null || min.getKey() > entry.getKey()) {//map内にある中で一番距離が短い
                        min = entry;
                    }
                }
                temp = claff[a];
                claff[a] = min.getValue();//一番距離が短いものをその対応する代表点のクラスタに所属させる。
                if (temp != claff[a]) {
                    flag = true;
                }
            }
            if (!flag) {//一度も代表点が変わらないならk-means終わり
                break;
            }
            clrep=new double[clnum][dim];
            //ここからは代表点の更新
            for (int a = 0; a < clnum; a++) {//ここのa:代表点のクラスタ番号
                temp = 0;
                for (int b = 0; b < datanum; b++) {//ここのb:データ番号
                    if (claff[b] == a) {//もし代表点のクラスタ番号と、そのデータ番号に対応する所属クラスタ番号が一致しているなら
                        temp++;//temp:重心を計算するときに割る値
                        for (int c = 0; c < dim; c++) {//ここのc:次元番号
                            clrep[a][c] += imputdata[b][c];//重心を求めるために加算する
                        }
                    }
                }
                for (int b = 0; b < clnum; b++) {
                    for (int c = 0; c < dim; c++) {
                        clrep[b][c] /= temp;//合計値÷クラスタに所属するデータ数
                    }
                }
            }
        }
        for (int a = 0; a < datanum; a++) {//結果の出力
            System.out.print("座標：(");
            for (int b = 0; b < dim; b++) {
                System.out.print(imputdata[a][b] + " ");
            }
            System.out.println(")　所属クラスタ番号：" +claff[a]);
        }
    }

    public static double distance(double[] a, double[] b) {//距離の計算、マンハッタン距離にしました
        double ans=0;
        for(int x=0; x<a.length; x++){
            ans+=(Math.abs(a[x] - b[x]));
        }
        return ans;
    }
}