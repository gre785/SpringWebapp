
package com.fh.service.system.role;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.system.Role;
import com.fh.util.PageData;

@Service("roleService")
public class RoleService
{

    @Resource(name = "daoSupport")
    private DaoSupport _dao;

    @SuppressWarnings("unchecked")
    public List<Role> listAllERRoles()
        throws Exception
    {
        return (List<Role>)_dao.findForList("RoleMapper.listAllERRoles", null);

    }

    @SuppressWarnings("unchecked")
    public List<Role> listAllappERRoles()
        throws Exception
    {
        return (List<Role>)_dao.findForList("RoleMapper.listAllappERRoles", null);
    }

    @SuppressWarnings("unchecked")
    public List<Role> listAllRoles()
        throws Exception
    {
        return (List<Role>)_dao.findForList("RoleMapper.listAllRoles", null);

    }

    public PageData findGLbyrid(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("RoleMapper.findGLbyrid", pd);
    }

    public PageData findYHbyrid(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("RoleMapper.findYHbyrid", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAllUByRid(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("RoleMapper.listAllUByRid", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAllAppUByRid(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("RoleMapper.listAllAppUByRid", pd);
    }

    @SuppressWarnings("unchecked")
    public List<Role> listAllRolesByPId(PageData pd)
        throws Exception
    {
        return (List<Role>)_dao.findForList("RoleMapper.listAllRolesByPId", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAllkefu(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("RoleMapper.listAllkefu", pd);
    }

    @SuppressWarnings("unchecked")
    public List<PageData> listAllGysQX(PageData pd)
        throws Exception
    {
        return (List<PageData>)_dao.findForList("RoleMapper.listAllGysQX", pd);
    }

    public void deleteKeFuById(String ROLE_ID)
        throws Exception
    {
        _dao.delete("RoleMapper.deleteKeFuById", ROLE_ID);
    }

    public void deleteGById(String ROLE_ID)
        throws Exception
    {
        _dao.delete("RoleMapper.deleteGById", ROLE_ID);
    }

    public void deleteRoleById(String ROLE_ID)
        throws Exception
    {
        _dao.delete("RoleMapper.deleteRoleById", ROLE_ID);
    }

    public Role getRoleById(String roleId)
        throws Exception
    {
        return (Role)_dao.findForObject("RoleMapper.getRoleById", roleId);
    }

    public void updateRoleRights(Role role)
        throws Exception
    {
        _dao.update("RoleMapper.updateRoleRights", role);
    }

    public void updateQx(String msg, PageData pd)
        throws Exception
    {
        _dao.update("RoleMapper." + msg, pd);
    }

    public void updateKFQx(String msg, PageData pd)
        throws Exception
    {
        _dao.update("RoleMapper." + msg, pd);
    }

    public void gysqxc(String msg, PageData pd)
        throws Exception
    {
        _dao.update("RoleMapper." + msg, pd);
    }

    public void setAllRights(PageData pd)
        throws Exception
    {
        _dao.update("RoleMapper.setAllRights", pd);

    }

    public void add(PageData pd)
        throws Exception
    {
        _dao.findForList("RoleMapper.insert", pd);
    }

    public void saveKeFu(PageData pd)
        throws Exception
    {
        _dao.findForList("RoleMapper.saveKeFu", pd);
    }

    public void saveGYSQX(PageData pd)
        throws Exception
    {
        _dao.findForList("RoleMapper.saveGYSQX", pd);
    }

    public PageData findObjectById(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("RoleMapper.findObjectById", pd);
    }

    public PageData edit(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("RoleMapper.edit", pd);
    }

}
