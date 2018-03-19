
package com.fh.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository("daoSupport")
public class DaoSupport
    implements DAO
{

    @Resource(name = "sqlSessionTemplate")
    private SqlSessionTemplate _sqlSessionTemplate;

    public Object save(String statement, Object parameter)
        throws Exception
    {
        return _sqlSessionTemplate.insert(statement, parameter);
    }

    public Object batchSave(String statement, List<Object> parameters)
        throws Exception
    {
        return _sqlSessionTemplate.insert(statement, parameters);
    }

    public Object update(String statement, Object parameter)
        throws Exception
    {
        return _sqlSessionTemplate.update(statement, parameter);
    }

    public void batchUpdate(String statement, List<Object> parameters)
        throws Exception
    {
        SqlSessionFactory sqlSessionFactory = _sqlSessionTemplate.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            for (Object parameter : parameters) {
                sqlSession.update(statement, parameter);
            }
            sqlSession.flushStatements();
            sqlSession.commit();
            sqlSession.clearCache();
        } finally {
            sqlSession.close();
        }
    }

    public Object batchDelete(String statement, List<Object> parameters)
        throws Exception
    {
        return _sqlSessionTemplate.delete(statement, parameters);
    }

    public Object delete(String statement, Object parameter)
        throws Exception
    {
        return _sqlSessionTemplate.delete(statement, parameter);
    }

    public Object findForObject(String statement, Object parameter)
        throws Exception
    {
        return _sqlSessionTemplate.selectOne(statement, parameter);
    }

    public Object findForList(String statement, Object parameter)
        throws Exception
    {
        return _sqlSessionTemplate.selectList(statement, parameter);
    }

    public Object findForMap(String statement, Object parameter, String key)
        throws Exception
    {
        return _sqlSessionTemplate.selectMap(statement, parameter, key);
    }

}
