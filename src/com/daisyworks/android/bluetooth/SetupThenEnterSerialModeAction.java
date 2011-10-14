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

import java.io.IOException;

import android.os.Handler;
import android.util.Log;

public class SetupThenEnterSerialModeAction extends BaseBluetoothAction
{
  @Override
  protected void performIOAction (final AsyncReader reader,
                                  final Handler handler)
    throws IOException
  {
    if (BTCommThread.DEBUG_BLUETOOTH) Log.i(BTCommThread.LOG_TAG, "BlueTooth initiating communication");

    write("$$$");
    String result = reader.readLine(1000);

    if (!"CMD\r\n".equalsIgnoreCase(result))
    {
      handler.obtainMessage(BTCommThread.BLUETOOTH_CONNECTION_ERROR).sendToTarget();
      return;
    }

    write("ST,255\n");
    result = reader.readLine(1000);

    if (!"AOK\r\n".equalsIgnoreCase(result))
    {
      handler.obtainMessage(BTCommThread.BLUETOOTH_CONNECTION_ERROR).sendToTarget();
      return;
    }

    write("S~,0\n");
    result = reader.readLine(1000);

    if (!"AOK\r\n".equalsIgnoreCase(result))
    {
      handler.obtainMessage(BTCommThread.BLUETOOTH_CONNECTION_ERROR).sendToTarget();
      return;
    }

    // Set baudrate to 57600
    write("SU,57.6\n");
    result = reader.readLine(1000);

    if (!"AOK\r\n".equalsIgnoreCase(result))
    {
      handler.obtainMessage(BTCommThread.BLUETOOTH_CONNECTION_ERROR).sendToTarget();
      return;
    }

    write("---\n");
    reader.readLine(1000);
  }
}