package com.daisyworks.android.widget;

import android.text.Editable;
import android.text.TextWatcher;

public class AbstractTextWatcher implements TextWatcher
{
  @Override
  public void afterTextChanged(final Editable s)
  {
    // does nothing
  }

  @Override
  public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after)
  {
    // does nothing
  }

  @Override
  public void onTextChanged(final CharSequence s, final int start, final int before, final int count)
  {
    // does nothing
  }
}
