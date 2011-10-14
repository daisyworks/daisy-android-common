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

public class SameClassActionComparator implements ActionComparator
{
  public static final SameClassActionComparator INSTANCE = new SameClassActionComparator();

  @Override
  public boolean isRedundant (final BluetoothAction newAction, final BluetoothAction oldAction)
  {
    return newAction.getClass() == oldAction.getClass();
  }
}
