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
import java.io.OutputStream;

import android.os.Handler;
import android.util.Log;

public abstract class BaseBluetoothAction implements BluetoothAction
{
	  
  private OutputStream out;

  @Override
  public void performAction(final Handler handler,
                            final AsyncReader reader,
                            final OutputStream outputStream)
    throws IOException
  {
    this.out = outputStream;
    performIOAction(reader, handler);
  }

  protected void writeln(final String msg) throws IOException
  {
    write(msg + "\r\n");
  }

  protected void write(final String msg) throws IOException
  {
    out.write(msg.getBytes());
    out.flush();
    if (BTCommThread.DEBUG_BLUETOOTH) Log.i(BTCommThread.LOG_TAG, "BlueTooth wrote: " + msg);
  }

  @Override
  public String getRedundancyTag ()
  {
    return String.valueOf(hashCode());
  }

  protected abstract void performIOAction(final AsyncReader reader,
                                          final Handler handler)
    throws IOException;

}