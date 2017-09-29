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
        this.articul = articul;
        this.name = name;
        this.count = Integer.valueOf(count);
        this.price = Integer.valueOf(summ)/Integer.valueOf(count);
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
