
package com.fh.service.system.menu;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.system.Menu;
import com.fh.util.PageData;

@Service("menuService")
public class MenuService
{

    @Resource(name = "daoSupport")
    private DaoSupport _dao;

    public void deleteMenuById(String MENU_ID)
        throws Exception
    {
        _dao.save("MenuMapper.deleteMenuById", MENU_ID);

    }

    public PageData getMenuById(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("MenuMapper.getMenuById", pd);

    }

    public PageData findMaxId(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("MenuMapper.findMaxId", pd);

    }

    @SuppressWarnings("unchecked")
    public List<Menu> listAllParentMenu()
        throws Exception
    {
        return (List<Menu>)_dao.findForList("MenuMapper.listAllParentMenu", null);

    }

    public void saveMenu(Menu menu)
        throws Exception
    {
        if (menu.getMENU_ID() != null && menu.getMENU_ID() != "") {
            // dao.update("MenuMapper.updateMenu", menu);
            _dao.save("MenuMapper.insertMenu", menu);
        } else {
            _dao.save("MenuMapper.insertMenu", menu);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listSubMenuByParentId(String parentId)
        throws Exception
    {
        return (List<Menu>)_dao.findForList("MenuMapper.listSubMenuByParentId", parentId);

    }

    public List<Menu> listAllMenu()
        throws Exception
    {
        List<Menu> rl = this.listAllParentMenu();
        for (Menu menu : rl) {
            List<Menu> subList = this.listSubMenuByParentId(menu.getMENU_ID());
            menu.setSubMenu(subList);
        }
        return rl;
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listAllSubMenu()
        throws Exception
    {
        return (List<Menu>)_dao.findForList("MenuMapper.listAllSubMenu", null);

    }

    public PageData edit(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("MenuMapper.updateMenu", pd);
    }

    public PageData editicon(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("MenuMapper.editicon", pd);
    }

    public PageData editType(PageData pd)
        throws Exception
    {
        return (PageData)_dao.findForObject("MenuMapper.editType", pd);
    }
}
