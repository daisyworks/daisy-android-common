package com.daisyworks.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractArrayAdapter<T> extends BaseAdapter
{
  private final Context context;
  private final T[] items;
  private int listViewId;

  public AbstractArrayAdapter(final Context context, final T[] items, final int listViewId)
  {
    this.context = context;
    this.items = items;
    this.listViewId = listViewId;
  }

  @Override
  public int getCount()
  {
    return items.length;
  }

  @Override
  public T getItem(final int position)
  {
    return items[position];
  }

  @Override
  public long getItemId(final int position)
  {
    return position;
  }

  @Override
  public int getItemViewType(final int position)
  {
    return 0;
  }

  @Override
  public int getViewTypeCount()
  {
    return 1;
  }

  @Override
  public View getView(final int position, final View convertView, final ViewGroup parent)
  {
    View view = convertView;
    if (view == null)
    {
      final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(listViewId, null);
    }

    final T item = items[position];
    final View result = getView(item, view, parent);
    return result;
  }

  protected abstract View getView(final T item, final View view, final ViewGroup parent);
}