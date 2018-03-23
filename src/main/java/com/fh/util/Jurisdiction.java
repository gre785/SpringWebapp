
package com.fh.util;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.fh.entity.system.Menu;

public class Jurisdiction
{

    @SuppressWarnings("unchecked")
    public static boolean hasJurisdiction(String menuUrl)
    {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        Boolean b = true;
        List<Menu> menuList = (List<Menu>)session.getAttribute(Const.SESSION_allmenuList);

        for (int i = 0; i < menuList.size(); i++) {
            for (int j = 0; j < menuList.get(i).getSubMenu().size(); j++) {
                if (menuList.get(i).getSubMenu().get(j).getMENU_URL().split(".do")[0].equals(menuUrl.split(".do")[0])) {
                    if (!menuList.get(i).getSubMenu().get(j).isHasMenu()) {
                        return false;
                    } else {
                        Map<String, String> map = (Map<String, String>)session.getAttribute(Const.SESSION_QX);
                        map.remove("add");
                        map.remove("del");
                        map.remove("edit");
                        map.remove("cha");
                        String MENU_ID = menuList.get(i).getSubMenu().get(j).getMENU_ID();
                        String USERNAME = session.getAttribute(Const.SESSION_USERNAME).toString();
                        Boolean isAdmin = "admin".equals(USERNAME);
                        map.put("add", (RightsHelper.testRights(map.get("adds"), MENU_ID)) || isAdmin ? "1" : "0");
                        map.put("del", RightsHelper.testRights(map.get("dels"), MENU_ID) || isAdmin ? "1" : "0");
                        map.put("edit", RightsHelper.testRights(map.get("edits"), MENU_ID) || isAdmin ? "1" : "0");
                        map.put("cha", RightsHelper.testRights(map.get("chas"), MENU_ID) || isAdmin ? "1" : "0");
                        session.removeAttribute(Const.SESSION_QX);
                        session.setAttribute(Const.SESSION_QX, map);
                    }
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean buttonJurisdiction(String menuUrl, String type)
    {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        Boolean b = true;
        List<Menu> menuList = (List<Menu>)session.getAttribute(Const.SESSION_allmenuList);

        for (int i = 0; i < menuList.size(); i++) {
            for (int j = 0; j < menuList.get(i).getSubMenu().size(); j++) {
                if (menuList.get(i).getSubMenu().get(j).getMENU_URL().split(".do")[0].equals(menuUrl.split(".do")[0])) {
                    if (!menuList.get(i).getSubMenu().get(j).isHasMenu()) {
                        return false;
                    } else {
                        Map<String, String> map = (Map<String, String>)session.getAttribute(Const.SESSION_QX);
                        String MENU_ID = menuList.get(i).getSubMenu().get(j).getMENU_ID();
                        String USERNAME = session.getAttribute(Const.SESSION_USERNAME).toString();
                        Boolean isAdmin = "admin".equals(USERNAME);
                        if ("add".equals(type)) {
                            return ((RightsHelper.testRights(map.get("adds"), MENU_ID)) || isAdmin);
                        } else if ("del".equals(type)) {
                            return ((RightsHelper.testRights(map.get("dels"), MENU_ID)) || isAdmin);
                        } else if ("edit".equals(type)) {
                            return ((RightsHelper.testRights(map.get("edits"), MENU_ID)) || isAdmin);
                        } else if ("cha".equals(type)) {
                            return ((RightsHelper.testRights(map.get("chas"), MENU_ID)) || isAdmin);
                        }
                    }
                }
            }
        }
        return true;
    }

}
