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

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

public class BluetoothApplication extends Application
{
  private Map<String, BTCommThread> devices = new HashMap<String, BTCommThread>();

  @Override
  public void onCreate ()
  {
    super.onCreate();
  }

  @Override
  public void onLowMemory ()
  {
    super.onLowMemory();
  }
  
  public Map<String, BTCommThread> getDevices() {
	  return devices;
  }

  public BTCommThread getBtCommThreadforNewActivity(final Handler handler,
                                                    final String deviceId,
                                                    final long timeout,
                                                    final BluetoothAction[] initialActions)
  {
	BTCommThread btCommThread = devices.get(deviceId);
    if (btCommThread == null || btCommThread.isShutdown())
    {
      btCommThread = new BTCommThread(handler, deviceId, timeout, initialActions);
      btCommThread.start();
      devices.put(deviceId, btCommThread);
    }
    else
    {
      btCommThread.newActivity(handler, timeout);
    }
    
    if (BTCommThread.DEBUG_BLUETOOTH) Log.d("BluetoothApplication", "new deviceId: " + deviceId + ", existing " + btCommThread.deviceAddress);

    return btCommThread;
  }

  @Override
  public void onTerminate ()
  {
    super.onTerminate();
    
    for (BTCommThread t : devices.values()) {
    	t.shutdown();
    }
  }
}
