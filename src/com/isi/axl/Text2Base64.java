package com.isi.axl;

public class Text2Base64 {

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
