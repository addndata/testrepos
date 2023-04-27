package com.project.admin.model;

public class GroupMaster {

	private int group_id;
	private String group_name;
	private String group_desc;
	private int is_active;
	private String group_prefix;
	private String group_status;
	private int group_login;
	private int created_by;
	private String created_on;
	
	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getGroup_desc() {
		return group_desc;
	}
	public void setGroup_desc(String group_desc) {
		this.group_desc = group_desc;
	}
	public String getGroup_status() {
		return group_status;
	}
	public void setGroup_status(String group_status) {
		this.group_status = group_status;
	}
	public int getIs_active() {
		return is_active;
	}
	public void setIs_active(int is_active) {
		this.is_active = is_active;
	}
	public String getGroup_prefix() {
		return group_prefix;
	}
	public void setGroup_prefix(String group_prefix) {
		this.group_prefix = group_prefix;
	}
	public int getGroup_login() {
		return group_login;
	}
	public void setGroup_login(int group_login) {
		this.group_login = group_login;
	}
	public int getCreated_by() {
		return created_by;
	}
	public void setCreated_by(int created_by) {
		this.created_by = created_by;
	}
	public String getCreated_on() {
		return created_on;
	}
	public void setCreated_on(String created_on) {
		this.created_on = created_on;
	}
	
	public class RoleMaster
	{
		private int role_id;
		private int group_id;
		private String role_name;
		private String role_desc;
		private String role_status;
		private int is_active;
		private String role_prefix;
		private int role_login;
		private int created_by;
		private String created_on;
		
		
		public int getRole_id() {
			return role_id;
		}
		public void setRole_id(int role_id) {
			this.role_id = role_id;
		}
		public int getGroup_id() {
			return group_id;
		}
		public void setGroup_id(int group_id) {
			this.group_id = group_id;
		}
		public String getRole_name() {
			return role_name;
		}
		public void setRole_name(String role_name) {
			this.role_name = role_name;
		}
		public String getRole_desc() {
			return role_desc;
		}
		public void setRole_desc(String role_desc) {
			this.role_desc = role_desc;
		}
		public String getRole_status() {
			return role_status;
		}
		public void setRole_status(String role_status) {
			this.role_status = role_status;
		}
		public int getIs_active() {
			return is_active;
		}
		public void setIs_active(int is_active) {
			this.is_active = is_active;
		}
		public String getRole_prefix() {
			return role_prefix;
		}
		public void setRole_prefix(String role_prefix) {
			this.role_prefix = role_prefix;
		}
		public int getRole_login() {
			return role_login;
		}
		public void setRole_login(int role_login) {
			this.role_login = role_login;
		}
		public int getCreated_by() {
			return created_by;
		}
		public void setCreated_by(int created_by) {
			this.created_by = created_by;
		}
		public String getCreated_on() {
			return created_on;
		}
		public void setCreated_on(String created_on) {
			this.created_on = created_on;
		}	
	}
	
	public class ModuleMaster
	{
		private int module_id;
		private String module_title;
		private String module_url;
		private int parent_id;
		private int module_order;
		private String module_status;
		
		public int getModule_id() {
			return module_id;
		}
		public void setModule_id(int module_id) {
			this.module_id = module_id;
		}
		public String getModule_title() {
			return module_title;
		}
		public void setModule_title(String module_title) {
			this.module_title = module_title;
		}
		public String getModule_url() {
			return module_url;
		}
		public void setModule_url(String module_url) {
			this.module_url = module_url;
		}
		public int getParent_id() {
			return parent_id;
		}
		public void setParent_id(int parent_id) {
			this.parent_id = parent_id;
		}
		public int getModule_order() {
			return module_order;
		}
		public void setModule_order(int module_order) {
			this.module_order = module_order;
		}
		public String getModule_status() {
			return module_status;
		}
		public void setModule_status(String module_status) {
			this.module_status = module_status;
		}
		
	}
	public class RoleModuleRelation
	{
		private int role_id;
		private String role_name;
		private String role_status;
		private String relation_status;
		private int module_id;
		private String module_title;
		private String module_status;
		
		public int getRole_id() {
			return role_id;
		}
		public void setRole_id(int role_id) {
			this.role_id = role_id;
		}
		public String getRole_name() {
			return role_name;
		}
		public void setRole_name(String role_name) {
			this.role_name = role_name;
		}
		public String getRole_status() {
			return role_status;
		}
		public void setRole_status(String role_status) {
			this.role_status = role_status;
		}
		public String getRelation_status() {
			return relation_status;
		}
		public void setRelation_status(String relation_status) {
			this.relation_status = relation_status;
		}
		public int getModule_id() {
			return module_id;
		}
		public void setModule_id(int module_id) {
			this.module_id = module_id;
		}
		public String getModule_title() {
			return module_title;
		}
		public void setModule_title(String module_title) {
			this.module_title = module_title;
		}
		public String getModule_status() {
			return module_status;
		}
		public void setModule_status(String module_status) {
			this.module_status = module_status;
		}
	}
	
	public class MethodMaster
	{
		private int module_id;
		private String module_title;
		private String module_status;
		private int method_id;
		private String method_title;
		private String method_status;
		private String method_name;
		
		public int getModule_id() {
			return module_id;
		}
		public void setModule_id(int module_id) {
			this.module_id = module_id;
		}
		public String getModule_title() {
			return module_title;
		}
		public void setModule_title(String module_title) {
			this.module_title = module_title;
		}
		public String getModule_status() {
			return module_status;
		}
		public void setModule_status(String module_status) {
			this.module_status = module_status;
		}
		public int getMethod_id() {
			return method_id;
		}
		public void setMethod_id(int method_id) {
			this.method_id = method_id;
		}
		public String getMethod_title() {
			return method_title;
		}
		public void setMethod_title(String method_title) {
			this.method_title = method_title;
		}
		public String getMethod_status() {
			return method_status;
		}
		public void setMethod_status(String method_status) {
			this.method_status = method_status;
		}
		public String getMethod_name() {
			return method_name;
		}
		public void setMethod_name(String method_name) {
			this.method_name = method_name;
		}
		
	}
	
	public class RoleMethodRelation
	{
		private int role_id;
		private String role_name;
		private String role_status;
		private String relation_status;
		private int method_id;
		private String method_title;
		private String method_status;
		
		public int getRole_id() {
			return role_id;
		}
		public void setRole_id(int role_id) {
			this.role_id = role_id;
		}
		public String getRole_name() {
			return role_name;
		}
		public void setRole_name(String role_name) {
			this.role_name = role_name;
		}
		public String getRole_status() {
			return role_status;
		}
		public void setRole_status(String role_status) {
			this.role_status = role_status;
		}
		public String getRelation_status() {
			return relation_status;
		}
		public void setRelation_status(String relation_status) {
			this.relation_status = relation_status;
		}
		public int getMethod_id() {
			return method_id;
		}
		public void setMethod_id(int method_id) {
			this.method_id = method_id;
		}
		public String getMethod_title() {
			return method_title;
		}
		public void setMethod_title(String method_title) {
			this.method_title = method_title;
		}
		public String getMethod_status() {
			return method_status;
		}
		public void setMethod_status(String method_status) {
			this.method_status = method_status;
		}
		
	}
	
}
