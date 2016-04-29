package com.xinyusoft.projectlist.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig appBeanDaoConfig;

    private final AppBeanDao appBeanDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        appBeanDaoConfig = daoConfigMap.get(AppBeanDao.class).clone();
        appBeanDaoConfig.initIdentityScope(type);

        appBeanDao = new AppBeanDao(appBeanDaoConfig, this);

        registerDao(AppBean.class, appBeanDao);
    }
    
    public void clear() {
        appBeanDaoConfig.getIdentityScope().clear();
    }

    public AppBeanDao getAppBeanDao() {
        return appBeanDao;
    }

}
