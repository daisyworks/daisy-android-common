package com.daisyworks.android.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractListAdapter<T> extends BaseAdapter
{
  private final Context context;
  private final List<T> items;
  private int listViewId;

  public AbstractListAdapter(final Context context, final List<T> items, final int listViewId)
  {
    this.context = context;
    this.items = items;
    this.listViewId = listViewId;
  }

  @Override
  public int getCount()
  {
    return items.size();
  }

  @Override
  public T getItem(final int position)
  {
    return items.get(position);
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

    final T item = items.get(position);
    final View result = getView(item, view, parent);
    return result;
  }

  protected abstract View getView(final T item, final View view, final ViewGroup parent);
}