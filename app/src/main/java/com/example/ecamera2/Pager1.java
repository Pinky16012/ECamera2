package com.example.ecamera2;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.ecamera2.compare;

import androidx.appcompat.app.AlertDialog;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class Pager1 extends RelativeLayout{
    String[] items={"強度平衡", "水平構圖","三分構圖","消失點構圖"};
    private int checkRecommend = 0;
    private Context context1;
    private Mat template = new Mat();
    private Point origP = new Point();
    private Point DistinationP = new Point();
    private Mat horizonMat = new Mat();

    private int counter = 0;

    List<String> Score = new ArrayList<>();
    View view;

    public Pager1(Context context) {
        super(context);
        context1 = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.my_pager1, null);//連接頁面
        ImageButton select_composition = view.findViewById(R.id.btn_selectMode);//取得頁面元件
        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //將元件放入ViewPager




    }

    boolean ib;
    boolean horizontal;
    boolean third;
    boolean vp;
    boolean frame;
    private Bitmap recommendImg;
    private Bitmap outputImg;
    private ImageView img_recommend;
    boolean moveCorrect = false;

    public  String changeType(double a){
        int score = (int)a;
        String score_string = Integer.toString(score);
        return  score_string;
    }

    private int highestComposition = 0;
    private double highest = 0.0;

    public void selectMode(Bitmap bitmap, Mat img,boolean check[],Context context,int checkRecommend_,int counter_){
        ib = check[0];
        horizontal = check[1];
        third = check[2];
        vp = check[3];
        checkRecommend = checkRecommend_;
        counter = counter_;
        String nameofcom = new String();
        final IB i = new IB();
        final horizontal h = new horizontal();
        final RoThird r = new RoThird();
        final vanishpoint v = new vanishpoint();
        final framewithframe frame = new framewithframe();
        //宣告frame



        Double score[] ={0.0,0.0,0.0,0.0,0.0};

        String SScore[] = {null,null,null,null,null};

        if(checkRecommend == 0){

            for(int a = 0;a <=4 ;a++){   //得到分數
                if(check[a] == true){
                    if(a == 0){
                        score[0] = i.getIBscore(bitmap);
                        SScore[0] = changeType(score[0]);
                        nameofcom = "強度平衡:" + SScore[0];
                        Score.add(nameofcom);
                    }else if(a == 1){
                        score[1] = h.horizontal_composition(bitmap);
                        SScore[1] = changeType(score[1]);
                        nameofcom = "水平構圖:" + SScore[1];
                        Score.add(nameofcom);
                    }else if(a == 2){
                        score[2] = r.rotMain(bitmap);
                        SScore[2] = changeType(score[2]);
                        nameofcom = "三分構圖:" + SScore[2];
                        Score.add(nameofcom);
                    }else if(a == 3){
                        score[3] = v.vanishpoint(img);
                        SScore[3] = changeType(score[3]);
                        nameofcom = "消失點構圖:" + SScore[3];
                        Score.add(nameofcom);

                    }else if(a == 4){
//                        outputImg = frame.frameCal(bitmap);
//                        score[4] = i.getIBscore(bitmap); //改為frame
//                        SScore[4] = changeType(score[4]);
                    }
                }else{
                    SScore[a] = " ";
                }
            }

            //顯示分數
            String finalScore = "您的構圖分數\n";
            for(int j = 0; j < Score.size(); j++) {
                finalScore = finalScore + Score.get(j) + "\n";
            }
            Score.clear();
            new AlertDialog.Builder(context)
                    .setTitle("分數")
                    .setMessage(finalScore)
                    .show();

            //選擇高分的
            if(check[1] || check[2] || check[3] || check[4]){
                if (score[1] < score[2]){
                    if(score[2] < score[3]){
                        if(score[3] < score[4]){
                            highestComposition = 4;
                            highest = score[4];
                        }else{
                            highestComposition = 3;
                            highest = score[3];
                        }
                    }else if(score[2] > score[3]){
                        if(score[2] < score[4]){
                            highestComposition = 4;
                            highest = score[4];
                        }else{
                            highestComposition = 2;
                            highest = score[2];
                        }
                    }
                }else if(score[1] > score[2]){
                    if(score[1] < score[3]){
                        if(score[3] < score[4]){
                            highestComposition = 4;
                            highest = score[4];
                        }else{
                            highestComposition = 3;
                            highest = score[3];
                        }
                    }else if (score[1] > score[3]){
                        if(score[1] < score[4]){
                            highestComposition = 4;
                            highest = score[4];
                        }else{
                            highestComposition = 1;
                            highest = score[1];
                        }
                    }
                }
                //推薦

            }
            if(check[0] == true && check[1] == false && check[2] == false && check[3] == false && check[4] == false){
                checkRecommend = 0;
            }
        }

        if(checkRecommend == 1){
//            i.getIBscore(bitmap);
//            h.horizontal_composition(bitmap);
//            r.rotMain(bitmap);
//            v.vanishpoint(img);
            //frame
            selectRecommend(highestComposition,highest,bitmap,img,context);
        }else
            selectRecommend(highestComposition,highest,bitmap,img,context);


    }

    public void selectRecommend(int a,double score,Bitmap bitmap1,Mat img1,Context context){

        final horizontal h = new horizontal();
        final RoThird r = new RoThird();
        final vanishpoint v = new vanishpoint();
//        final compare c = new compare();
        if (counter >= 5){checkRecommend = 2; moveCorrect = false;}
        if(checkRecommend == 1){
            counter += 1;
            //判斷移動相機是否正確
            if(a == 1){   //水平構圖
                double temscore = h.horizontal_composition(bitmap1);
                if (temscore >= 90){
                    moveCorrect = true;
                }

            }else if(a == 2){   //三分構圖
                Mat tem_img = new Mat();
                Utils.bitmapToMat(bitmap1, tem_img);
                Mat template2 = new Mat();
                template2 = compare.pictureCut(template, origP);
                moveCorrect = compare.comparePic(img1,template2,DistinationP);

            }else if(a == 3){  //消失點
                Mat template2 = new Mat();
                template2 = compare.pictureCut(template, origP);
                moveCorrect = compare.comparePic(img1,template2,DistinationP);

            }
        }

        if(score >= 95 && checkRecommend == 0){
            checkRecommend = 0;
            moveCorrect = false;
            new AlertDialog.Builder(context)
                    .setTitle("恭喜!")
                    .setMessage("這是一張符合構圖的照片")
                    .show();
        }else if(checkRecommend == 1 && moveCorrect == true){
            checkRecommend = 0;
            moveCorrect = false;
            counter = 0;
            new AlertDialog.Builder(context)
                    .setTitle("恭喜!")
                    .setMessage("這是一張符合構圖的照片")
                    .show();
        }else if (checkRecommend == 1 && moveCorrect == false){
            new AlertDialog.Builder(context)
                    .setTitle("矯正失敗")
                    .setMessage("請在試一次")
                    .show();
        }
        else if(checkRecommend == 0){
            if(a == 1){

                if(checkRecommend == 0){
                    h.horizontal_composition(bitmap1);
                    horizonMat = h.pictureCut(img1, bitmap1);
                    DistinationP= h.returnDisP();
                    recommendImg = h.recommend(bitmap1); //水平構圖

                }
            }else if(a == 2){
                if(checkRecommend == 0){
                    r.rotMain(bitmap1);
                    recommendImg = r.recommend(bitmap1); //三分構圖
                    template = r.returnMat();
                    origP = r.returnOrigP();
                    DistinationP= r.returnDistinationP();
                }
            }else if(a == 3){
                if(checkRecommend == 0){
                    v.vanishpoint(img1);
                    Mat z = new Mat();
                    z = v.draw_rec(img1);
                    Utils.matToBitmap(z, bitmap1);
                    recommendImg = bitmap1;
                    template = v.getTargetPic();
                    origP = v.getOrigP();
                    DistinationP= v.getCalPoint();
                }

            }
            checkRecommend = 1;
        }else if (checkRecommend == 2){
            new AlertDialog.Builder(context)
                    .setTitle("矯正失敗")
                    .setMessage("請您再次選擇構圖")
                    .show();
            checkRecommend = 0;
            counter = 0;
        }
    }
    public int getCheckRecommendValue(){
        return checkRecommend;
    }

    public Bitmap getRecommendImg() {return recommendImg; }

    public int getCounter(){ return  counter;}
}
