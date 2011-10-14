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
package com.daisyworks.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.daisyworks.android.bluetooth.R;

public class DialogUtil
{
  public static final Dialog okAndFinishDialog(final Activity activity, final int message)
  {
    return okDialog(activity, message,
                    new DialogInterface.OnClickListener()
                    {
                      @Override
                      public void onClick (final DialogInterface dialog, final int which)
                      {
                        activity.finish();
                      }
                    });
  }

  public static final Dialog okDialog(final Context context, final int message, final DialogInterface.OnClickListener action)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(message)
           .setCancelable(false)
           .setPositiveButton(R.string.ok, action);
    return builder.create();
  }

  public static final Dialog yesNoDialog(final Context context,
                                         final int message,
                                         final DialogInterface.OnClickListener yesAction)
  {
    return yesNoDialog(context, message, yesAction, null);
  }

  public static final Dialog yesNoDialog(final Context context,
                                         final String message,
                                         final DialogInterface.OnClickListener yesAction)
  {
    return yesNoDialog(context, message, yesAction, null);
  }

  public static final Dialog yesNoDialog(final Context context,
                                         final int message,
                                         final DialogInterface.OnClickListener yesAction,
                                         final DialogInterface.OnClickListener noAction)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(message)
           .setCancelable(false)
           .setPositiveButton(R.string.yes, yesAction)
           .setNegativeButton(R.string.no, noAction);
    return builder.create();
  }

  public static final Dialog yesNoDialog(final Context context,
                                         final String message,
                                         final DialogInterface.OnClickListener yesAction,
                                         final DialogInterface.OnClickListener noAction)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(message)
           .setCancelable(false)
           .setPositiveButton(R.string.yes, yesAction)
           .setNegativeButton(R.string.no, noAction);
    return builder.create();
  }
}
