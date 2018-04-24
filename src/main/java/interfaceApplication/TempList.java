package interfaceApplication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.database.dbFilter;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.session.session;
import common.java.string.StringHelper;

public class TempList {
	
    private GrapeTreeDBModel temp;
    private JSONObject userInfo = null;
    private String currentWeb = null;
    private String pkString;

    public TempList() {
    	
        temp = new GrapeTreeDBModel();
        
        //数据模型
      	GrapeDBDescriptionModel  gDbSpecField = new GrapeDBDescriptionModel ();
        gDbSpecField.importDescription(appsProxy.tableConfig("TempList"));
        temp.descriptionModel(gDbSpecField);
        
        //权限模型绑定
  		GrapePermissionsModel gperm = new GrapePermissionsModel();
  		gperm.importDescription(appsProxy.tableConfig("TempList"));
  		temp.permissionsModel(gperm);
  		
  		pkString = temp.getPk();

        //用户信息
        userInfo = (new session()).getDatas();
		if (userInfo != null && userInfo.size() != 0) {
			currentWeb = userInfo.getString("currentWeb"); // 当前用户所属网站id
		}
        //开启权限检查
        temp.enableCheck();
    }

    /**
     * 新增模版方案
     * 
     * @param tempinfo
     * @return
     */
    @SuppressWarnings("unchecked")
    public String TempListInsert(String tempinfo) {
        Object code = 99;
        String result = rMsg.netMSG(100, "新增模版方案失败");
        if (StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject object = JSONObject.toJSON(tempinfo);
        if (object != null && object.size() > 0) {
            if (object.containsKey("wbid")) {
                object.put("wbid", currentWeb);
            }
            code = temp.dataEx(object).insertEx();
        }
        return code != null ? rMsg.netMSG(0, "新增模版方案成功") : result;
    }

    /**
     * 更改模版方案
     * 
     * @param tid
     * @param tempinfo
     * @return
     */
    public String TempListUpdate(String tid, String tempinfo) {
        boolean code = false;
        String result = rMsg.netMSG(100, "更改模版方案失败");
        if (StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject obj = JSONObject.toJSON(tempinfo);
        code = temp.eq(pkString, tid).data(obj).updateEx();
        return result = code ? rMsg.netMSG(0, "更改模版方案成功") : result;
    }

    /**
     * 删除
     * 
     * @param id
     * @return
     */
    public String TempListDelete(String id) {
        return TempListBatchDelete(id);
    }

    public String TempListBatchDelete(String tid) {
        long code = 0;
        String[] value = null;
        String result = rMsg.netMSG(100, "模版方案删除失败");
        if (!StringHelper.InvaildString(tid)) {
            value = tid.split(",");
        }
        if (value != null) {
            temp.or();
            for (String id : value) {
                temp.eq(pkString, id);
            }
            code = temp.deleteAll();
        }
        return code > 0 ? rMsg.netMSG(0, "模版方案删除成功") : result;
    }

    /**
     * 分页显示模版方案
     * 
     * @param idx
     * @param pageSize
     * @return
     */
    public String TempListPage(int idx, int pageSize) {
        return TempListPageBy(idx, pageSize, null);
    }

    public String TempListPageBy(int idx, int pageSize, String tempinfo) {
        long total = 0;
        if (!StringHelper.InvaildString(tempinfo)) {
            JSONArray CondArray = buildCond(tempinfo);
            if (CondArray != null && CondArray.size() > 0) {
                temp.where(CondArray);
            } else {
                return rMsg.netPAGE(idx, pageSize, total, new JSONArray());
            }
        }
        JSONArray array = temp.dirty().page(idx, pageSize);
        total = temp.count();
        return rMsg.netPAGE(idx, pageSize, total, (array != null && array.size() > 0) ? array : new JSONArray());
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
        if(StringHelper.InvaildString(Info)){//TODO 1
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
