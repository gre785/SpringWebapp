
package com.fh.service.system.user;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.entity.system.User;
import com.fh.util.PageData;

@Service("userService")
public class UserService
{

    @Resource(name = "daoSupport")
    private DaoSupport _dao;

    public PageData findByUiId(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("UserXMapper.findByUiId", pd);
    }

    public PageData findByUId(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("UserXMapper.findByUId", pd);
    }

    public PageData findByUE(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("UserXMapper.findByUE", pd);
    }

    public PageData findByUN(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("UserXMapper.findByUN", pd);
    }

    public void saveU(PageData pd)
        throws Exception
    {
        _dao.save("UserXMapper.saveU", pd);
    }

    public void editU(PageData pd)
        throws Exception
    {
        _dao.update("UserXMapper.editU", pd);
    }

    public void setSkin(PageData pd)
        throws Exception
    {
        _dao.update("UserXMapper.setSKIN", pd);
    }

    public void deleteU(PageData pd)
        throws Exception
    {
        _dao.delete("UserXMapper.deleteU", pd);
    }

    public void deleteAllU(String[] USER_IDS)
        throws Exception
    {
        _dao.delete("UserXMapper.deleteAllU", USER_IDS);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listPdPageUser(Page page)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("UserXMapper.userlistPage", page);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAllUser(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("UserXMapper.listAllUser", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listGPdPageUser(Page page)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("UserXMapper.userGlistPage", page);
    }

    public void saveIP(PageData pd)
        throws Exception
    {
        _dao.update("UserXMapper.saveIP", pd);
    }

    public PageData getUserByNameAndPwd(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("UserXMapper.getUserInfo", pd);
    }

    public void updateLastLogin(PageData pd)
        throws Exception
    {
        _dao.update("UserXMapper.updateLastLogin", pd);
    }

    public User getUserAndRoleById(String USER_ID)
        throws Exception
    {
        return (User)_dao.findForObject("UserMapper.getUserAndRoleById", USER_ID);
    }

}
