package stark.tony.control;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
  private ArrayList<BluetoothDevice> mDevices;
  private Context mContext;

  public DeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
    mDevices = devices;
    mContext = context;
  }

  @Override public int getCount() {
    return mDevices.size();
  }

  @Override public Object getItem(int position) {
    return position;
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(mContext, R.layout.item, null);
      holder = new ViewHolder();
      holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    BluetoothDevice device = mDevices.get(position);
    holder.mTvName.setText(device.getName());
    return convertView;
  }

  class ViewHolder {
    TextView mTvName;
  }
}
