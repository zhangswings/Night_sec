package com.example.swings.night_sec;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用菜单页面（主页面）
 * 1.快速入库
 * 2.快速出库
 * 3.产品盘点
 * 4.信息查询
 * 5.产品退库
 * 6.系统设置
 * 7.修改重量
 */
public class MainActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    private GridView mGridview;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mGridview = (GridView) findViewById(R.id.gridview_menu);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("康华纸业:" + getIntent().getStringExtra("user"));
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.btn_ok,
                R.string.btn_cancle);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(mNavigationView);
// 准备要添加的数据条目
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        Integer[] imgs = {R.mipmap.icon_ru, R.mipmap.icon_chu, R.mipmap.icon_pan, R.mipmap.icon_info, R.mipmap.tuiku, R.mipmap.icon_ru,R.mipmap.setting};
        String names[] = {"快速入库", "快速出库", "产品盘点", "信息查询", "产品退库","修改重量", "系统设置"};
        for (int i = 1; i <= names.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("imageItem", imgs[i - 1]);// 添加图像资源的ID
            item.put("textItem", names[i - 1]);// 按序号添加ItemText
            items.add(item);
        }
        adapter = new SimpleAdapter(this, items, R.layout.activity_card, new String[]{"imageItem", "textItem",},
                new int[]{R.id.card_bg, R.id.card_title});
        mGridview.setAdapter(adapter);
        /**
         * 菜单选项
         */
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = null;
                switch (arg2) {
                    case 0:
                        intent = new Intent(MainActivity.this, ActivityRu_before.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, ActivityOut.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, ActivityPan.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, ActivityInfo.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, Tuiku.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(MainActivity.this, ActivityChangeWeight.class);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        //profile Image
        setUpProfileImage();
//        switchToIndex();
//        new AsyncTask<String, Void, String>() {
//
//
//            @Override
//            protected String doInBackground(String... params) {
//                OkHttpClient client = new OkHttpClient();
//                client.setConnectTimeout(30, TimeUnit.SECONDS);
//
//                RequestBody formBody = new FormEncodingBuilder().add("count", params
//                        [1]).build();
//                Request request = new Request.Builder().url(params[0]).post(formBody).build();
//                try {
//                    Response response = client.newCall(request).execute();
//                    return response.body().string();
//                } catch (IOException e) {
//                    Log.e("IOException", e.toString());
//                }
////                    Request request = new Request.Builder()
////                            .url(params[0])
////                            .build();
////                    try
////                    {
////                    Response response = client.newCall(request).execute();
////                    return response.body().string();}
////                    catch (IOException e){
////                        System.out.println(e.toString());
////                    }
//                return null;
//
//            }

//            @Override
//            protected void onPostExecute(String text) {
//                try {
//                    Document document = DocumentHelper.parseText(text);
//                    Element root = document.getRootElement();
//                    String info = root.getText();
//                    Gson gson = new Gson();
//                    Type type = new TypeToken<List<Material>>() {
//                    }.getType();
//                    List<Material> materials = gson.fromJson(info, type);
//                    StringBuilder builder = new StringBuilder();
//                    for (Material material : materials
//                            ) {
//                        builder.append(material.getMaterialName() + "\n");
//                    }
//                } catch (DocumentException e) {
//                    Log.e("IOException", e.toString());
//                }
//
//            }
//        }.execute("http://192.168.0.187:8091/Service1.asmx/GetLowerList", "80");
    }

    /**
     * 加载左侧菜单圆形图片
     */
    private void setUpProfileImage() {
        findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                switchToBlog();
                mDrawerLayout.closeDrawers();
                mNavigationView.getMenu().getItem(1).setChecked(true);
            }
        });
    }


    /**
     * 侧边菜单选项
     *
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent = null;
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_item_index:
//                                intent = new Intent(MainActivity.this, ActivityRu.class);
//                                startActivity(intent);
                                break;
                            case R.id.navigation_item_book:
                                intent = new Intent(MainActivity.this, ActivityPan.class);
                                startActivity(intent);
                                break;
                            case R.id.navigation_item_example:
                                intent = new Intent(MainActivity.this, ActivityRu_before.class);
                                startActivity(intent);
                                break;
                            case R.id.navigation_item_blog:
                                intent = new Intent(MainActivity.this, ActivityOut.class);
                                startActivity(intent);
                                break;
                            case R.id.navigation_item_about:
                                intent = new Intent(MainActivity.this, ActivityInfo.class);
                                startActivity(intent);
                                break;
                            case R.id.navigation_item_tuiku:
                                intent = new Intent(MainActivity.this, Tuiku.class);
                                startActivity(intent);
                                break;

                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    /**
     * 查看可用的Fragment
     *
     * @return
     */
    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private BackHandledFragment selectedFragment;
    private NavigationView mNavigationView;


    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.selectedFragment = backHandledFragment;
    }


    private long exitTime = 0;

    /**
     * 退出程序
     */
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    /**
     * 重写返回Back按键
     */
    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                doExitApp();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
