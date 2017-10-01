/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

/**
 *
 * @author vadim
 */
public class ProductEntity {
    private String articul;
    private String name;
    private int count;
    private float price;

    public ProductEntity(String articul, String name, int count, float price) {
        this.articul = articul;
        this.name = name;
        this.count = count;
        this.price = price;
    }
    
    public ProductEntity(String articul, String name, String count, String summ) {
        try{
        this.articul = articul;
        this.name = name;
        float temp=Float.valueOf(count);
        float summ_f =Float.valueOf(summ);
        this.count = Math.round(temp);
        this.price = summ_f/this.count;
        }
        catch(Exception ex){
            this.count=0;
            this.price=0;
        }
    }

    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
