package tsingtaopad.et.dslvdemo;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.List;

/**
 * Created by yuyidong on 16/1/23.
 */
public class SDListViewActivity extends AppCompatActivity implements
        AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener,
        SlideAndDragListView.OnDragDropListener,
        SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener {
    private static final String TAG = SDListViewActivity.class.getSimpleName();

    private Menu mMenu;
    private List<ApplicationInfo> mAppList;
    private SlideAndDragListView mListView;
    private Toast mToast;
    private ApplicationInfo mDraggedEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdlv);
        initData();
        initMenu();
        initUiAndListener();
        mToast = Toast.makeText(SDListViewActivity.this, "", Toast.LENGTH_SHORT);
    }

    public void initData() {
        mAppList = getPackageManager().getInstalledApplications(0);
    }

    public void initMenu() {
        // 第一个参数表示条目滑动时,是否能滑的过头   true表示过头    false 表示不过头
        mMenu = new Menu(true);
        /*mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(Utils.getDrawable(this, R.drawable.btn_left0))
                .setText("One")
                .setTextColor(Color.GRAY)
                .setTextSize(14)
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setBackground(Utils.getDrawable(this, R.drawable.btn_left1))
                .setText("Two")
                .setTextColor(Color.BLACK)
                .setTextSize((14))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setBackground(Utils.getDrawable(this, R.drawable.btn_left0))
                .setText("新")
                .setTextColor(Color.BLACK)
                .setTextSize((14))
                .build());*/
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) + 30)
                .setBackground(Utils.getDrawable(this, R.drawable.btn_right0))
                .setText("Three")
                .setDirection(MenuItem.DIRECTION_RIGHT) // 右滑出现
                .setTextColor(Color.BLACK)
                .setTextSize(14)
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(Utils.getDrawable(this, R.drawable.btn_right1))
                .setDirection(MenuItem.DIRECTION_RIGHT) // 右滑出现
                .setIcon(getResources().getDrawable(R.drawable.ic_launcher))
                .build());
    }

    public void initUiAndListener() {
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit);
        mListView.setMenu(mMenu);
        mListView.setAdapter(mAdapter);

        mListView.setOnDragDropListener(this);// 拖动
        mListView.setOnItemClickListener(this);// 单击监听
        mListView.setOnSlideListener(this); // Item 滑动监听器   左右滑动
        mListView.setOnMenuItemClickListener(this);// 实现 menu item 的单击事件
        mListView.setOnItemLongClickListener(this); // 长按监听
    }

    private BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mAppList.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(SDListViewActivity.this).inflate(R.layout.item_custom_btn, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                cvh.btnClick = (Button) convertView.findViewById(R.id.btn_item_click);
                cvh.btnClick.setOnClickListener(mOnClickListener);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            ApplicationInfo item = (ApplicationInfo) this.getItem(position);
            cvh.txtName.setText(item.loadLabel(getPackageManager()));
            cvh.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
            cvh.btnClick.setText(position + "");
            cvh.btnClick.setTag(position);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(300);
            convertView.startAnimation(alphaAnimation);
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            public Button btnClick;
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o instanceof Integer) {
                    mToast.setText("按钮点击 --> " + ((Integer) o));
                    mToast.show();
                }
            }
        };
    };

    // 拖动监听
    @Override
    public void onDragViewStart(int beginPosition) {// 参数 position 表示的是刚开始拖动的时候取的 item 在 ListView 中的位置
        mDraggedEntity = mAppList.get(beginPosition);
        toast("开始拖动时的位置 ---> " + beginPosition);
    }

    // 拖动监听
    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {// 参数 fromPosition 和 toPosition 表示从哪个位置拖动到哪个位置
        ApplicationInfo applicationInfo = mAppList.remove(fromPosition);
        mAppList.add(toPosition, applicationInfo);
        toast("从哪个位置 ---> " + fromPosition + "  拖动到哪个位置 --> " + toPosition);
    }

    // 拖动监听
    @Override
    public void onDragViewDown(int finalPosition) {  // 参数 position 表示的是拖动的 item 最放到了 ListView 的哪个位置
        mAppList.set(finalPosition, mDraggedEntity);
        toast("最放到了的哪个位置 ---> " + finalPosition);
    }

    // Item 滑动监听器
    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        toast("onSlideOpen   position--->" + position + "  direction--->" + direction);
    }

    // Item 滑动监听器
    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
        toast("onSlideClose   position--->" + position + "  direction--->" + direction);
    }

    // 实现 menu item 的单击事件
    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        toast("onMenuItemClick   itemPosition--->" + itemPosition + "  buttonPosition-->" + buttonPosition + "  direction-->" + direction);
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_NOTHING;  // 点击无反应
                    case 1:
                        return Menu.ITEM_SCROLL_BACK; // 收回
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_SCROLL_BACK; // 收回
                    case 1:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP; // 置顶
                }
        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        toast("长按监听 position--->" + position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        toast("单击监听 position--->" + position);
    }

    private void toast(String toast) {
        mToast.setText(toast);
        mToast.show();
    }
}
