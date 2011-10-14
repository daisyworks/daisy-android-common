package com.daisyworks.android;

public class ThreadUtil
{
  public static void waitUntil(final long time) throws InterruptedException
  {
    long currentTime = System.currentTimeMillis();
    while (currentTime < time)
    {
      synchronized(ThreadUtil.class)
      {
        ThreadUtil.class.wait(time - currentTime);
      }
      currentTime = System.currentTimeMillis();
    }
  }
}
