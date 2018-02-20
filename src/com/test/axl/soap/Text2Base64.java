package com.test.axl.soap;

/**
 * The <code>Text2Base64</code> class converts a standard ASCII text string into a Base64-encoded string.
 * Base64 encoding is used for many purposes, especially by HTTP. One common use is encoding usernames and
 * passwords for HTTP Basic Authentication.
 *
 * @author  kstearns
 * @version 1.0  (March 2002)
 */
public class Text2Base64
{
  /**
  * This method accepts an ASCII text string and returns the Base64-encoded version of that string.
  * This method is static which allows the encoding to be performed without instantiating a <code>Text2Base64</code> object.
  * @param text The ASCII text string to be Base64 encoded
  * @return The Base64-encoded string
  */
  public static String getBase64(String text)
  {
    int i = 0;
    int j = 0;

    String base64key = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    StringBuffer base64string = new StringBuffer();
    char text0, text1, text2;

    for (i = 0; i < text.length();)
    {
      text0 = text.charAt(i);

      base64string.setLength(j+4);
      base64string.setCharAt(j, base64key.charAt((text0 & 252) >> 2));
      if ((i+1)<text.length())
      {
        text1 = text.charAt(i+1);
        base64string.setCharAt(j+1, base64key.charAt(((text0 & 3) << 4) | ((text1 & 240) >> 4)));
        if ((i+2)<text.length())
        {
          text2 = text.charAt(i+2);
          base64string.setCharAt(j+2, base64key.charAt(((text1 & 15) << 2) | ((text2 & 192) >> 6)));
          base64string.setCharAt(j+3, base64key.charAt((text2 & 63)));
        }
        else
        {
          base64string.setCharAt(j+2, base64key.charAt(((text1 & 15) << 2)));
          base64string.setCharAt(j+3, (char)61);
        }
      }
      else
      {
        base64string.setCharAt(j+1, base64key.charAt(((text0 & 3) << 4)));
        base64string.setCharAt(j+2, (char)61);
        base64string.setCharAt(j+3, (char)61);
      }

      i += 3;
      j += 4;

    }

    return (base64string.toString());
  }
}
