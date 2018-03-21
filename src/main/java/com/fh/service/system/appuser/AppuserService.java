
package com.fh.service.system.appuser;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;

@Service("appuserService")
public class AppuserService
{

    @Resource(name = "daoSupport")
    private DaoSupport _dao;

    public PageData findByUiId(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("AppuserMapper.findByUiId", pd);
    }

    public PageData findByUId(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("AppuserMapper.findByUId", pd);
    }

    public PageData findByUE(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("AppuserMapper.findByUE", pd);
    }

    public PageData findByUN(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("AppuserMapper.findByUN", pd);
    }

    public void saveU(PageData pd)
        throws Exception
    {
        _dao.save("AppuserMapper.saveU", pd);
    }

    public void editU(PageData pd)
        throws Exception
    {
        _dao.update("AppuserMapper.editU", pd);
    }

    public void deleteU(PageData pd)
        throws Exception
    {
        _dao.delete("AppuserMapper.deleteU", pd);
    }

    public void deleteAllU(String[] USER_IDS)
        throws Exception
    {
        _dao.delete("AppuserMapper.deleteAllU", USER_IDS);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAllUser(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("AppuserMapper.listAllUser", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listPdPageUser(Page page)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("AppuserMapper.userlistPage", page);
    }

    public void saveIP(PageData pd)
        throws Exception
    {
        _dao.update("AppuserMapper.saveIP", pd);
    }

    public PageData getUserByNameAndPwd(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("AppuserMapper.getUserInfo", pd);
    }

    public void updateLastLogin(PageData pd)
        throws Exception
    {
        _dao.update("AppuserMapper.updateLastLogin", pd);
    }
}
