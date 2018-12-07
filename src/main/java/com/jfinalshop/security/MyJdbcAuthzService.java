package com.jfinalshop.security;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jfinalshop.model.Permission;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.PermissionService;
import com.jfinalshop.service.RoleService;
import com.jfinalshop.shiro.core.JdbcAuthzService;
import com.jfinalshop.shiro.core.handler.AuthzHandler;
import com.jfinalshop.shiro.core.handler.JdbcPermissionAuthzHandler;
import com.jfinalshop.util.HasorUtils;

/**
 * Created by wangrenhui on 14-1-7.
 */
public class MyJdbcAuthzService implements JdbcAuthzService {
	
  private RoleService roleService = HasorUtils.getBean(RoleService.class);
  private PermissionService permissionService = HasorUtils.getBean(PermissionService.class);
  
  @Override
  public Map<String, AuthzHandler> getJdbcAuthz() {
    //加载数据库的url配置
    //按长度倒序排列
    Map<String, AuthzHandler> authzJdbcMaps = Collections.synchronizedMap(new TreeMap<String, AuthzHandler>(new Comparator<String>() {
          @Override
          public int compare(String k1, String k2) {
            int result = k2.length() - k1.length();
            if (result == 0) {
              return k1.compareTo(k2);
            }
            return result;
          }
        }));
    //遍历角色
    List<Role> roles = roleService.findAll();
    for (Role role : roles) {
      //角色可用
      if (role.getIsEnabled() == true) {
    	List<Permission> permissions = permissionService.findByRole(role);
        //遍历权限
        for (Permission permission : permissions) {
          //权限可用
          if (permission.getIsEnabled() == true) {
            if (permission.getUrl() != null && !permission.getUrl().isEmpty()) {
            	authzJdbcMaps.put(permission.getUrl(), new JdbcPermissionAuthzHandler(permission.getValue()));
            }
          } 
        }
      }
    }
    return authzJdbcMaps;
  }
}
