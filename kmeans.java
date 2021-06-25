import java.util.*;
import java.util.Map.Entry;
import java.io.*;

class Main {
    public static void main(String srgs[]) {
        Scanner scan = new Scanner(System.in);//�W������
        System.out.print("���̓t�@�C�����F");
        String file = scan.next();//���̓t�@�C����(csv����)
        String line = null;
        int dim = 0, datanum = 0;//dim:�������Adatanum:�f�[�^��
        String[] data = null;
        try {//���̓t�@�C���̎������ƃf�[�^���̎Z�o
            File csv = new File(file);
            BufferedReader bf_init = new BufferedReader(new FileReader(csv));
            while (bf_init.ready()) {
                line = bf_init.readLine();
                data = line.split(",");
                datanum++;
            }
            dim = data.length;
            bf_init.close();
        } catch (FileNotFoundException e) {//��O����
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = null;
        System.out.println("�������F" + dim);
        System.out.println("�f�[�^���F" + datanum);
        double[][] imputdata = new double[datanum][dim];//���̓f�[�^
        try {
            File csv = new File(file);
            BufferedReader bf = new BufferedReader(new FileReader(csv));
            int i = 0;
            while (bf.ready()) {
                line = bf.readLine();
                data = line.split(",");
                for (int j = 0; j < data.length; j++) {
                    imputdata[i][j] = Double.parseDouble(data[j]);//double�œǂݍ���
                }
                i++;
            }
            bf.close();
        } catch (FileNotFoundException e) {//��O����
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("�N���X�^�[���F");
        int clnum = scan.nextInt();//�N���X�^�[���̓���
        double[][] clrep = new double[clnum][dim];// ��\�_
        int[] claff = new int[datanum];// ��\�����_
        for (int a = 0; a < claff.length; a++) {//�����l�͑�\�_���ǂ��ɂ������Ȃ�-1�Ƃ���B
            claff[a] = -1;
        }
        ArrayList<Integer> shuffleList = new ArrayList<Integer>();//��������͍ŏ��̑�\�_�������_���Ɍ���
        for (int a = 0; a < datanum; a++) {
            shuffleList.add(a);
        }//�f�[�^�����̒l��arraylist�œ���A�N���X�^�[�����̒l���������Ă��āA���̎����Ă��������̗v�f�ԍ��̃f�[�^���ŏ��̑�\�_�Ƃ���
        Collections.shuffle(shuffleList);
        scan.close();
        for (int a = 0; a < clnum; a++) {
            clrep[a] = imputdata[shuffleList.get(a).intValue()];
        }
        Map<Double, Integer> map = new HashMap<>();
        Entry<Double, Integer> min;
        boolean flag = false;
        int temp = 0;
        while (!flag) {//�������炪k-means�@�̏���
            flag = false;
            for (int a = 0; a < datanum; a++) {
                min = null;
                map = new HashMap<>();
                for (int b = 0; b < clrep.length; b++) {//hashmap�Ɂu������͍��W�Ƃ��ꂼ��̑�\�_�Ƃ̋����v�Ɓu�Ή������\�_�v������
                    map.put(distance(clrep[b], imputdata[a]), b);
                }
                for (Entry<Double, Integer> entry : map.entrySet()) {
                    if (min == null || min.getKey() > entry.getKey()) {//map���ɂ��钆�ň�ԋ������Z��
                        min = entry;
                    }
                }
                temp = claff[a];
                claff[a] = min.getValue();//��ԋ������Z�����̂����̑Ή������\�_�̃N���X�^�ɏ���������B
                if (temp != claff[a]) {
                    flag = true;
                }
            }
            if (!flag) {//��x����\�_���ς��Ȃ��Ȃ�k-means�I���
                break;
            }
            clrep=new double[clnum][dim];
            //��������͑�\�_�̍X�V
            for (int a = 0; a < clnum; a++) {//������a:��\�_�̃N���X�^�ԍ�
                temp = 0;
                for (int b = 0; b < datanum; b++) {//������b:�f�[�^�ԍ�
                    if (claff[b] == a) {//������\�_�̃N���X�^�ԍ��ƁA���̃f�[�^�ԍ��ɑΉ����鏊���N���X�^�ԍ�����v���Ă���Ȃ�
                        temp++;//temp:�d�S���v�Z����Ƃ��Ɋ���l
                        for (int c = 0; c < dim; c++) {//������c:�����ԍ�
                            clrep[a][c] += imputdata[b][c];//�d�S�����߂邽�߂ɉ��Z����
                        }
                    }
                }
                for (int b = 0; b < clnum; b++) {
                    for (int c = 0; c < dim; c++) {
                        clrep[b][c] /= temp;//���v�l���N���X�^�ɏ�������f�[�^��
                    }
                }
            }
        }
        for (int a = 0; a < datanum; a++) {//���ʂ̏o��
            System.out.print("���W�F(");
            for (int b = 0; b < dim; b++) {
                System.out.print(imputdata[a][b] + " ");
            }
            System.out.println(")�@�����N���X�^�ԍ��F" +claff[a]);
        }
    }

    public static double distance(double[] a, double[] b) {//�����̌v�Z�A�}���n�b�^�������ɂ��܂���
        double ans=0;
        for(int x=0; x<a.length; x++){
            ans+=(Math.abs(a[x] - b[x]));
        }
        return ans;
    }
}