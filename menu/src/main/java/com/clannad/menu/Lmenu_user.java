package com.clannad.menu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import butterknife.OnClick;


public class Lmenu_user extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmenu_user);



    }
    @OnClick({R.id.mine_avatar, R.id.rl_nick, R.id.rl_sex, R.id.rl_email, R.id.rl_account, R.id.exit_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_avatar:
                System.out.println("mine_avatar");
                //showAvatarPop();
                break;
            case R.id.rl_nick:
                System.out.println("rl_nick");
                //AlertDialog nickNameDialog = buildeChangeInfoDialog(UPDATE_USER_NICKNAME);
               // nickNameDialog.show();
                break;
            case R.id.rl_sex:
                System.out.println("rl_sex:");
               //changeUserSex();
                break;
            case R.id.rl_email:
                System.out.println("rl_email");
                //AlertDialog emailDialog = buildeChangeInfoDialog(UPDATE_USER_EMAIL);
                //emailDialog.show();
                break;
            case R.id.rl_account:
                System.out.println("rl_account:");
                //  login();
                break;
            case R.id.exit_btn:
                System.out.println("exit_btn:");
                //   goToRegisterActivity();
                break;
        }
    }


    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;
    public String filePath = "";
    private void showAvatarPop() {
//        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
//                null);
//        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
//        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
//        layout_photo.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                // TODO Auto-generated method stub
//                layout_choose.setBackgroundColor(getResources().getColor(
//                        R.color.base_color_text_white));
//                layout_photo.setBackgroundDrawable(getResources().getDrawable(
//                        R.drawable.pop_bg_press));
//                File dir = new File(BmobConstants.MyAvatarDir);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                // 原图
//                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
//                        .format(new Date()));
//                filePath = file.getAbsolutePath();// 获取相片的保存路径
//                Uri imageUri = Uri.fromFile(file);
//
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent,
//                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
//            }
//        });
//        layout_choose.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                //  ShowLog("点击相册");
//                layout_photo.setBackgroundColor(getResources().getColor(
//                        R.color.base_color_text_white));
//                layout_choose.setBackgroundDrawable(getResources().getDrawable(
//                        R.drawable.pop_bg_press));
//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent,
//                        BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
//            }
//        });
//
//        avatorPop = new PopupWindow(view, mScreenWidth, 600);
//        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                    avatorPop.dismiss();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//        avatorPop.setTouchable(true);
//        avatorPop.setFocusable(true);
//        avatorPop.setOutsideTouchable(true);
//        avatorPop.setBackgroundDrawable(new BitmapDrawable());
//        // 动画效果 从底部弹起
//        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
//        avatorPop.showAtLocation(exitBtn, Gravity.BOTTOM, 0, 0);
    }

}
