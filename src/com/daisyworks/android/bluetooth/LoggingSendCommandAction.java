package com.daisyworks.android.bluetooth;

import java.io.IOException;

import android.os.Handler;


public class LoggingSendCommandAction extends TaggedSendCommandAction
{
  protected static Thread task = null;

  public LoggingSendCommandAction (final String cmd, final String tag)
  {
    super(cmd, tag);
  }

  @Override
  protected void performIOAction (final AsyncReader reader, final Handler handler) throws IOException
  {
    super.performIOAction(reader, handler);
    startAsynctask(reader);
  }

  public synchronized void startAsynctask(final AsyncReader reader)
  {
    if (task != null)
    {
      return;
    }

    task = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          while (null != reader.readLine(10000))
          {
            // do nothing
          }
        }
        catch(Exception e)
        {
          // ignore
        }
        finally
        {
          synchronized(LoggingSendCommandAction.class)
          {
            task = null;
          }
        }
      }
    };
    task.start();
  }
}
