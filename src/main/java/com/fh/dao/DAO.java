
package com.fh.dao;

public interface DAO
{
    public Object save(String statement, Object parameter)
        throws Exception;

    public Object update(String statement, Object parameter)
        throws Exception;

    public Object delete(String statement, Object parameter)
        throws Exception;

    public Object findForObject(String statement, Object parameter)
        throws Exception;

    public Object findForList(String statement, Object parameter)
        throws Exception;

    public Object findForMap(String statement, Object parameter, String key)
        throws Exception;

}
