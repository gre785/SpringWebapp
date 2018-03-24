
package com.fh.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class PageData
    extends HashMap
    implements Map
{
    private static final long serialVersionUID = 1L;

    Map _map = null;
    HttpServletRequest _request;
    public PageData(HttpServletRequest request)
    {
        _request = request;
        Map properties = request.getParameterMap();
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry)entries.next();
            name = (String)entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[])valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        _map = returnMap;
    }

    public PageData()
    {
        _map = new HashMap();
    }

    @Override
    public Object get(Object key)
    {
        Object obj = null;
        if (_map.get(key) instanceof Object[]) {
            Object[] arr = (Object[])_map.get(key);
            obj = _request == null ? arr : (_request.getParameter((String)key) == null ? arr : arr[0]);
        } else {
            obj = _map.get(key);
        }
        return obj;
    }

    public String getString(Object key)
    {
        return (String)get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object put(Object key, Object value)
    {
        return _map.put(key, value);
    }

    @Override
    public Object remove(Object key)
    {
        return _map.remove(key);
    }

    public void clear()
    {
        _map.clear();
    }

    public boolean containsKey(Object key)
    {
        return _map.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return _map.containsValue(value);
    }

    public Set entrySet()
    {
        return _map.entrySet();
    }

    public boolean isEmpty()
    {
        return _map.isEmpty();
    }

    public Set keySet()
    {
        return _map.keySet();
    }

    @SuppressWarnings("unchecked")
    public void putAll(Map t)
    {
        _map.putAll(t);
    }

    public int size()
    {
        return _map.size();
    }

    public Collection values()
    {
        return _map.values();
    }

}
