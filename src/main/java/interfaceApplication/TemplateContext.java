package interfaceApplication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.database.dbFilter;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.string.StringHelper;

public class TemplateContext {
	
    private GrapeTreeDBModel tempContext;
    private String pkString;
    
    public TemplateContext() {
    	
        tempContext = new GrapeTreeDBModel();
        
        //数据模型
      	GrapeDBDescriptionModel  gDbSpecField = new GrapeDBDescriptionModel ();
        gDbSpecField.importDescription(appsProxy.tableConfig("TemplateContext"));
        tempContext.descriptionModel(gDbSpecField);
        
        //权限模型绑定
  		GrapePermissionsModel gperm = new GrapePermissionsModel();
  		gperm.importDescription(appsProxy.tableConfig("TemplateContext"));
  		tempContext.permissionsModel(gperm);
        
  		pkString = tempContext.getPk();
  		
        //开启权限检查
        tempContext.enableCheck();

    }

    /**
     * 新增模版
     * 
     * @param tempinfo
     * @return
     */
    public String TempInsert(String tempinfo) {
        Object info = null;
        String result = rMsg.netMSG(100, "新增模版失败");
        if (!StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject object = JSONObject.toJSON(tempinfo);
        if (object != null && object.size() > 0) {
            info = tempContext.data(object).insertEx();
        }
        return info != null ? rMsg.netMSG(0, "新增模版成功") : result;
    }

    /**
     * 删除模版
     * 
     * @param tempID
     * @return
     */
    public String TempDelete(String tempID) {
        return TempBatchDelete(tempID);
    }

    public String TempBatchDelete(String tempid) {
        long code = 0;
        String[] value = null;
        String result = rMsg.netMSG(100, "删除失败");
        if (!StringHelper.InvaildString(tempid)) {
            value = tempid.split(",");
        }
        if (tempid != null) {
            tempContext.or();
            for (String id : value) {
                tempContext.eq(pkString, id);
            }
            code = tempContext.deleteAll();
        }
        return code > 0 ? rMsg.netMSG(0, "删除成功") : result;
    }

    /**
     * 更新模版
     * 
     * @param tempid
     * @param tempinfo
     * @return
     */
    public String TempUpdate(String tempid, String tempinfo) {
        boolean code = false;
        String result = rMsg.netMSG(100, "模版更新失败");
        if (!StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject object = JSONObject.toJSON(tempinfo);
        if (object != null && object.size() > 0) {
            code = tempContext.eq(pkString, tempid).data(object).updateEx();
        }
        return result = code ? rMsg.netMSG(0, "模版更新成功") : result;
    }

    /**
     * 分页显示模版
     * 
     * @param idx
     * @param pageSize
     * @return
     */
    public String TempPage(int idx, int pageSize) {
        return TempPageBy(idx, pageSize, null);
    }

    public String TempPageBy(int idx, int pageSize, String tempinfo) {
        long total = 0;
        if (!StringHelper.InvaildString(tempinfo)) {
            JSONArray condArray = buildCond(tempinfo);
            if (condArray != null && condArray.size() > 0) {
                tempContext.where(condArray);
            } else {
                return rMsg.netPAGE(idx, pageSize, total, new JSONArray());
            }
        }
        JSONArray array = tempContext.dirty().page(idx, pageSize);
        total = tempContext.count();
        return rMsg.netPAGE(idx, pageSize, total, (array != null && array.size() > 0) ? array : new JSONArray());
    }

    /**
     * 根据类型查询模版信息
     * 
     * @param tempinfo
     * @return
     */
    public String TempFindByType(String tempinfo) {
        JSONArray array = null;
        if (!StringHelper.InvaildString(tempinfo)) {
            JSONArray condArray = buildCond(tempinfo);
            if (condArray != null && condArray.size() > 0) {
                array = tempContext.where(condArray).select();
            }
        }
        return rMsg.netMSG(true, (array != null && array.size() > 0) ? array : new JSONArray());
    }

    /**
     * 获取模版名称
     * @param tids
     * @return {tid:name,tid:name}
     */
    @SuppressWarnings("unchecked")
    public String TempFindByTid(String tids) {
        String[] value = null;
        JSONArray array = null;
        JSONObject rObject = null, tempObj;
        if (!StringHelper.InvaildString(tids)) {
            value = tids.split(",");
        }
        if (value != null) {
            tempContext.or();
            for (String tid : value) {
                tempContext.eq(pkString, tid);
            }
            array = tempContext.field("_id,name").select();
        }
        if (array != null && array.size() > 0) {
            rObject = new JSONObject();
            for (Object object : array) {
                tempObj = (JSONObject) object;
                rObject.put(tempObj.getPkValue(pkString), tempObj.getString("name"));
            }
        }
        return (rObject != null && rObject.size() > 0) ? rObject.toJSONString() : null;
    }
    
    /**
     * 整合参数，将JSONObject类型的参数封装成JSONArray类型
     * 
     * @param object
     * @return
     */
    public JSONArray buildCond(String Info) {
        String key;
        Object value;
        JSONArray condArray = null;
        if(StringHelper.InvaildString(Info)){// TODO 1
        	return null;
        }
        JSONObject object = JSONObject.toJSON(Info);
        dbFilter filter = new dbFilter();
        if (object != null && object.size() > 0) {
            for (Object object2 : object.keySet()) {
                key = object2.toString();
                value = object.get(key);
                filter.eq(key, value);
            }
            condArray = filter.build();
        } else {
            condArray = JSONArray.toJSONArray(Info);
        }
        return condArray;
    }
}
