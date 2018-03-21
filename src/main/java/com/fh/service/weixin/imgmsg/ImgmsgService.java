
package com.fh.service.weixin.imgmsg;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;

@Service("imgmsgService")
public class ImgmsgService
{

    @Resource(name = "daoSupport")
    private DaoSupport _dao;

    public void save(PageData pd)
        throws Exception
    {
        _dao.save("ImgmsgMapper.save", pd);
    }

    public void delete(PageData pd)
        throws Exception
    {
        _dao.delete("ImgmsgMapper.delete", pd);
    }

    public void edit(PageData pd)
        throws Exception
    {
        _dao.update("ImgmsgMapper.edit", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("ImgmsgMapper.datalistPage", page);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("ImgmsgMapper.listAll", pd);
    }

    public PageData findById(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("ImgmsgMapper.findById", pd);
    }

    public void deleteAll(String[] ArrayDATA_IDS)
        throws Exception
    {
        _dao.delete("ImgmsgMapper.deleteAll", ArrayDATA_IDS);
    }

    public PageData findByKw(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("ImgmsgMapper.findByKw", pd);
    }
}
