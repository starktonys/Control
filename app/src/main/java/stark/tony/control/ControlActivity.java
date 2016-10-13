package stark.tony.control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kyleduo.switchbutton.SwitchButton;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

  private static final String TAG = "Control";
  private RelativeLayout mActivityMain;
  private ImageView mIvOperation;
  private TextView mTvControl;
  private Button mBtnAuto;
  private SwitchButton mSbtnControl1;
  private SwitchButton mSbtnControl2;
  private SwitchButton mSbtnControl3;
  private SwitchButton mSbtnControl4;
  private boolean isOpen;
  private BluetoothAdapter mBluetoothAdapter;
  private static final int REQUEST_CODE_DEVICE = 800;
  private OutputStream mOutputStream;
  private boolean isAuto = true;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_control);
    initView();
    setAttribute();
    initBluetooth();
  }

  private void initBluetooth() {
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  }

  private void setAttribute() {
    mIvOperation.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (isOpen) {
          mIvOperation.setBackgroundResource(R.drawable.icon_close);
          isOpen = false;

          if (mBluetoothAdapter != null) {
            // 蓝牙是否可用
            if (mBluetoothAdapter.isEnabled()) {
              // 关闭蓝牙
              mBluetoothAdapter.disable();
            }
          }
        } else {
          mIvOperation.setBackgroundResource(R.drawable.icon_open);
          isOpen = true;

          if (mBluetoothAdapter != null) {
            // 蓝牙是否可用
            if (!mBluetoothAdapter.isEnabled()) {
              // 打开蓝牙
              mBluetoothAdapter.enable();

              //打开跳出到选择页面中
              Intent intent = new Intent(ControlActivity.this, MainActivity.class);
              startActivityForResult(intent, REQUEST_CODE_DEVICE);
            }
          }
        }
      }
    });

    mBtnAuto.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (isAuto) {
          if (mOutputStream != null) {
            new Thread() {
              @Override public void run() {
                try {
                  mOutputStream.write("fa0602".getBytes("utf-8"));
                } catch (IOException e) {
                  e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                  @Override public void run() {
                    mBtnAuto.setText("手动模式");
                  }
                });
              }
            }.start();
          } else {
            Toast.makeText(ControlActivity.this, "请选择设备", Toast.LENGTH_SHORT).show();
            return;
          }
        } else {
          if (mOutputStream != null) {
            new Thread() {
              @Override public void run() {
                try {
                  mOutputStream.write("fa0601".getBytes("utf-8"));
                } catch (IOException e) {
                  e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                  @Override public void run() {
                    mBtnAuto.setText("自动模式");
                  }
                });
              }
            }.start();
          } else {
            Toast.makeText(ControlActivity.this, "请选择设备", Toast.LENGTH_SHORT).show();
            return;
          }
        }
      }
    });

    mSbtnControl1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          //打开指令
          sendCtrl(1, b);
        } else {
          //关闭指令
          sendCtrl(1, b);
        }
      }
    });

    mSbtnControl2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          //打开指令
          sendCtrl(2, b);
        } else {
          //关闭指令
          sendCtrl(2, b);
        }
      }
    });

    mSbtnControl3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          //打开指令
          sendCtrl(3, b);
        } else {
          //关闭指令
          sendCtrl(3, b);
        }
      }
    });

    mSbtnControl4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          //打开指令
          sendCtrl(4, b);
        } else {
          //关闭指令
          sendCtrl(4, b);
        }
      }
    });
  }

  private void initView() {
    mActivityMain = (RelativeLayout) findViewById(R.id.activity_main);
    mIvOperation = (ImageView) findViewById(R.id.iv_operation);
    mTvControl = (TextView) findViewById(R.id.tv_control);
    mBtnAuto = (Button) findViewById(R.id.btn_auto);
    mSbtnControl1 = (SwitchButton) findViewById(R.id.sbtn_control1);
    mSbtnControl2 = (SwitchButton) findViewById(R.id.sbtn_control2);
    mSbtnControl3 = (SwitchButton) findViewById(R.id.sbtn_control3);
    mSbtnControl4 = (SwitchButton) findViewById(R.id.sbtn_control4);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (resultCode) {
      case RESULT_OK:
        if (requestCode == REQUEST_CODE_DEVICE) {
          BluetoothDevice device = data.getParcelableExtra(MainActivity.EXTRA_DEVICE);
          Log.e(TAG, "onActivityResult: " + device.getName());
          conn(device);
        }
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void conn(final BluetoothDevice device) {
    // 建立蓝牙连接是耗时操作, 类似TCP Socket, 需要放在子线程里
    new Thread() {
      public void run() {
        try {
          // 获取 BluetoothSocket, UUID需要和蓝牙服务端保持一致
          BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(
              UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
          // 和蓝牙服务端建立连接
          bluetoothSocket.connect();
          // 获取输出流, 往蓝牙服务端写指令信息
          mOutputStream = bluetoothSocket.getOutputStream();

          //发一个默认自动的指令
          mOutputStream.write("fa0601".getBytes("utf-8"));

          // 提示用户
          runOnUiThread(new Runnable() {
            public void run() {
              Log.e(TAG, "run: 连接成功----");
              Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
            }
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      ;
    }.start();
  }

  private void sendCtrl(int i, boolean isOpen) {

    try {
      byte[] bs = new byte[2];

      if (i == 1) {
        bs[0] = (byte) 0xaa;
      } else if (i == 2) {
        bs[0] = (byte) 0xbb;
      } else if (i == 3) {
        bs[0] = (byte) 0xcc;
      } else if (i == 4) {
        bs[0] = (byte) 0xdd;
      }

      //是否开关
      if (isOpen) {
        bs[1] = (byte) 0x01;
      } else {
        bs[1] = (byte) 0x02;
      }

      mOutputStream.write(bs);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
