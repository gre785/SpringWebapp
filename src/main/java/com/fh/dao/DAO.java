
package com.fh.dao;

public interface DAO
{
    /**
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return The number of rows affected by the insert.
     * @throws Exception
     */
    public Object save(String statement, Object parameter)
        throws Exception;

    /**
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return The number of rows affected by the update.
     * @throws Exception
     */
    public Object update(String statement, Object parameter)
        throws Exception;

    /**
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return The number of rows affected by the delete.
     * @throws Exception
     */
    public Object delete(String statement, Object parameter)
        throws Exception;

    /**
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return Mapped object
     * @throws Exception
     */
    public Object findForObject(String statement, Object parameter)
        throws Exception;

    /**
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return List of mapped objects
     * @throws Exception
     */
    public Object findForList(String statement, Object parameter)
        throws Exception;

    /**
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param key
     * @return Map of mapped objects
     * @throws Exception
     */
    public Object findForMap(String statement, Object parameter, String key)
        throws Exception;

}
