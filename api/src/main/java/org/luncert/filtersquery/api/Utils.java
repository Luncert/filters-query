package org.luncert.filtersquery.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

  private static final char NUL = '\0';

  public static boolean isEmpty(final CharSequence cs) {
    return cs == null || cs.isEmpty();
  }

  public static String unwrap(final String str, final char wrapChar) {
    if (isEmpty(str) || wrapChar == NUL || str.length() == 1) {
      return str;
    }

    if (str.charAt(0) == wrapChar && str.charAt(str.length() - 1) == wrapChar) {
      final int startIndex = 0;
      final int endIndex = str.length() - 1;

      return str.substring(startIndex + 1, endIndex);
    }

    return str;
  }
}
