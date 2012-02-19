package com.daisyworks.android.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ListEntry implements Comparable<ListEntry>
{
  public String id;
  public String value;

  public ListEntry (final String id, final String value)
  {
    this.id = id;
    this.value = value;
  }

  @Override
  public String toString()
  {
    return value;
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final ListEntry another)
  {
    if  (value == another.value)
    {
      return 0;
    }
    if (value == null)
    {
      return -1;
    }
    if (another.value == null)
    {
      return 1;
    }
    return value.compareTo(another.value);
  }

  public static ArrayAdapter<ListEntry> fromResources(final Context context,
                                                      final int idsResourceId,
                                                      final int valuesResourceId,
                                                      final int textViewResourceId,
                                                      final boolean sort)
  {
    ArrayAdapter<ListEntry> adapter = new ArrayAdapter<ListEntry>(context, textViewResourceId);

    final String[] ids = context.getResources().getStringArray(idsResourceId);
    final String[] values = context.getResources().getStringArray(valuesResourceId);

    ListEntry[] entries = new ListEntry[ids.length];

    for (int i = 0; i < ids.length; i++)
    {
      entries[i] = new ListEntry(ids[i], values[i]);
    }

    if (sort)
    {
      Arrays.sort(entries);
    }

    for (final ListEntry entry : entries)
    {
      adapter.add(entry);
    }

    return adapter;
  }

  public static ArrayAdapter<ListEntry> fromList(final Context context,
                                                 final List<ListEntry> entries,
                                                 final int textViewResourceId,
                                                 final boolean sort)
  {
    ArrayAdapter<ListEntry> adapter = new ArrayAdapter<ListEntry>(context, textViewResourceId);

    if (sort)
    {
      Collections.sort(entries);
    }

    for (final ListEntry entry : entries)
    {
      adapter.add(entry);
    }

    return adapter;
  }
}
