/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        String start=server+"/admin/inputdocs/manual"+"?";
        String request="";
        for(ProductEntity entity:list){
            request+="products[]=";
            request+=getJSONString(entity);
            request+="&";
        }
        
        request+="addition="+addition;
        String res="";
        try {
            res=new URI("http", server, "/admin/inputdocs/manual", request, "").toString();
        } catch (URISyntaxException ex) {
            Logger.getLogger(RequestMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(res);
        return res;
    }
    
    public String getStringWithHash(String hash){
        String start=server+"/admin/inputdocs/manual"+"?";
        String request="";       
        request+="hash="+hash;
        String res="";
        try {
            res=new URI("http", server, "/admin/inputdocs/manual", request, "").toString();
        } catch (URISyntaxException ex) {
            Logger.getLogger(RequestMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(res);
        return res;
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
