package org.aspd.message;

/**
 * @author olozynskyy
 * @since 3.7.0
 */
public class TextMessage implements Message
{
  private String message = null;
  private String fileExtension = null;

  @Override
  public byte[] getBytes()
  {
    return new byte[0];
  }
}
