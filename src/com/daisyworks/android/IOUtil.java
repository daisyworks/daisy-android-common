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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IOUtil
{
  public static final String inputToString(final InputStream in) throws IOException
  {
    final char[] buf = new char[1024];
    final StringBuilder stringBuf = new StringBuilder();

    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    int read;
    while (-1 != (read = reader.read(buf)))
    {
      stringBuf.append(buf, 0, read);
    }
    return stringBuf.toString();
  }

  public static void closeQuietly(final InputStream in)
  {
    if (in == null)
    {
      return;
    }

    try
    {
      in.close();
    }
    catch(final IOException ioe)
    {
      // ignore, nothing to do
    }
  }

  public static void pipe(final InputStream in, final OutputStream out) throws IOException
  {
    final byte[] buf = new byte[1024];
    int bytesRead = 0;
    while (-1 != (bytesRead = in.read(buf)))
    {
      out.write(buf, 0, bytesRead);
    }
  }

  public static byte[] inputToByteArray(final InputStream in, final int estimatedSize) throws IOException
  {
    final ByteArrayOutputStream out = new ByteArrayOutputStream(estimatedSize);
    pipe(in, out);
    return out.toByteArray();
  }
}
