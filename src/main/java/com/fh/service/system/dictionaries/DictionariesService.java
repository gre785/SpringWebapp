
package com.fh.service.system.dictionaries;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;

@Service("dictionariesService")
public class DictionariesService
{

    @Resource(name = "daoSupport")
    private DaoSupport _dao;

    public void save(PageData pd)
        throws Exception
    {
        _dao.save("DictionariesMapper.save", pd);
    }

    public void edit(PageData pd)
        throws Exception
    {
        _dao.update("DictionariesMapper.edit", pd);
    }

    public PageData findById(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("DictionariesMapper.findById", pd);
    }

    public PageData findCount(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("DictionariesMapper.findCount", pd);
    }

    public PageData findBmCount(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("DictionariesMapper.findBmCount", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> dictlistPage(Page page)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("DictionariesMapper.dictlistPage", page);
    }

    public void delete(PageData pd)
        throws Exception
    {
        _dao.delete("DictionariesMapper.delete", pd);

    }
}
