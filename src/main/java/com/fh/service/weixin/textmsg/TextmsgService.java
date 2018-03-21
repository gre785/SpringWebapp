
package com.fh.service.weixin.textmsg;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;

@Service("textmsgService")
public class TextmsgService
{

    @Resource(name = "daoSupport")
    private DaoSupport _dao;

    public void save(PageData pd)
        throws Exception
    {
        _dao.save("TextmsgMapper.save", pd);
    }

    public void delete(PageData pd)
        throws Exception
    {
        _dao.delete("TextmsgMapper.delete", pd);
    }

    public void edit(PageData pd)
        throws Exception
    {
        _dao.update("TextmsgMapper.edit", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("TextmsgMapper.datalistPage", page);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("TextmsgMapper.listAll", pd);
    }

    public PageData findById(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("TextmsgMapper.findById", pd);
    }

    public void deleteAll(String[] ArrayDATA_IDS)
        throws Exception
    {
        _dao.delete("TextmsgMapper.deleteAll", ArrayDATA_IDS);
    }

    public PageData findByKw(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("TextmsgMapper.findByKw", pd);
    }
}
