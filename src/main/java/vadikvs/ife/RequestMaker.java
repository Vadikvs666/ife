/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author vadim
 */
public class RequestMaker {
    
    private List<ProductEntity> list;
    private String server;
    private String addition;

    public RequestMaker(List<ProductEntity> list, String server,String addition) {
        this.list = list;
        this.server = server;
        this.addition=addition;
    }
    
    public String getStringRequest(){
        String request="http://"+server+"/?";
        for(ProductEntity entity:list){
            request+="products[]=";
            request+=getJSONString(entity);
            request+="&";
        }
        request+="addition="+addition;
        return request;
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
    
}
