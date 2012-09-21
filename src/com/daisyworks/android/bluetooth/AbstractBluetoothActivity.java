/*
    This file is part of the DaisyWorks Android Library.

    The DaisyWorks Android Library is free software: you can redistribute
    it and/or modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation, either version 3
    of the License, or (at your option) any later version.

    The DaisyWorks Android Library is distributed in the hope that it
    will be useful, but WITHOUT ANY WARRANTY; without even the implied
    warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with the DaisyWorks Android Library.
    If not, see <http://www.gnu.org/licenses/>.

    Copyright 2011 DaisyWorks, Inc
*/
package com.daisyworks.android.bluetooth;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daisyworks.android.DialogUtil;

public class AbstractBluetoothActivity extends Activity
{
  private final static int BLUETOOTH_NOT_AVAILABLE = 1;
  private final static int BLUETOOTH_NOT_ENABLED = 2;
  private final static int BLUETOOTH_UNEXPECTED_ERROR = 3;

  private final int statusBarProgress;
  private final int statusBarText;

  public AbstractBluetoothActivity(final int statusBarProgress, final int statusBarText)
  {
    this.statusBarProgress = statusBarProgress;
    this.statusBarText = statusBarText;
  }

  @Override
  protected void onCreate (final Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    if (adapter == null)
    {
      showDialog(BLUETOOTH_NOT_AVAILABLE);
      return;
    }

    if (!adapter.isEnabled())
    {
      final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, 1);
      return;
    }

    bluetoothEnabled();
  }

  @Override
  protected void onActivityResult (final int requestCode, final int resultCode, final Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK)
    {
      final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

      if (adapter != null && adapter.isEnabled())
      {
        bluetoothEnabled();
      }
      else
      {
        showDialog(BLUETOOTH_UNEXPECTED_ERROR);
      }
    }
    else
    {
      showDialog(BLUETOOTH_NOT_ENABLED);
    }
  }

  protected void bluetoothEnabled()
  {
    // allow subclasses to handle what happens once BT has been turned on
  }

  protected void spinProgressBar(final boolean spin)
  {
    final ProgressBar progressBar = (ProgressBar)findViewById(statusBarProgress);
    progressBar.setVisibility(spin ? View.VISIBLE : View.INVISIBLE);
  }

  protected void setStatus(final int statusId)
  {
    final TextView textView = (TextView)findViewById(statusBarText);
    textView.setText(statusId);
  }

  @Override
  protected Dialog onCreateDialog (final int id)
  {
    switch(id) {
      case BLUETOOTH_NOT_AVAILABLE:
        return DialogUtil.okAndFinishDialog(this, R.string.bluetooth_unavailable);
      case BLUETOOTH_NOT_ENABLED:
        return DialogUtil.okAndFinishDialog(this, R.string.bluetooth_not_enabled);
      case BLUETOOTH_UNEXPECTED_ERROR:
        return DialogUtil.okAndFinishDialog(this, R.string.bluetooth_unexpected_error);
    }
    return super.onCreateDialog(id);
  }

  public BTCommThread getBtCommThreadforNewActivity(final Handler handler,
                                                    final String deviceId,
                                                    final long timeout,
                                                    final BluetoothAction[] initialActions)
  {
    return ((BluetoothApplication)getApplication()).getBtCommThreadforNewActivity(handler, deviceId, timeout, initialActions);
  }

  protected class BaseCommHandler extends Handler
  {
    public BaseCommHandler()
    {
      // default constructor
    }

    @Override
    public void handleMessage (final Message msg)
    {
      switch(msg.what) {
        case BTCommThread.BLUETOOTH_START_CONNECT:
          setStatus(R.string.bluetooth_connecting);
          spinProgressBar(true);
          break;

        case BTCommThread.BLUETOOTH_CONNECTED:
          setStatus(R.string.bluetooth_connected);
          spinProgressBar(false);
          break;

        case BTCommThread.BLUETOOTH_CONNECTION_ERROR:
          setStatus(R.string.bluetooth_connection_error);
          spinProgressBar(false);
          Toast.makeText(AbstractBluetoothActivity.this, R.string.bluetooth_connection_error_toast, Toast.LENGTH_SHORT);
          break;

        case BTCommThread.BLUETOOTH_CONNECTION_CLOSED:
          setStatus(R.string.bluetooth_not_connected);
          spinProgressBar(false);
          break;
      }
    }
  }
}
