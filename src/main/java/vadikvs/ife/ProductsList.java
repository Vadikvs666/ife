/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vadim
 */
public class ProductsList {

    private List<ProductEntity> list;

    public ProductsList() {
        list = new ArrayList<>();
    }

    public ProductsList(List<ProductEntity> data) {
        list = new ArrayList<>();
        list.addAll(data);
    }

    public void addAll(List<ProductEntity> data) {
        for (ProductEntity entity : data) {
            add(entity);
        }
    }

    public void add(ProductEntity entity) {
        boolean exist = false;
        for (ProductEntity it : list) {
            System.out.println("Текущий товар: " + entity.getName() + " артикул: " + entity.getArticul());
            System.out.println("В списк товар: " + it.getName() + " артикул: " + it.getArticul());
            if (it.getName().equals(entity.getName())) {
                if (it.getArticul().equals(entity.getArticul())) {
                    System.out.println("Совпадение цен и артикула");
                    int count = it.getCount() + entity.getCount();
                    float sum_it = it.getCount() * it.getPrice();
                    float sum_en = entity.getCount() * entity.getPrice();
                    float summ = sum_en + sum_it;
                    it.setCount(count);
                    it.setPrice(summ / count);
                    exist = true;
                }
            }
        }
        if (!exist) {
            list.add(entity);
            System.out.println("Добавляем в список товар... ");
        }
    }

    public List<ProductEntity> get() {
        return list;
    }

}
