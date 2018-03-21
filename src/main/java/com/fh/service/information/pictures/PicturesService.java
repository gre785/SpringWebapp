
package com.fh.service.information.pictures;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;

@Service("picturesService")
public class PicturesService
{

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    /*
    * add
    */
    public void save(PageData pd)
        throws Exception
    {
        dao.save("PicturesMapper.save", pd);
    }

    /*
    * delete
    */
    public void delete(PageData pd)
        throws Exception
    {
        dao.delete("PicturesMapper.delete", pd);
    }

    /*
    * update
    */
    public void edit(PageData pd)
        throws Exception
    {
        dao.update("PicturesMapper.edit", pd);
    }

    /*
    * find list
    */
    public List<PageData> list(Page page)
        throws Exception
    {
        return (List<PageData>)dao.findForList("PicturesMapper.datalistPage", page);
    }

    /*
    * find all
    */
    public List<PageData> listAll(PageData pd)
        throws Exception
    {
        return (List<PageData>)dao.findForList("PicturesMapper.listAll", pd);
    }

    /*
    * find through ID
    */
    public PageData findById(PageData pd)
        throws Exception
    {
        return (PageData)dao.findForObject("PicturesMapper.findById", pd);
    }

    /*
    *  delete all
    */
    public void deleteAll(String[] ArrayDATA_IDS)
        throws Exception
    {
        dao.delete("PicturesMapper.deleteAll", ArrayDATA_IDS);
    }

    /*
    * bulk get
    */
    public List<PageData> getAllById(String[] ArrayDATA_IDS)
        throws Exception
    {
        return (List<PageData>)dao.findForList("PicturesMapper.getAllById", ArrayDATA_IDS);
    }

    /*
    * delete image
    */
    public void delTp(PageData pd)
        throws Exception
    {
        dao.update("PicturesMapper.delTp", pd);
    }

}
