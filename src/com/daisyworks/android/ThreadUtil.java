package com.daisyworks.android;

public class ThreadUtil
{
  public static void waitUntil(final long time)
  {
    long current;
    while ((current = System.currentTimeMillis()) < time)
    try
    {
      Thread.sleep(time - current);
    }
    catch(final InterruptedException ie)
    {
      Thread.currentThread().interrupt();
    }
  }
}
