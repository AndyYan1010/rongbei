package com.bt.andy.rongbei;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bt.andy.rongbei.fragment.Check_F;
import com.bt.andy.rongbei.fragment.Home_F;
import com.bt.andy.rongbei.fragment.Plan_F;
import com.bt.andy.rongbei.fragment.Search_F;
import com.bt.andy.rongbei.fragment.User_F;
import com.bt.andy.rongbei.utils.ToastUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private long        exitTime   = 0;//记录点击物理返回键的时间
    // 界面底部的菜单按钮
    private ImageView[] bt_menu    = new ImageView[5];
    // 界面底部的未选中菜单按钮资源
    private int[]       select_off = {R.drawable.bt_menu_0_select, R.drawable.bt_menu_1_select, R.drawable.bt_menu_2_select, R.drawable.bt_menu_3_select, R.drawable.bt_menu_4_select};
    // 界面底部的选中菜单按钮资源
    private int[]       select_on  = {R.drawable.guide_home_on, R.drawable.stock_on, R.drawable.search_huibao_on, R.drawable.plan_on, R.drawable.guide_account_on};
    // 界面底部的菜单按钮id
    private int[]       bt_menu_id = {R.id.iv_menu_0, R.id.iv_menu_1, R.id.iv_menu_2, R.id.iv_menu_3, R.id.iv_menu_4};
    //底部布局按钮的id
    private int[]       linear_id  = {R.id.linear0, R.id.linear1, R.id.linear2, R.id.linear3, R.id.linear4};
    private LinearLayout linear_home;
    private LinearLayout linear_check;//检验
    private LinearLayout linear_search;//查看
    private LinearLayout linear_plan;//计划
    private LinearLayout linear_mine;
    private Home_F       home_F;//主页
    private Check_F      check_F;//检验
    private Search_F     search_F;//查看工序
    private Plan_F       plan_F;//查看工序
    private User_F       user_F;//个人中心

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setView();
        setData();
    }

    private void setView() {
        linear_home = findViewById(R.id.linear0);
        linear_check = findViewById(R.id.linear1);
        linear_search = findViewById(R.id.linear2);
        linear_plan = findViewById(R.id.linear3);
        linear_mine = findViewById(R.id.linear4);
    }

    private void setData() {
        // 找到底部菜单的按钮并设置监听
        for (int i = 0; i < bt_menu.length; i++) {
            bt_menu[i] = (ImageView) findViewById(bt_menu_id[i]);
        }
        linear_home.setOnClickListener(this);
        linear_check.setOnClickListener(this);
        linear_search.setOnClickListener(this);
        linear_plan.setOnClickListener(this);
        linear_mine.setOnClickListener(this);
        // 初始化默认显示的界面
        if (home_F == null) {
            home_F = new Home_F();
            addFragment(home_F);
            showFragment(home_F);
        } else {
            showFragment(home_F);
        }
        // 设置默认首页为点击时的图片
        bt_menu[0].setImageResource(select_on[0]);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear0:
                // 主界面
                if (home_F == null) {
                    home_F = new Home_F();
                    // 判断当前界面是否隐藏，如果隐藏就进行添加显示，false表示显示，true表示当前界面隐藏
                    addFragment(home_F);
                    showFragment(home_F);
                } else {
                    if (home_F.isHidden()) {
                        showFragment(home_F);
                    }
                }
                break;
            case R.id.linear1:
                if (!"".equals(MyAppliaction.userType) && !MyAppliaction.userType.contains("检测")) {
                    ToastUtils.showToast(this, "您没有检测权限");
                    return;
                }
                // 检验
                if (check_F == null) {
                    check_F = new Check_F();
                    // 判断当前界面是否隐藏，如果隐藏就进行添加显示，false表示显示，true表示当前界面隐藏
                    if (!check_F.isHidden()) {
                        addFragment(check_F);
                        showFragment(check_F);
                    }
                } else {
                    if (check_F.isHidden()) {
                        showFragment(check_F);
                    }
                }
                break;
            case R.id.linear2:
                // 查询
                if (search_F == null) {
                    search_F = new Search_F();
                    // 判断当前界面是否隐藏，如果隐藏就进行添加显示，false表示显示，true表示当前界面隐藏
                    if (!search_F.isHidden()) {
                        addFragment(search_F);
                        showFragment(search_F);
                    }
                } else {
                    if (search_F.isHidden()) {
                        showFragment(search_F);
                    }
                }
                break;
            case R.id.linear3:
                if (null == MyAppliaction.userRight || !MyAppliaction.userRight.contains("生产主管")) {
                    ToastUtils.showToast(this, "您没有对应权限");
                    return;
                }
                //计划
                if (plan_F == null) {
                    plan_F = new Plan_F();
                    // 判断当前界面是否隐藏，如果隐藏就进行添加显示，false表示显示，true表示当前界面隐藏
                    if (!plan_F.isHidden()) {
                        addFragment(plan_F);
                        showFragment(plan_F);
                    }
                } else {
                    if (plan_F.isHidden()) {
                        showFragment(plan_F);
                    }
                }
                break;
            case R.id.linear4:
                // 个人中心
                if (user_F == null) {
                    user_F = new User_F();
                    // 判断当前界面是否隐藏，如果隐藏就进行添加显示，false表示显示，true表示当前界面隐藏
                    if (!user_F.isHidden()) {
                        addFragment(user_F);
                        showFragment(user_F);
                    }
                } else {
                    if (user_F.isHidden()) {
                        showFragment(user_F);
                    }
                }
                break;
        }
        // 设置按钮的选中和未选中资源
        for (int i = 0; i < bt_menu.length; i++) {
            bt_menu[i].setImageResource(select_off[i]);
            if (view.getId() == linear_id[i]) {
                bt_menu[i].setImageResource(select_on[i]);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出应用",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            MyAppliaction.exit();
        }
    }

    /**
     * 添加Fragment
     **/
    public void addFragment(Fragment fragment) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame, fragment);
        ft.commit();
    }

    /**
     * 显示Fragment
     **/
    public void showFragment(Fragment fragment) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        // 设置Fragment的切换动画
        //        ft.setCustomAnimations(R.anim.cu_push_right_in, R.anim.cu_push_left_out);

        // 判断页面是否已经创建，如果已经创建，那么就隐藏掉
        if (home_F != null) {
            ft.hide(home_F);
        }
        if (check_F != null) {
            ft.hide(check_F);
        }
        if (search_F != null) {
            ft.hide(search_F);
        }
        if (plan_F != null) {
            ft.hide(plan_F);
        }
        if (user_F != null) {
            ft.hide(user_F);
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }
}
