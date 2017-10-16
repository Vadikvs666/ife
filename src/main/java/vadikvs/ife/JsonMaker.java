/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author vadim
 */
public class JsonMaker {
    
    private List<ProductEntity> list;
    private String json;

    public String getJson() {
        return json;
    }

    public JsonMaker(List<ProductEntity> list) {
        this.list = list;
        this.json = getJSONArrayString(list);
    }
    public  String getJSONArrayString(List<ProductEntity> list){
        String result="";
        JSONArray jsonArray = new JSONArray();
        for(ProductEntity e : list){
            JSONObject obj = new JSONObject();
            obj = this.getJSON(e);
            jsonArray.add(obj);
        }
        result = jsonArray.toJSONString();
        return result;
    }
    private String getJSONString(ProductEntity product){
        String result="";
        JSONObject obj = new JSONObject();
        obj.put("title", product.getName());
        obj.put("artikul", product.getArticul());
        obj.put("price", product.getPrice());
        obj.put("newprice", "0");
        obj.put("count", product.getCount());
        result=obj.toJSONString();
        return result;
    }
    private JSONObject getJSON(ProductEntity product){
        JSONObject obj = new JSONObject();
        obj.put("title", product.getName());
        obj.put("artikul", product.getArticul());
        obj.put("price", product.getPrice());
        obj.put("newprice", "0");
        obj.put("count", product.getCount());
        return obj;
    }
}
