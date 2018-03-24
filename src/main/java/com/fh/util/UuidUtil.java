
package com.fh.util;

import java.util.UUID;

public class UuidUtil
{
    public static String get32UUID()
    {
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    public static void main(String[] args)
    {
        System.out.println(get32UUID());
    }
}
