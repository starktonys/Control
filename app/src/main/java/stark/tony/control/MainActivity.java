package stark.tony.control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private RelativeLayout mActivityMain;
  private Button mBtnStartSearch;
  private ImageView mIvBack;
  private ImageView mIvHasConnect;
  private ListView mLvHasConnect;
  private ImageView mIvCanConnect;
  private ListView mLvCanConnect;

  private BluetoothAdapter mBluetoothAdapter;
  private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
  private DeviceAdapter mAdapter;

  private Button btnEndSearch;

  public static final String EXTRA_DEVICE = "DEVICE";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
    initBluetooth();
    initEvent();
    setAttribute();
  }

  private void initView() {
    mActivityMain = (RelativeLayout) findViewById(R.id.activity_main);
    mBtnStartSearch = (Button) findViewById(R.id.btn_start_search);
    mIvBack = (ImageView) findViewById(R.id.iv_back);
    mIvHasConnect = (ImageView) findViewById(R.id.iv_has_connect);
    mLvHasConnect = (ListView) findViewById(R.id.lv_has_connect);
    mIvCanConnect = (ImageView) findViewById(R.id.iv_can_connect);
    mLvCanConnect = (ListView) findViewById(R.id.lv_can_connect);
    btnEndSearch = (Button) findViewById(R.id.btn_end_search);
  }

  private void setAttribute() {
    mAdapter = new DeviceAdapter(getApplicationContext(), mDevices);
    mLvCanConnect.setAdapter(mAdapter);
    mLvCanConnect.setOnItemClickListener(this);

    // 注册广播接收者, 当扫描到蓝牙设备的时候, 系统会发送广播
    IntentFilter filter = new IntentFilter();
    filter.addAction(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    registerReceiver(mBluetoothReceiver, filter);
  }

  private void initEvent() {
    mBtnStartSearch = (Button) findViewById(R.id.btn_start_search);
    mBtnStartSearch.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        // 开始扫描
        mDevices.clear();
        mAdapter.notifyDataSetChanged();
        mBluetoothAdapter.startDiscovery();
      }
    });

    btnEndSearch.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        // 停止扫描
        mBluetoothAdapter.cancelDiscovery();
      }
    });

    mIvBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });
  }

  private void initBluetooth() {
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter != null) {
      // 蓝牙是否可用
      if (!mBluetoothAdapter.isEnabled()) {
        // 打开蓝牙
        mBluetoothAdapter.enable();
      }
    }
  }

  private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        // 扫描到蓝牙设备
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mDevices.add(device);
        mAdapter.notifyDataSetChanged();
        Log.e("device", "onReceive: " + device.getName());
      } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
        Toast.makeText(getApplicationContext(), "开始扫描", Toast.LENGTH_SHORT).show();
      } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        Toast.makeText(getApplicationContext(), "扫描结束", Toast.LENGTH_SHORT).show();
      }
    }
  };

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    BluetoothDevice device = mDevices.get(position);
    Toast.makeText(MainActivity.this, "您选择了" + device.getName(), Toast.LENGTH_SHORT)
        .show();
    Intent intent = new Intent();
    intent.putExtra(EXTRA_DEVICE, device);
    setResult(RESULT_OK, intent);
    finish();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mBluetoothReceiver);
  }
}
